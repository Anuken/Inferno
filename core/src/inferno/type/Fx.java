package inferno.type;

import inferno.graphics.Drawf;
import inferno.graphics.Effects.Effect;
import inferno.graphics.Pal;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.math.*;
import io.anuke.arc.util.Tmp;

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
        Drawf.z(100000f);
        Lines.stroke(3f * e.fin());
        Draw.color(Pal.lucine);
        Lines.circle(e.x, e.y, e.fout() * 40f);
        Lines.stroke(1f * e.fin());
        Lines.circle(e.x, e.y, 30f);
        Draw.alpha(e.fin());
        Drawf.symbols(e.id, e.x, e.y, 40f);
        Drawf.light(e.x, e.y, 40f, Pal.lucine, e.fin());

        /*
        //lines
                float length = e.fout(Interpolation.exp5Out) * 240f;
        Tmp.v1.trns(75f, length);

        Drawf.z(e.y);
        Draw.color(Pal.lucine);
        //Draw.alpha(e.fin());
        Lines.stroke(e.fin(Interpolation.exp5In) * 4f);
        Lines.lineAngle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 75f, e.fin(Interpolation.exp5In) * 24f, CapStyle.round);
         */

        float length = e.fout(Interpolation.exp5Out) * 200f;
        Tmp.v1.trns(75f, length);

        Drawf.z(e.y);
        Draw.color(Pal.lucine);
        Draw.alpha(e.fin(Interpolation.exp5In));
        Fill.circle(e.x + Tmp.v1.x, e.y + Tmp.v1.y, e.fin(Interpolation.exp5In) * 13f);
    }),
    meteorpost = new Effect(40f, e -> {
        /*
        e.scaled(10f, f -> {
            float height = 130f + 50f * f.fout();

            Drawf.z(e.y - 10f);
            Draw.color(Color.WHITE);

            Lines.stroke(15f * f.fout());
            Lines.line(e.x, e.y, e.x, e.y + height, CapStyle.round);
            Drawf.light(e.x, e.y + height/2f, 70f, height * 2f, Pal.lucine, e.fout() * 0.5f);
            Draw.reset();
        });*/
        Drawf.z(e.y - 10f);

        Lines.stroke(1.1f * e.fout() + 1f);
        Draw.color(Color.WHITE, Pal.lucine, e.fin());
        Angles.randLenVectors(e.id, 14, 70f * e.finpow(), (x, y) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 6f * e.fout() + 1f);
        });
    }),
    playershoot = new Effect(10, e -> {
        Draw.color(Color.WHITE, Color.CYAN, e.fout());
        Angles.randLenVectors(e.id, 3, 40f * e.fin(), e.rotation, 20f, (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 6f);
            Drawf.light(e.x + x, e.y + y, 8f * e.fout(), Color.CYAN);
        });
    });
}
