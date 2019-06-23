package inferno.world;

import io.anuke.arc.graphics.g2d.TextureRegion;

public class Tile{
    /** May be null.*/
    public final TextureRegion floor, wall;

    public boolean solid;

    public Tile(TextureRegion floor, TextureRegion wall){
        this.floor = floor;
        this.wall = wall;
    }
}
