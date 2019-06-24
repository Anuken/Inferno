package inferno.type;

import inferno.graphics.Effects.Effect;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.Fill;
import io.anuke.arc.math.Angles;

public class Fx{
    public static final Effect

    spark = new Effect(15, e -> {
        Draw.color(Color.RED, Color.BLUE, e.fout());
        Angles.randLenVectors(e.id, 10, 10f * e.fin(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 3f);
        });
        Draw.color();
    });
}
