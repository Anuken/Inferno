package inferno.world;

import arc.*;
import arc.files.*;
import arc.graphics.g2d.TextureAtlas.*;
import arc.maps.*;
import arc.maps.loaders.*;
import arc.maps.loaders.XmlReader.*;
import arc.struct.*;
import arc.util.*;
import arc.util.serialization.*;

public class MapLoader extends TmxMapLoader{

    @Override
    public TiledMap load(String fileName, Parameters parameters){
        this.convertObjectToTileSpace = parameters.convertObjectToTileSpace;
        this.flipY = parameters.flipY;
        Fi tmxFile = resolve(fileName);
        root = xml.parse(tmxFile);

        return loadTilemap(root, tmxFile, null);
    }

    @Override
    protected TiledMap loadTilemap(Xml root, Fi tmxFile, ImageResolver imageResolver){
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

        MapProperties mapProperties = map.properties;
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

        Xml properties = root.getChildByName("properties");
        if(properties != null){
            loadProperties(map.properties, properties);
        }
        Seq<Xml> tilesets = root.getChildrenByName("tileset");
        for(Xml element : tilesets){
            loadTileSet(map, element, tmxFile, imageResolver);
            root.removeChild(element);
        }
        for(int i = 0, j = root.getChildCount(); i < j; i++){
            Xml element = root.getChild(i);
            String name = element.getName();
            if(name.equals("layer")){
                loadTileLayer(map, map.layers, element);
            }else if(name.equals("objectgroup")){
                loadObjectGroup(map, map.layers, element);
            }
        }
        return map;
    }

    @Override
    protected void loadTileSet(TiledMap map, Xml element, Fi tmxFile, ImageResolver imageResolver){
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

            Fi image = null;
            if(source != null){
                Fi tsx = getRelativeFileHandle(tmxFile, source);
                try{
                    element = xml.parse(tsx);
                    name = element.get("name", null);
                    tilewidth = element.getIntAttribute("tilewidth", 0);
                    tileheight = element.getIntAttribute("tileheight", 0);
                    spacing = element.getIntAttribute("spacing", 0);
                    margin = element.getIntAttribute("margin", 0);
                    Xml offset = element.getChildByName("tileoffset");
                    if(offset != null){
                        offsetX = offset.getIntAttribute("x", 0);
                        offsetY = offset.getIntAttribute("y", 0);
                    }
                    Xml imageElement = element.getChildByName("image");
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
                Xml offset = element.getChildByName("tileoffset");
                if(offset != null){
                    offsetX = offset.getIntAttribute("x", 0);
                    offsetY = offset.getIntAttribute("y", 0);
                }
                Xml imageElement = element.getChildByName("image");
                if(imageElement != null){
                    imageSource = imageElement.getAttribute("source");
                    imageWidth = imageElement.getIntAttribute("width", 0);
                    imageHeight = imageElement.getIntAttribute("height", 0);
                    image = getRelativeFileHandle(tmxFile, imageSource);
                }
            }

            TileSet tileset = new TileSet();
            tileset.name = name;
            tileset.getProperties().put("firstgid", firstgid);

            Seq<Xml> tileElements = element.getChildrenByName("tile");
            for(Xml tileElement : tileElements){
                Xml imageElement = tileElement.getChildByName("image");
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

                String regionName = imageSource.substring(1 + imageSource.lastIndexOf("/")).replace(".png", "");



                MapTile tile = new MapTile(null);
                tile.region = Core.atlas.has(regionName) ? Core.atlas.find(regionName) : new AtlasRegion(Core.atlas.find("error")){{ name = regionName; }};
                tile.id = firstgid + tileElement.getIntAttribute("id");
                tile.offsetX = (offsetX);
                tile.offsetY = (flipY ? -offsetY : offsetY);
                tileset.put(tile.id, tile);
            }

            for(Xml tileElement : tileElements){
                int localtid = tileElement.getIntAttribute("id", 0);
                MapTile tile = tileset.get(firstgid + localtid);
                if(tile != null){
                    Xml objectgroupElement = tileElement.getChildByName("objectgroup");
                    if(objectgroupElement != null){

                        for(Xml objectElement : objectgroupElement.getChildrenByName("object")){
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
                    Xml properties = tileElement.getChildByName("properties");
                    if(properties != null){
                        loadProperties(tile.getProperties(), properties);
                    }
                }
            }

            Xml properties = element.getChildByName("properties");
            if(properties != null){
                loadProperties(tileset.getProperties(), properties);
            }
            map.tilesets.addTileSet(tileset);
        }
    }
}
