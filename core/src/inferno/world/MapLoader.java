package inferno.world;

import io.anuke.arc.Core;
import io.anuke.arc.collection.Array;
import io.anuke.arc.collection.IntArray;
import io.anuke.arc.files.FileHandle;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.maps.ImageResolver;
import io.anuke.arc.maps.MapProperties;
import io.anuke.arc.maps.tiled.*;
import io.anuke.arc.maps.tiled.tiles.AnimatedTiledMapTile;
import io.anuke.arc.maps.tiled.tiles.StaticTiledMapTile;
import io.anuke.arc.util.ArcRuntimeException;
import io.anuke.arc.util.Log;
import io.anuke.arc.util.serialization.SerializationException;
import io.anuke.arc.util.serialization.XmlReader.Element;

public class MapLoader extends TmxMapLoader{

    @Override
    public TiledMap load(String fileName, Parameters parameters){
        this.convertObjectToTileSpace = parameters.convertObjectToTileSpace;
        this.flipY = parameters.flipY;
        FileHandle tmxFile = resolve(fileName);
        root = xml.parse(tmxFile);

        return loadTilemap(root, tmxFile, null);
    }

    @Override
    protected TiledMap loadTilemap(Element root, FileHandle tmxFile, ImageResolver imageResolver){
        TiledMap map = new TiledMap();

        String mapOrientation = root.getAttribute("orientation", null);
        int mapWidth = root.getIntAttribute("width", 0);
        int mapHeight = root.getIntAttribute("height", 0);
        int tileWidth = root.getIntAttribute("tilewidth", 0);
        int tileHeight = root.getIntAttribute("tileheight", 0);
        int hexSideLength = root.getIntAttribute("hexsidelength", 0);
        String staggerAxis = root.getAttribute("staggeraxis", null);
        String staggerIndex = root.getAttribute("staggerindex", null);
        String mapBackgroundColor = root.getAttribute("backgroundcolor", null);

        MapProperties mapProperties = map.getProperties();
        if(mapOrientation != null){
            mapProperties.put("orientation", mapOrientation);
        }
        mapProperties.put("width", mapWidth);
        mapProperties.put("height", mapHeight);
        mapProperties.put("tilewidth", tileWidth);
        mapProperties.put("tileheight", tileHeight);
        mapProperties.put("hexsidelength", hexSideLength);
        if(staggerAxis != null){
            mapProperties.put("staggeraxis", staggerAxis);
        }
        if(staggerIndex != null){
            mapProperties.put("staggerindex", staggerIndex);
        }
        if(mapBackgroundColor != null){
            mapProperties.put("backgroundcolor", mapBackgroundColor);
        }
        mapTileWidth = tileWidth;
        mapTileHeight = tileHeight;
        mapWidthInPixels = mapWidth * tileWidth;
        mapHeightInPixels = mapHeight * tileHeight;

        if(mapOrientation != null){
            if("staggered".equals(mapOrientation)){
                if(mapHeight > 1){
                    mapWidthInPixels += tileWidth / 2;
                    mapHeightInPixels = mapHeightInPixels / 2 + tileHeight / 2;
                }
            }
        }

        Element properties = root.getChildByName("properties");
        if(properties != null){
            loadProperties(map.getProperties(), properties);
        }
        Array<Element> tilesets = root.getChildrenByName("tileset");
        for(Element element : tilesets){
            loadTileSet(map, element, tmxFile, imageResolver);
            root.removeChild(element);
        }
        for(int i = 0, j = root.getChildCount(); i < j; i++){
            Element element = root.getChild(i);
            String name = element.getName();
            if(name.equals("layer")){
                loadTileLayer(map, map.getLayers(), element);
            }else if(name.equals("objectgroup")){
                loadObjectGroup(map, map.getLayers(), element);
            }
        }
        return map;
    }

