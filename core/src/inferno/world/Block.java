package inferno.world;

import inferno.graphics.Drawf;
import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.arc.math.geom.Point2;

import static inferno.Inferno.tilesize;
import static inferno.Inferno.world;

public class Block{
    public int id;
    public String name;
    public TextureRegion edge;
    public TextureRegion[] regions;
    public float damage = - 1;
    public boolean solid, clear;
    public int variants;

    protected float shadowSize = 8f;

    public Block(String name){
        this.name = name;

        if(Core.atlas.has(name + "-edge")){
            edge = Core.atlas.find(name + "-edge");
        }

        for(int i = 0; i < 10; i++){
            if(!Core.atlas.has(name + (i + 1))){
                break;
            }
            variants = i + 1;
        }

        if(variants == 0){
            variants = 1;
            regions = new TextureRegion[1];
            regions[0] = Core.atlas.find(name);
        }else{
            regions = new TextureRegion[variants];

            for(int i = 0; i < variants; i++){
                regions[i] = Core.atlas.find(name + (i + 1));
            }
        }
    }

    public void draw(int x, int y){
        if(y == 0) Draw.color(Color.black);

        if(solid){
            TextureRegion region = regions[rand(x, y, variants) - 1];
            Draw.rect(region, x * tilesize, y * tilesize - tilesize / 2f + region.getHeight() / 2f);

            if(edge != null){
                Drawf.z(y * tilesize - tilesize / 2f - 0.0001f);
                int i = 0;
                for(Point2 p : Geometry.d4){
                    if(world.tile(x + p.x, y + p.y).wall != this){
                        Draw.rect(edge, x * tilesize, y * tilesize + tilesize, i*90);
                    }
                    i++;
                }
            }
        }else{
            Drawf.z(y * tilesize);
            Draw.rect(regions[0], x * tilesize, y * tilesize + regions[0].getHeight() / 2f);
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

    public static int rand(int x, int y, int max){
        return rand(x, y, 0, max);
    }

    static int rand(int x, int y, int offset, int max){
        return Mathf.randomSeed(x + y *tilesize + offset, 1, max);
    }

}
