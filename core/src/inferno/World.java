package inferno;

import inferno.world.MapLoader;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.Core;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.maps.MapLayer;
import io.anuke.arc.maps.tiled.TiledMap;
import io.anuke.arc.maps.tiled.TiledMapTileLayer;
import io.anuke.arc.util.Structs;

public class World implements ApplicationListener{
    TiledMap map;
    TiledMapTileLayer floorLayer, wallLayer;
    MapLayer objectLayer;

    @Override
    public void init(){
        map = new MapLoader().load("maps/map.tmx");

        floorLayer = (TiledMapTileLayer) map.getLayers().get("floor");
        wallLayer = (TiledMapTileLayer) map.getLayers().get("walls");
        objectLayer = map.getLayers().get("objects");
    }

    public int width(){
        return wallLayer.getWidth();
    }

    public int height(){
        return wallLayer.getHeight();
    }

    public TextureRegion floorRegion(int x, int y){
        return floorLayer.getCell(x, y) == null ? Core.atlas.find("error") : floorLayer.getCell(x, y).getTile().getTextureRegion();
    }

    public TextureRegion wallRegion(int x, int y){
        return wallLayer.getCell(x, y) == null ? Core.atlas.find("error") : wallLayer.getCell(x, y).getTile().getTextureRegion();
    }

    public boolean solid(int x, int y){
        if(!Structs.inBounds(x, y, floorLayer.getWidth(), floorLayer.getHeight())){
            return true;
        }
        //TODO horribly slow
        return wallLayer.getCell(x, y) != null && wallLayer.getCell(x, y).getTile().getProperties().containsKey("solid");
    }
}
