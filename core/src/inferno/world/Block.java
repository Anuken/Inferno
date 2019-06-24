package inferno.world;

import io.anuke.arc.graphics.g2d.TextureRegion;

public class Block{
    static int lastID;

    public final TextureRegion region;
    public final int id = lastID++;
    public boolean solid;

    public Block(TextureRegion region){
        this.region = region;
    }
}
