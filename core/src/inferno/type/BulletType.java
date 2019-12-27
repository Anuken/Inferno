package inferno.type;

import inferno.graphics.*;
import inferno.graphics.Effects.*;
import inferno.world.*;
import arc.graphics.*;
import arc.graphics.g2d.*;

import static inferno.Inferno.*;

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

    public void draw(Laser laser){
        Lines.stroke(8f * laser.fout(), Pal.fireball);
        Lines.lineAngle(laser.x, laser.y, laser.angle, 1000f);
    }

    public void update(Bullet bullet){

    }

    public void init(Bullet bullet){
        if(shake > 0){
            renderer.shake(shake);
        }
    }

    public boolean solid(int x, int y){
        Tile tile = world.tileOpt(x, y);
        if(tile != null && tile.wall != null && tile.wall.clear){
            return false;
        }
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
