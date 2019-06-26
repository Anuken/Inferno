package inferno;

import inferno.world.*;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.Core;
import io.anuke.arc.collection.ObjectMap;
import io.anuke.arc.graphics.g2d.TextureAtlas.AtlasRegion;
import io.anuke.arc.maps.*;
import io.anuke.arc.maps.tiled.*;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.arc.math.geom.Point2;
import io.anuke.arc.util.Structs;

public class World implements ApplicationListener{
    Tile[][] tiles;
    ObjectMap<MapTile, Block> blocks = new ObjectMap<>();
    TiledMap map;
    TileLayer floorLayer, wallLayer, overLayer;
    MapLayer objectLayer;

    public World(){
        map = new MapLoader().load("maps/map.tmx");

        floorLayer = map.getLayer("floor");
        wallLayer = map.getLayer("walls");
        overLayer = map.getLayer("overlay");
        objectLayer = map.getLayer("objects");

        tiles = new Tile[floorLayer.getWidth()][floorLayer.getHeight()];

        for(MapTile tile : map.tilesets.getTileSet(0)){
            if(!Core.atlas.isFound(tile.region)) continue;

            String name = ((AtlasRegion)tile.region).name;
            Block destination = Blocks.blocks.find(b -> b.name.equalsIgnoreCase(name));
            if(destination == null){
                destination = new Block(name);
            }

            destination.solid = tile.getProperties().containsKey("solid");
            destination.id = tile.id;
            destination.region = tile.region;

            blocks.put(tile, destination);
        }

        for(int x = 0; x < width(); x++){
            for(int y = 0; y < height(); y++){
                Block floor = floorLayer.getCell(x, y) == null ?  null : blocks.get(floorLayer.getCell(x, y).tile);
                Block wall = wallLayer.getCell(x, y) == null ?  null : blocks.get(wallLayer.getCell(x, y).tile);

                tiles[x][y] = new Tile(floor, wall);
                if(overLayer.getCell(x, y) != null){
                    tiles[x][y].floor = blocks.get(overLayer.getCell(x, y).tile);
                }
                tiles[x][y].rotation = overLayer.getCell(x, y) == null ? 0 : overLayer.getCell(x, y).rotation * 90;
            }
        }

        for(int x = 0; x < width(); x++){
            for(int y = 0; y < height(); y++){
                Tile tile = tiles[x][y];

                if(tile.solid()){
                    for(Point2 near : Geometry.d4){
                        if(!tile(x + near.x, y + near.y).solid()){
                            tile.shadowed = true;
                            break;
                        }
                    }
                }else if(tile.wall != null){
                    tile.shadowed = true;
                }
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
        return tileOpt(x, y) == null || tile(x, y).solid();
    }

    public Tile tile(int x, int y){
        return tiles[Mathf.clamp(x, 0, width() - 1)][Mathf.clamp(y, 0, height() - 1)];
    }

    public Tile tileOpt(int x, int y){
        return !Structs.inBounds(x, y, width(), height()) ? null : tiles[x][y];
    }
}
