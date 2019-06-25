package inferno.world;

import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;

import static inferno.Inferno.tilesize;

public class Block{
    public int id;
    public String name;
    public TextureRegion region;
    public boolean solid;

    public Block(String name){
        this.name = name;
    }

    public void draw(int x, int y){
        Draw.rect(region, x * tilesize, y * tilesize - tilesize / 2f + region.getHeight() / 2f);
    }
}
