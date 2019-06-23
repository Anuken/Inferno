package inferno.graphics;

import static inferno.Inferno.renderer;

public class Layer{

    public static void z(float z){
        renderer.lbatch.z = z;
    }

    public static void sort(boolean sort){
        renderer.lbatch.sort(sort);
    }
}
