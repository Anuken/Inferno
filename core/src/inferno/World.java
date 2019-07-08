package inferno;

import inferno.world.*;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.collection.*;
import io.anuke.arc.graphics.g2d.TextureAtlas.AtlasRegion;
import io.anuke.arc.maps.*;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.arc.math.geom.Point2;
import io.anuke.arc.util.Structs;

import static inferno.Inferno.*;

public class World implements ApplicationListener{
    Tile[][] tiles;
    ObjectMap<MapTile, Block> blocks = new ObjectMap<>();
    Array<Point2> candles = new Array<>();
    TiledMap map;
    TileLayer floorLayer, wallLayer, overLayer;
    MapLayer objectLayer;

    public World(){
        map = new MapLoader().load("maps/map.tmx");

        floorLayer = map.getLayer("floor");
        wallLayer = map.getLayer("walls");
        overLayer = map.getLayer("overlay");
        objectLayer = map.getLayer("images");

        tiles = new Tile[floorLayer.width][floorLayer.height];

        for(MapTile tile : map.tilesets.getTileSet(0)){
            //if(!Core.atlas.isFound(tile.region)) continue;

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
                Block floor = blocks.getNull(floorLayer.getTile(x, y));
                Block overlay = blocks.getNull(overLayer.getTile(x, y));
                Block wall = blocks.getNull(wallLayer.getTile(x, y));

                tiles[x][y] = new Tile(floor, overlay, wall);
                tiles[x][y].rotation = overLayer.getCell(x, y) == null ? 0 : overLayer.getCell(x, y).rotation * 90;

                if(wall != null && wall.name.equals("candle")){
                    candles.add(new Point2(x, y));
                }
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

        bulletGroup.resize(0, 0, width() * tilesize, height() * tilesize);
        charGroup.resize(0, 0, width() * tilesize, height() * tilesize);
    }

    public Array<Point2> candles(){
        return candles;
    }

    public Array<MapObject> getObjects(){
        return objectLayer.objects;
    }

    public int width(){
        return wallLayer.width;
    }

    public int height(){
        return wallLayer.height;
    }

    public int world(float f){
        return (int)(f / tilesize);
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
