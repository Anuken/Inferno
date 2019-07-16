package inferno.type;

import inferno.graphics.Drawf;
import inferno.graphics.Effects.Effect;
import inferno.graphics.Pal;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.math.*;
import io.anuke.arc.util.Tmp;

public class Fx{
    private static final RandomXS128 random = new RandomXS128();

    public static final Effect

    spark = new Effect(20, e -> {
        Draw.color(Color.WHITE, e.color, e.fout());
        Angles.randLenVectors(e.id, 7, 30f * e.fin(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 4f);
            //Layer.light(e.x + x, e.y + y, 18f * e.fout(), e.color);
        });
    }),
    fireballtrail = new Effect(70, e -> {
        Drawf.z(e.y + 1f);
        Draw.color(e.color);
        Angles.randLenVectors(e.id, 2, 5f + 20f * e.fin(), (x, y) -> {
            Fill.circle(e.x + x, e.y + y, e.fout() * 5f + 1f);
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
        Lines.circle(e.x, e.y, e.fin() * 70f);
    }),
    tpwave = new Effect(50, e -> {
        Lines.stroke(4f * e.fin());
        Draw.color(Pal.lucine, Pal.candle, 0.5f);
        Lines.circle(e.x, e.y, e.fout() * 60f);
        Draw.alpha(e.fin());
        Drawf.symbols(e.id, e.x, e.y, 40f);
    }),
    candlespiral = new Effect(100f, e -> {
        Drawf.z(e.y - 20f);
        int amount = 100;
        float length = e.fout() * 110f;
        Draw.color(Pal.candle);

        random.setSeed(e.id);
        for(int i = 0; i < amount; i++){
            float scl = length * random.nextFloat();
            float vang = random.nextFloat() * 360f + e.fin()*360f * random.nextFloat() * e.fin();
            Tmp.v1.set(scl, 0).rotate(vang);

            Fill.circle(Tmp.v1.x + e.x, Tmp.v1.y + e.y, 2f * e.fin());
        }

        Draw.color(Pal.candle);
        Fill.circle(e.x, e.y, e.fin() * 6f + 1.8f);
        Draw.color();
        Fill.circle(e.x, e.y, e.fin() * 4f + 1f);

        Drawf.light(e.x, e.y, e.fin() * 70f, Color.ORANGE, e.fin());
    }),
    candlefire  = new Effect(10f, e -> {
        Drawf.z(e.y - 30f);

        Draw.color(Color.WHITE, Pal.candle, e.fin());
        Fill.circle(e.x, e.y, 20f * e.fout());
        Angles.randLenVectors(e.id, 20, 50f * e.finpow(), (x, y) -> {
            Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), 6f * e.fout());
        });
        Lines.stroke(e.fout() * 2f);
        Lines.circle(e.x, e.y, e.fin() * 50f);
    }),
    fireballfire  = new Effect(10f, e -> {
        Drawf.z(e.y - 30f);

        Draw.color(Color.WHITE, Pal.fireball, e.fin());
        Fill.circle(e.x, e.y, 50f * e.fout());
        Lines.stroke(e.fout() * 2f);
        Lines.circle(e.x, e.y, e.fin() * 50f);
        Drawf.light(e.x, e.y, 200f, Pal.fireball, e.fout());
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
    }),

    indline = new Effect(30f, e -> {
        Drawf.z(e.y + 1000f);
        Draw.color(Pal.lucine);
        Lines.stroke(2f * e.fslope());

        float fract = 0.4f;

        for(int i = 0; i < 4; i++){
            Tmp.v1.trns(e.rotation, Math.max(e.fin() * (100f) + i * 20 - 20, 0));
            Lines.swirl(e.x + Tmp.v1.x, e.y + Tmp.v1.y, 10f, fract, e.rotation - 180f*fract);
        }
        Draw.reset();
    }),

    indwave = new Effect(indline.lifetime, e -> {
        Drawf.z(e.y + 1000f);
        Draw.color(Pal.lucine);
        Lines.stroke(2f * e.fslope());

        float fract = 0.17f;

        for(int i = 0; i < 4; i++){
            Lines.swirl(e.x, e.y, Math.max(100f * e.fin() + i * 20f - 10, 0), fract, e.rotation - 180f*fract);
        }
        Draw.reset();
    });;
}
