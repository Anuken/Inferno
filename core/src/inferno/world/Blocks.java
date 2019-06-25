package inferno.world;

import io.anuke.arc.collection.Array;
import io.anuke.arc.graphics.g2d.Draw;

import static inferno.Inferno.tilesize;

public class Blocks{
    public static final Array<Block> blocks = Array.with(

        new Block("torch"){

            @Override
            public void draw(int x, int y){
                Draw.rect(region, x * tilesize, y * tilesize - tilesize / 2f + region.getHeight() / 2f);
            }
        }

    );
}
