package inferno.world;

import inferno.graphics.Layer;
import io.anuke.arc.collection.Array;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.Fill;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.util.Time;

import static inferno.Inferno.tilesize;
import static inferno.Inferno.world;

public class Blocks{
    public static final Array<Block> blocks = Array.with(

        new Block("candle"){
            {
                shadowSize = 14f;
            }

            @Override
            public void draw(int x, int y){
                Layer.z(y * tilesize);
                Draw.rect(region, x * tilesize, y * tilesize + region.getHeight() / 2f - 1,
                    region.getWidth() * Mathf.sign(Mathf.randomSeed(y * world.width() +x) - 0.5f), region.getHeight());

                float offset = 14f;

                Layer.light(x * tilesize, y * tilesize + offset, 50f + Mathf.absin(Time.time(), 10f, 10f),
                        Color.ORANGE, 0.7f);

                float rad = 1.8f + Mathf.absin(Time.time(), 5f, 1.1f);

                Draw.color(Color.ORANGE);
                Fill.circle(x * tilesize, y * tilesize + offset, rad);
                Draw.color();
                Fill.circle(x * tilesize, y * tilesize + offset, rad * 0.5f);

            }
        }

    );
}
