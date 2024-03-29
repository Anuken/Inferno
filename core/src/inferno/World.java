package inferno;

import inferno.world.*;
import arc.ApplicationListener;
import arc.struct.*;
import arc.func.*;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.maps.*;
import arc.math.Mathf;
import arc.math.geom.*;
import arc.util.*;

import static inferno.Inferno.*;

public class World implements ApplicationListener{
    Tile[][] tiles;
    ObjectMap<MapTile, Block> blocks = new ObjectMap<>();
    ObjectMap<String, Block> blockNames = new ObjectMap<>();
    TiledMap map;
    TileLayer floorLayer, wallLayer, overLayer;
    MapLayer objectLayer;
    public Seq<Point2> brokenWalls = new Seq<>();
    public Seq<Point2> candles = new Seq<>();
    public Vec2 statue = new Vec2();

    public World(){
        map = new MapLoader().load("maps/map.tmx");

        floorLayer = map.getLayer("floor");
        wallLayer = map.getLayer("walls");
        overLayer = map.getLayer("overlay");
        objectLayer = map.getLayer("images");

        tiles = new Tile[floorLayer.width][floorLayer.height];

        for(MapTile tile : map.tilesets.getTileSet(0)){
            String name = ((AtlasRegion)tile.region).name;
            if(name.endsWith("1")) name = name.substring(0, name.length() - 1);
            String fname = name;

            Block destination = Blocks.blocks.find(b -> b.name.equalsIgnoreCase(fname));
            if(destination == null){
                destination = new Block(name);
            }

            destination.solid = tile.getProperties().containsKey("solid");
            destination.clear = tile.getProperties().containsKey("clear");
            destination.id = tile.id;

            blocks.put(tile, destination);
            blockNames.put(name, destination);
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

                if(wall != null && wall.name.equals("statue")){
                    statue.set(x * tilesize + tilesize/2f, y * tilesize + tilesize*2f);
                }
            }
        }

        bulletGroup.resize(0, 0, width() * tilesize, height() * tilesize);
        charGroup.resize(0, 0, width() * tilesize, height() * tilesize);

        updateShadowed();
    }

    public void each(Cons<Tile> cons){
        for(int x = 0; x < width(); x++){
            for(int y = 0; y < height(); y++){
                cons.get(tiles[x][y]);
            }
        }
    }

    public void updateShadowed(){
        for(int x = 0; x < width(); x++){
            for(int y = 0; y < height(); y++){
                Tile tile = tiles[x][y];

                if(tile.solid() && !(tile.wall != null && tile.wall.clear)){
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

    public void wallDetonate(){
        int radius = 14;

        for(int x = 0; x < width(); x++){
            for(int y = 0; y < height(); y++){
                if(Mathf.within(x, y, width()/2, height()/2, radius)){
                    Tile tile = tile(x, y);
                    if(tile.wall != null && tile.wall.name.equals("shelf")){
                        int rx = Mathf.clamp(x - width()/2, -1, 1) * Mathf.random(0, 1);
                        int ry = Mathf.clamp(y - height()/2, -1, 1) * Mathf.random(0, 1);

                        tile.wall = null;

                        if(tiles[x + rx][y + ry].wall != null && tiles[x + rx][y + ry].wall.name.equals("shelf")){
                            brokenWalls.add(new Point2(x + rx, y + ry));
                        }
                        tiles[x + rx][y + ry].wall = blockNames.get("shelfrubble");
                        brokenWalls.add(new Point2(x, y));
                    }
                }
            }
        }

        updateShadowed();
        renderer.updateShadows();
    }

    public void wallExtinguish(){
        int radius = 14;

        for(int x = 0; x < width(); x++){
            for(int y = 0; y < height(); y++){
                Tile tile = tile(x, y);

                if(Mathf.within(x, y, width()/2, height()/2, radius) && tile.wall != null && (tile.wall.name.equals("shelf") || tile.wall.name.equals("shelfrubble"))){
                    if(tile.wall.name.equals("shelf")){
                        brokenWalls.add(new Point2(x, y));
                    }
                    tile.wall = blockNames.get("ashrubble");
                }
            }
        }

        updateShadowed();
        renderer.updateShadows();
    }

    public void wallUndetonate(){
        for(int x = 0; x < width(); x++){
            for(int y = 0; y < height(); y++){
                Tile tile = tile(x, y);
                if(tile.wall != null && (tile.wall.name.equals("shelfrubble") || tile.wall.name.equals("ashrubble"))){
                    tile.wall = null;
                }
            }
        }

        for(Point2 p : brokenWalls){
            Tile tile = tile(p.x, p.y);
            tile.wall = blockNames.get("shelf");
        }

        brokenWalls.clear();
        updateShadowed();
        renderer.updateShadows();
    }

    public Seq<MapObject> getObjects(){
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
