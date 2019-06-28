package inferno.type;

import inferno.graphics.Effects.Effect;
import inferno.graphics.Layer;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.Fill;
import io.anuke.arc.math.Angles;

public class Fx{
    public static final Effect

    spark = new Effect(20, e -> {
        Draw.color(Color.WHITE, e.color, e.fout());
        Angles.randLenVectors(e.id, 7, 30f * e.fin(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 4f);
            //Layer.light(e.x + x, e.y + y, 18f * e.fout(), e.color);
        });
        Draw.color();
    }),
    playershoot = new Effect(10, e -> {
        Draw.color(Color.WHITE, Color.CYAN, e.fout());
        Angles.randLenVectors(e.id, 3, 40f * e.fin(), e.rotation, 20f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 6f);
            Layer.light(e.x + x, e.y + y, 8f * e.fout(), Color.CYAN);
        });
        Draw.color();
    });
}
