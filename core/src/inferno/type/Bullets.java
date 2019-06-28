package inferno.type;

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
    };
}
