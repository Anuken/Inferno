package inferno.type;

import inferno.graphics.Effects.Effect;
import inferno.graphics.Drawf;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Fill;

import static inferno.Inferno.renderer;
import static inferno.Inferno.world;

public class BulletType{
    public float size = 4f;
    public float speed = 2f;
    public float light = 30f;
    public float damage = 4f;
    public float lifetime = 200f;
    public float shake = 0f;
    public boolean deflect = true, pierce = false;
    public Color lightColor = new Color(1f, 1f, 1f, 0.5f);

    public Effect hit = Fx.spark;

    public void draw(Bullet bullet){
        Fill.circle(bullet.x, bullet.y, 4f);
    }

    public void update(Bullet bullet){

    }

    public void init(Bullet bullet){
        if(shake > 0){
            renderer.shake(shake);
        }
    }

    public boolean solid(int x, int y){
        return world.solid(x, y);
    }

    public void drawLight(Bullet bullet){
        if(light <= 0) return;

        Drawf.light(bullet.x, bullet.y, light, lightColor);
    }

    public void despawn(Bullet bullet){
        hit.at(bullet.x, bullet.y, lightColor);
    }

    public void hit(Bullet bullet){
        if(pierce){
            if(!bullet.hit){
                hit.at(bullet.x, bullet.y, lightColor);
                bullet.hit = true;
            }
        }else{
            bullet.remove();
            hit.at(bullet.x, bullet.y, lightColor);
        }
    }
}
