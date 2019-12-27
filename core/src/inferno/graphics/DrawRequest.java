package inferno.graphics;

import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.util.pooling.Pool.Poolable;

class DrawRequest implements Comparable<DrawRequest>, Poolable{
    TextureRegion region;
    float x, y, z, originX, originY, width, height, rotation, color, blendColor;
    float[] vertices = new float[24];
    Texture texture;

    @Override
    public int compareTo(DrawRequest o){
        return Float.compare(o.z, z);
    }

    @Override
    public void reset(){
        x = y = z = originX = originY = width = height = rotation = color = blendColor = 0f;
        region = null;
        texture = null;
    }
}