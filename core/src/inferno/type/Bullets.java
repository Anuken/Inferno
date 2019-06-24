package inferno.type;

import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.*;

public class Bullets{
    public static final BulletType

    basic = new BulletType(){
        Color color = new Color(1f, 0f, 0f, 1f);
        {
            speed = 4f;
            lightColor = color;
        }

        @Override
        public void draw(Bullet bullet){
            Draw.color(color);
            Fill.circle(bullet.x, bullet.y, 5f);
            Draw.color(Color.WHITE);
            Fill.circle(bullet.x, bullet.y, 2f);
        }
    };
}
