package inferno.type;

import inferno.graphics.Drawf;
import inferno.graphics.Pal;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.*;

public class Bullets{
    public static final BulletType

    basic = new BulletType(){
        {
            speed = 7f;
            lightColor = Pal.player;
        }

        @Override
        public void draw(Bullet bullet){
            Draw.color(Pal.player);
            Fill.circle(bullet.x, bullet.y, 5f);
            Draw.color(Color.WHITE);
            Fill.circle(bullet.x, bullet.y, 2f);
        }
    },
    lbasic = new BulletType(){
        {
            speed = 3f;
            lightColor = Pal.lucine;
        }

        @Override
        public void draw(Bullet bullet){
            Draw.color(Pal.lucine);
            Fill.circle(bullet.x, bullet.y, 5f);
            Draw.color(Color.WHITE);
            Fill.circle(bullet.x, bullet.y, 2f);
        }
    },
    meteor = new BulletType(){
        {
            pierce = true;
            speed = 0.001f;
            light = 60f;
            lifetime = 10f;
            size = 50f;
            shake = 4f;
            deflect = false;
            lightColor = Pal.lucine;
        }

        @Override
        public void init(Bullet bullet){
            super.init(bullet);
            Fx.meteorpost.at(bullet.x, bullet.y);
        }

        @Override
        public void draw(Bullet b){
            Drawf.z(100000f);
            Draw.color(Color.WHITE, Pal.lucine, b.fin());
            Lines.circle(b.x, b.y, 40f * b.fin());

            Draw.color(Color.WHITE);
            Draw.alpha(b.fout());
            Fill.circle(b.x, b.y, b.fout() * 40f);

            Draw.color(Pal.lucine);
            Draw.alpha(b.fout());
            Drawf.symbols(b.id, b.x, b.y, 40f);
        }
    };
}
