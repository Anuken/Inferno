package inferno.graphics;

import io.anuke.arc.graphics.g2d.Draw;

import static inferno.Inferno.renderer;

public class Layer{

    public static void light(float x, float y, float rad){
        Draw.rect("light", x, y, rad*2, rad*2);
    }

    public static void z(float z){
        renderer.lbatch.z = z;
    }

    public static void sort(boolean sort){
        renderer.lbatch.sort(sort);
    }
}
