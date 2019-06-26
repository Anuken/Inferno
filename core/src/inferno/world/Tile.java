package inferno.world;

public class Tile{
    /** May be null.*/
    public Block floor, wall;
    public boolean shadowed;
    public int rotation;

    public Tile(Block floor, Block wall){
        this.floor = floor;
        this.wall = wall;
    }

    public boolean solid(){
        return wall != null && wall.solid;
    }
}
