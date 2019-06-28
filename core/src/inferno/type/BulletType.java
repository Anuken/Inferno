package inferno.type;

import inferno.graphics.Effects.Effect;
import inferno.graphics.Layer;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.*;

public class BulletType{
    public float size = 4f;
    public float speed = 2f;
    public float light = 30f;
    public float damage = 1f;
    public Color lightColor = new Color(1f, 1f, 1f, 0.5f);

    public Effect hit = Fx.spark;

    public void draw(Bullet bullet){
        Fill.circle(bullet.x, bullet.y, 4f);
    }

    public void update(Bullet bullet){

    }

    public void drawLight(Bullet bullet){
        if(light <= 0) return;

        Layer.light(bullet.x, bullet.y, light, lightColor);
    }

    public void hit(Bullet bullet){
        bullet.remove();
        hit.at(bullet.x, bullet.y);
    }
}
