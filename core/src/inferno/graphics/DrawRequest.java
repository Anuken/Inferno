package inferno.graphics;

import io.anuke.arc.graphics.g2d.TextureRegion;

class DrawRequest implements Comparable<DrawRequest>{
    TextureRegion region;
    float x, y, z, originX, originY, width, height, rotation, color;

    @Override
    public int compareTo(DrawRequest o){
        return Float.compare(o.z, z);
    }
}