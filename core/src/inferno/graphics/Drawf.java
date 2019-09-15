package inferno.graphics;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.Angles;
import io.anuke.arc.math.Mathf;

import static inferno.Inferno.renderer;

public class Drawf{
    static TextureRegion[] symbols;

    public static void symbols(int seed, float x, float y, float radius){
        if(symbols == null){
            TextureRegion region = Core.atlas.find("runes");
            TextureRegion[][] split = region.split(7, 7);
            symbols = new TextureRegion[split.length];
            for(int i = 0; i < split.length; i++){
                symbols[i] = split[i][0];
            }
        }

        int amount = (int)((radius * Mathf.PI2) / 10f);
        for(int i = 0; i < amount; i++){
            float ang = i / (float)amount * 360f;
            Draw.rect(symbols[Mathf.randomSeed(seed + i, 0, symbols.length - 1)], x + Angles.trnsx(ang, radius), y + Angles.trnsy(ang, radius));
        }
    }

    public static void light(float x, float y, float rad){
        light(x, y, rad, Color.white);
    }

    public static void light(float x, float y, float rad, Color color){
        light(x, y, rad, color, 1f);
    }

    public static void light(float x, float y, float rad, Color color, float alpha){
        light(x, y, rad * 2, rad * 2, color, alpha);
    }

    public static void light(float x, float y, float width, float height, Color color, float alpha){
        Core.batch = renderer.lbatch;
        Draw.color(color, alpha);
        Draw.rect("light", x, y, width, height);
        Draw.color();
        Core.batch = renderer.zbatch;
    }

    public static void light(Color color, float alpha, Runnable run){
        Core.batch = renderer.lbatch;
        Draw.color(color, alpha);
        run.run();
        //Draw.rect("light", x, y, width, height);
        Draw.color();
        Core.batch = renderer.zbatch;
    }

    public static void z(float z){
        renderer.zbatch.z = z;
    }

    public static void sort(boolean sort){
        renderer.zbatch.sort(sort);
    }
}
