package inferno.graphics;

import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.util.pooling.Pool.Poolable;

class DrawRequest implements Comparable<DrawRequest>, Poolable{
    TextureRegion region;
    float x, y, z, originX, originY, width, height, rotation, color;

    @Override
    public int compareTo(DrawRequest o){
        return Float.compare(o.z, z);
    }

    @Override
    public void reset(){
        x = y = z = originX = originY = width = height = rotation = color = 0f;
        region = null;
    }
}