package inferno.type;

import inferno.graphics.Layer;
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
        public void draw(Bullet bullet){
            Layer.z(100000f);
            Draw.color(Color.WHITE, Pal.lucine, bullet.fin());
            Lines.circle(bullet.x, bullet.y, 40f * bullet.fin());

            Draw.color(Color.WHITE);
            Draw.alpha(bullet.fout());
            Fill.circle(bullet.x, bullet.y, bullet.fout() * 40f);
        }


    };
}
