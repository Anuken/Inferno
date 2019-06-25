package inferno.graphics;

import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;

import static inferno.Inferno.renderer;

public class Layer{

    public static void light(float x, float y, float rad){
        light(x, y, rad, Color.WHITE);
    }

    public static void light(float x, float y, float rad, Color color){
        light(x, y, rad, color, 1f);
    }

    public static void light(float x, float y, float rad, Color color, float alpha){
        Core.batch = renderer.lbatch;
        Draw.color(color, alpha);
        Draw.rect("light", x, y, rad*2, rad*2);
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
