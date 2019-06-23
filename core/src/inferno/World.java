package inferno;

import inferno.world.MapLoader;
import inferno.world.Tile;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.Core;
import io.anuke.arc.maps.MapLayer;
import io.anuke.arc.maps.tiled.TiledMap;
import io.anuke.arc.maps.tiled.TiledMapTileLayer;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.util.Structs;

public class World implements ApplicationListener{
    Tile[][] tiles;
    TiledMap map;
    TiledMapTileLayer floorLayer, wallLayer;
    MapLayer objectLayer;

    public World(){
        map = new MapLoader().load("maps/map.tmx");

        floorLayer = (TiledMapTileLayer) map.getLayers().get("floor");
        wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");
        objectLayer = map.getLayers().get("objects");

        tiles = new Tile[floorLayer.getWidth()][floorLayer.getHeight()];

        for(int x = 0; x < width(); x++){
            for(int y = 0; y < height(); y++){
                tiles[x][y] = new Tile(
                    floorLayer.getCell(x, y) == null ? null : Core.atlas.isFound(floorLayer.getCell(x, y).getTile().getTextureRegion()) ? floorLayer.getCell(x, y).getTile().getTextureRegion() : null,
                    wallLayer.getCell(x, y) == null ? null : Core.atlas.isFound(wallLayer.getCell(x, y).getTile().getTextureRegion()) ? wallLayer.getCell(x, y).getTile().getTextureRegion() : null
                );

                tiles[x][y].solid = wallLayer.getCell(x, y) != null && wallLayer.getCell(x, y).getTile().getProperties().containsKey("solid");
            }
        }
    }

    public int width(){
        return wallLayer.getWidth();
    }

    public int height(){
        return wallLayer.getHeight();
    }

    public boolean solid(int x, int y){
        return tileOpt(x, y) == null || tile(x, y).solid;
    }

    public Tile tile(int x, int y){
        return tiles[Mathf.clamp(x, 0, width() - 1)][Mathf.clamp(y, 0, height() - 1)];
    }

    public Tile tileOpt(int x, int y){
        return !Structs.inBounds(x, y, width(), height()) ? null : tiles[x][y];
    }
}
