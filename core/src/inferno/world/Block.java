package inferno.world;

import inferno.graphics.Layer;
import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.arc.math.geom.Point2;

import static inferno.Inferno.tilesize;
import static inferno.Inferno.world;

public class Block{
    public int id;
    public String name;
    public TextureRegion region, edge;
    public boolean solid;

    protected float shadowSize = 8f;

    public Block(String name){
        this.name = name;

        if(Core.atlas.has(name + "-edge")){
            edge = Core.atlas.find(name + "-edge");
        }
    }

    public void draw(int x, int y){
        if(y == 0) Draw.color(Color.BLACK);

        if(solid){
            Draw.rect(region, x * tilesize, y * tilesize - tilesize / 2f + region.getHeight() / 2f);

            if(edge != null){
                Layer.z(y * tilesize - tilesize / 2f - 0.0001f);
                int i = 0;
                for(Point2 p : Geometry.d4){
                    if(world.tile(x + p.x, y + p.y).wall != this){
                        Draw.rect(edge, x * tilesize, y * tilesize + tilesize, i*90);
                    }
                    i++;
                }
            }
        }else{
            Layer.z(y * tilesize);
            Draw.rect(region, x * tilesize, y * tilesize + region.getHeight() / 2f);
        }
        Draw.color();
    }

    public void drawShadow(int x, int y){
        if(solid){
            Draw.rect("shadow", x * tilesize, y * tilesize);
        }else{
            Draw.rect("circle", x * tilesize, y * tilesize, shadowSize, shadowSize/2f);
        }
    }
}
