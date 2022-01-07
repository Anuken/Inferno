package inferno.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.*;

import static inferno.Inferno.renderer;

public class Drawf{
    static Vec2 vector = new Vec2();
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

    public static void lineAngle(float x, float y, float angle, float length, CapStyle style){
        vector.set(1, 1).setLength(length).setAngle(angle);

        line(x, y, x + vector.x, y + vector.y, style);
    }

    public static void line(float x, float y, float x2, float y2, CapStyle cap){
        line(x, y, x2, y2, cap, 0f);
    }

    public static void line(float x, float y, float x2, float y2, CapStyle cap, float padding){
        line(Core.atlas.white(), x, y, x2, y2, cap, padding);
    }

    public static void line(TextureRegion region, float x, float y, float x2, float y2, CapStyle cap, float padding){
        float stroke = Lines.getStroke();
        boolean precise = false;
        float length = Mathf.dst(x, y, x2, y2) + (cap == CapStyle.none || cap == CapStyle.round ? padding * 2f : stroke + padding * 2);
        float angle = (precise ? (float)Math.atan2(y2 - y, x2 - x) : Mathf.atan2(x2 - x, y2 - y)) * Mathf.radDeg;

        if(cap == CapStyle.square){
            Draw.rect(region, x - stroke / 2 - padding + length/2f, y, length, stroke, stroke / 2 + padding, stroke / 2, angle);
        }else if(cap == CapStyle.none){
            Draw.rect(region, x - padding + length/2f, y, length, stroke, padding, stroke / 2, angle);
        }else if(cap == CapStyle.round){ //TODO remove or fix
            TextureRegion cir = Core.atlas.has("hcircle") ? Core.atlas.find("hcircle") : Core.atlas.find("circle");
            Draw.rect(region, x - padding + length/2f, y, length, stroke, padding, stroke / 2, angle);
            Draw.rect(cir, x, y, stroke, stroke, angle + 180f);
            Draw.rect(cir, x2, y2, stroke, stroke, angle);
        }
    }

    public enum CapStyle{
        none, round, square
    }
}
