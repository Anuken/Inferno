package inferno.world;

import inferno.graphics.Layer;
import io.anuke.arc.Core;
import io.anuke.arc.collection.Array;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.util.*;

import static inferno.Inferno.tilesize;

public class Blocks{
    public static final Array<Block> blocks = Array.with(

        new Block("torch"){

            @Override
            public void draw(int x, int y){
                Draw.color();
                Layer.z(y * tilesize);
                Draw.rect(region, x * tilesize, y * tilesize + region.getHeight() / 2f);

                float offset = 8f;

                Layer.light(x * tilesize, y * tilesize + offset, 50f + Mathf.absin(Time.time(), 10f, 10f),
                        Color.ORANGE, 0.5f);

                Draw.color(Color.ORANGE);
                Fill.circle(x * tilesize, y * tilesize + offset, 1.5f + Mathf.absin(Time.time(), 5f, 1f));
                Draw.color(Color.WHITE);
                Fill.circle(x * tilesize, y * tilesize + offset, 0.9f + Mathf.absin(Time.time(), 5f, 0.5f));

                Draw.color();
            }

            @Override
            public void drawShadow(int x, int y){
                Draw.rect("circle", x * tilesize, y * tilesize, 8f, 4f);
            }
        }

    );
}
