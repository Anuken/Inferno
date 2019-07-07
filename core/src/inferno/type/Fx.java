package inferno.type;

import inferno.graphics.Effects.Effect;
import inferno.graphics.Layer;
import inferno.graphics.Pal;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.math.Angles;
import io.anuke.arc.math.Mathf;

public class Fx{
    public static final Effect

    spark = new Effect(20, e -> {
        Draw.color(Color.WHITE, e.color, e.fout());
        Angles.randLenVectors(e.id, 7, 30f * e.fin(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 4f);
            //Layer.light(e.x + x, e.y + y, 18f * e.fout(), e.color);
        });
    }),
    dash = new Effect(30, e -> {
        Draw.color(Color.WHITE, e.color, e.fout());
        Lines.stroke(2f * e.fout() + 1f);
        Angles.randLenVectors(e.id, 5, 70f * e.fin(), e.rotation, 30f, (x, y) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 5f * e.fout() + 1f);
        });
    }),
    wave = new Effect(8, e -> {
        Lines.stroke(4f * e.fout());
        Draw.color(Color.WHITE, Pal.lucine, e.fout());
        Lines.circle(e.x, e.y, e.fin() * 40f);
    }),
    meteorpre = new Effect(60f, e -> {
        Layer.z(100000f);
        Lines.stroke(3f * e.fin());
        Draw.color(Pal.lucine);
        Lines.circle(e.x, e.y, e.fout() * 40f);
        Lines.stroke(1f * e.fin());
        Lines.circle(e.x, e.y, 30f);
        Layer.light(e.x, e.y, 40f, Pal.lucine, e.fin());
    }),
    playershoot = new Effect(10, e -> {
        Draw.color(Color.WHITE, Color.CYAN, e.fout());
        Angles.randLenVectors(e.id, 3, 40f * e.fin(), e.rotation, 20f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 6f);
            Layer.light(e.x + x, e.y + y, 8f * e.fout(), Color.CYAN);
        });
    });
}
