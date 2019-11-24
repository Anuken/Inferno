package inferno.world;

import inferno.graphics.*;
import inferno.type.*;
import io.anuke.arc.collection.*;
import io.anuke.arc.graphics.*;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.math.*;
import io.anuke.arc.util.*;

import static inferno.Inferno.*;

public class Blocks{
    public static final Array<Block> blocks = Array.with(

        new Block("candle"){
            {
                shadowSize = 14f;
            }

            @Override
            public void draw(int x, int y){
                TextureRegion region = regions[0];

                Drawf.z(y * tilesize);
                Draw.rect(region, x * tilesize, y * tilesize + region.getHeight() / 2f - 1,
                    region.getWidth() * Mathf.sign(Mathf.randomSeed(y * world.width() +x) - 0.5f), region.getHeight());

                float offset = 14f;

                Drawf.light(x * tilesize, y * tilesize + offset, 50f + Mathf.absin(Time.time(), 10f, 10f),
                        Color.orange, 0.7f);

                float rad = 1.8f + Mathf.absin(Time.time(), 5f, 1.1f);

                Draw.color(Color.orange);
                Fill.circle(x * tilesize, y * tilesize + offset, rad);
                Draw.color();
                Fill.circle(x * tilesize, y * tilesize + offset, rad * 0.5f);

            }
        },

        new Block("barrier"){
            @Override
            public void draw(int x, int y){

            }
        },

        new Block("statue"){
            @Override
            public void draw(int x, int y){
                TextureRegion region = regions[0];

                Draw.rect(region, x * tilesize + tilesize/2f, y * tilesize - tilesize / 2f + region.getHeight() / 2f);

            }
        },

        new Block("shelfrubble"){
            {
                damage = 3f;
            }

            @Override
            public void draw(int x, int y){
                TextureRegion region = regions[rand(x, y, regions.length - 1)];

                Drawf.z(y*tilesize + tilesize);

                Draw.rect(region, x * tilesize , y * tilesize - tilesize / 2f + region.getHeight() / 2f);
                Drawf.light(x * tilesize, y * tilesize + tilesize/2f, 60f + Mathf.absin(Time.time(), 5f, 7f), Color.orange, 0.7f);
                if(!control.isPaused() && Mathf.chance(0.13 * Time.delta())){
                    Fx.fire.at(x * tilesize + Mathf.range(tilesize), y * tilesize + Mathf.range(tilesize) + tilesize/2f);
                }
            }
        },

        new Block("fire"){
            {
                damage = 0.1f;
                solid = false;
            }

            @Override
            public void drawShadow(int x, int y){

            }

            @Override
            public void draw(int x, int y){
                Drawf.light(x * tilesize, y * tilesize + tilesize/2f, 60f + Mathf.absin(Time.time(), 5f, 7f), Color.orange, 0.7f);
                if(!control.isPaused() && Mathf.chance(0.03 * Time.delta())){
                    Fx.fire.at(x * tilesize + Mathf.range(tilesize), y * tilesize + Mathf.range(tilesize));
                }
            }
        },

        new Block("ashrubble"){

            @Override
            public void draw(int x, int y){
                TextureRegion region = regions[rand(x, y, regions.length - 1)];
                Drawf.z(y*tilesize + tilesize);
                Draw.rect(region, x * tilesize , y * tilesize - tilesize / 2f + region.getHeight() / 2f);
            }
        },

        new Block("book"){
            Color[] colors = {Color.valueOf("4c5f3e"), Color.valueOf("7b6844"), Color.valueOf("445e6d"), Color.valueOf("704533"), Color.valueOf("8f875f")};
            Color temp = new Color();
            int w = 4, h = 5;

            @Override
            public void draw(int x, int y){
                Drawf.z(y * tilesize + tilesize/2f);
                int spread = tilesize;

                int amount = rand(x, y, -1, 3);

                for(int i = 0; i < amount; i ++){

                    int dx = rand(x, y, i*2, spread);
                    int dy = rand(x, y, i*2 + 1, spread);
                    int rot = rand(x, y, i*2 + 2, 360);
                    int color = rand(x, y, i * 2 + 3, colors.length);
                    float mul = 1f + (rand(x, y, i*2 + 3, 255)/255f-0.5f)/6f;

                    drawBook(w, h,
                    x * tilesize + dx-spread/2,
                    y * tilesize + dy - spread/2,
                    rot, colors[color - 1], mul);
                }

            }

            void drawBook(int w, int h, float x, float y, float rot, Color color, float mul){

                temp.set(color).mul(mul, mul, mul, 1f);

                Draw.color(0f, 0f, 0f, 0.25f);
                Fill.rect(x, y - 2f, w, h, rot);

                Draw.colorMul(temp, 0.7f);
                Fill.rect(x, y - 0.7f, w, h, rot);

                Draw.colorMul(color, mul);
                Fill.rect(x, y, w, h, rot);

                Draw.color();
            }
        },
        new Block("carpet"){

            @Override
            public void draw(int x, int y){
                TextureRegion region = regions[0];

                Draw.rect(region, x * tilesize, y * tilesize, world.tile(x, y).rotation);
            }
        },
        new Block("carpet-trim"){

            @Override
            public void draw(int x, int y){
                TextureRegion region = regions[0];

                Draw.rect(region, x * tilesize, y * tilesize, world.tile(x, y).rotation);
            }

        },
        new Block("rubble"){

            @Override
            public void draw(int x, int y){
                TextureRegion d = regions[rand(x, y, variants) - 1];
                Draw.color(0f, 0f, 0f, 0.3f);
                Draw.rect(d, x * tilesize, y * tilesize + tilesize/2f - 1);
                Draw.color();
                Draw.rect(d, x * tilesize, y * tilesize + tilesize/2f);
            }
        }

    );

}
