package inferno.world;

import io.anuke.arc.graphics.g2d.TextureRegion;

public class Tile{
    /** May be null.*/
    public Block floor, wall;
    public boolean shadowed;

    public Tile(Block floor, Block wall){
        this.floor = floor;
        this.wall = wall;
    }

    public boolean solid(){
        return wall != null && wall.solid;
    }
}