    @Override
    protected void loadTileSet(TiledMap map, Element element, FileHandle tmxFile, ImageResolver imageResolver){
        if(element.getName().equals("tileset")){
            String name = element.get("name", null);
            int firstgid = element.getIntAttribute("firstgid", 1);
            int tilewidth = element.getIntAttribute("tilewidth", 0);
            int tileheight = element.getIntAttribute("tileheight", 0);
            int spacing = element.getIntAttribute("spacing", 0);
            int margin = element.getIntAttribute("margin", 0);
            String source = element.getAttribute("source", null);

            int offsetX = 0;
            int offsetY = 0;

            String imageSource = "";
            int imageWidth = 0, imageHeight = 0;

            FileHandle image = null;
            if(source != null){
                FileHandle tsx = getRelativeFileHandle(tmxFile, source);
                try{
                    element = xml.parse(tsx);
                    name = element.get("name", null);
                    tilewidth = element.getIntAttribute("tilewidth", 0);
                    tileheight = element.getIntAttribute("tileheight", 0);
                    spacing = element.getIntAttribute("spacing", 0);
                    margin = element.getIntAttribute("margin", 0);
                    Element offset = element.getChildByName("tileoffset");
                    if(offset != null){
                        offsetX = offset.getIntAttribute("x", 0);
                        offsetY = offset.getIntAttribute("y", 0);
                    }
                    Element imageElement = element.getChildByName("image");
                    if(imageElement != null){
                        imageSource = imageElement.getAttribute("source");
                        imageWidth = imageElement.getIntAttribute("width", 0);
                        imageHeight = imageElement.getIntAttribute("height", 0);
                        image = getRelativeFileHandle(tsx, imageSource);
                    }
                }catch(SerializationException e){
                    throw new ArcRuntimeException("Error parsing external tileset.");
                }
            }else{
                Element offset = element.getChildByName("tileoffset");
                if(offset != null){
                    offsetX = offset.getIntAttribute("x", 0);
                    offsetY = offset.getIntAttribute("y", 0);
                }
                Element imageElement = element.getChildByName("image");
                if(imageElement != null){
                    imageSource = imageElement.getAttribute("source");
                    imageWidth = imageElement.getIntAttribute("width", 0);
                    imageHeight = imageElement.getIntAttribute("height", 0);
                    image = getRelativeFileHandle(tmxFile, imageSource);
                }
            }

            TiledMapTileSet tileset = new TiledMapTileSet();
            tileset.setName(name);
            tileset.getProperties().put("firstgid", firstgid);

            Array<Element> tileElements = element.getChildrenByName("tile");
            for(Element tileElement : tileElements){
                Element imageElement = tileElement.getChildByName("image");
                if(imageElement != null){
                    imageSource = imageElement.getAttribute("source");
                    imageWidth = imageElement.getIntAttribute("width", 0);
                    imageHeight = imageElement.getIntAttribute("height", 0);

                    if(source != null){
                        image = getRelativeFileHandle(getRelativeFileHandle(tmxFile, source), imageSource);
                    }else{
                        image = getRelativeFileHandle(tmxFile, imageSource);
                    }
                }

                TiledMapTile tile = new StaticTiledMapTile((TextureRegion)null);
                tile.setTextureRegion(Core.atlas.find(imageSource.substring(1 + imageSource.lastIndexOf("/")).replace(".png", "")));
                tile.setId(firstgid + tileElement.getIntAttribute("id"));
                tile.setOffsetX(offsetX);
                tile.setOffsetY(flipY ? -offsetY : offsetY);
                tileset.putTile(tile.getId(), tile);
            }


            Array<AnimatedTiledMapTile> animatedTiles = new Array<>();

            for(Element tileElement : tileElements){
                int localtid = tileElement.getIntAttribute("id", 0);
                TiledMapTile tile = tileset.getTile(firstgid + localtid);
                if(tile != null){
                    Element animationElement = tileElement.getChildByName("animation");
                    if(animationElement != null){

                        Array<StaticTiledMapTile> staticTiles = new Array<>();
                        IntArray intervals = new IntArray();
                        for(Element frameElement : animationElement.getChildrenByName("frame")){
                            staticTiles.add((StaticTiledMapTile)tileset.getTile(firstgid + frameElement.getIntAttribute("tileid")));
                            intervals.add(frameElement.getIntAttribute("duration"));
                        }

                        AnimatedTiledMapTile animatedTile = new AnimatedTiledMapTile(intervals, staticTiles);
                        animatedTile.setId(tile.getId());
                        animatedTiles.add(animatedTile);
                        tile = animatedTile;
                    }

                    Element objectgroupElement = tileElement.getChildByName("objectgroup");
                    if(objectgroupElement != null){

                        for(Element objectElement : objectgroupElement.getChildrenByName("object")){
                            loadObject(map, tile, objectElement);
                        }
                    }

                    String terrain = tileElement.getAttribute("terrain", null);
                    if(terrain != null){
                        tile.getProperties().put("terrain", terrain);
                    }
                    String probability = tileElement.getAttribute("probability", null);
                    if(probability != null){
                        tile.getProperties().put("probability", probability);
                    }
                    Element properties = tileElement.getChildByName("properties");
                    if(properties != null){
                        loadProperties(tile.getProperties(), properties);
                    }
                }
            }

            for(AnimatedTiledMapTile tile : animatedTiles){
                tileset.putTile(tile.getId(), tile);
            }

            Element properties = element.getChildByName("properties");
            if(properties != null){
                loadProperties(tileset.getProperties(), properties);
            }
            map.getTileSets().addTileSet(tileset);
        }
    }
}
