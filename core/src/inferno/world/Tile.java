package inferno.world;

public class Tile{
    /** May be null.*/
    public Block floor, wall, overlay;
    public boolean shadowed;
    public int rotation;

    public Tile(Block floor, Block overlay, Block wall){
        this.floor = floor;
        this.wall = wall;
        this.overlay = overlay;
    }

    public boolean solid(){
        return wall != null && wall.solid;
    }
}
