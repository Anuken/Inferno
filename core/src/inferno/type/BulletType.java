package inferno.type;

import inferno.graphics.Effects.Effect;
import io.anuke.arc.graphics.g2d.Fill;

public class BulletType{
    public float size = 4f;
    public float speed = 2f;

    public Effect hit = Fx.spark;

    public void draw(Bullet bullet){
        Fill.circle(bullet.x, bullet.y, 4f);
    }

    public void update(Bullet bullet){

    }

    public void hit(Bullet bullet){
        bullet.remove();
        hit.at(bullet.x, bullet.y);
    }
}
