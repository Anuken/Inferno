package inferno.type;

import inferno.entity.*;
import inferno.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;

import static inferno.Inferno.*;

public class Laser extends Bullet implements ScaleTrait{
    public static final float length = 1300f;

    float angle;
    boolean damaged;

    public Laser(BulletType type, float x, float y, float angle){
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.shooter = boss;
        this.type = type;
        this.lifetime = type.lifetime;
        type.init(this);
    }

    @Override
    public boolean collides(SolidEntity other){
        return false;
    }

    @Override
    public void update(){
        time = Mathf.clamp(time + Time.delta, 0, type.lifetime);

        if(time >= lifetime){
            type.despawn(this);
            remove();
        }

        if(!damaged){
            player.hitbox(Tmp.r1);
            Tmp.v2.trns(angle, length);
            if(Intersector.intersectSegmentRectangle(x, y, x + Tmp.v2.x, y + Tmp.v2.y, Tmp.r1)){
                player.damage(type.damage);
                //TODO damage effect/shake
                renderer.shake(5f, 5f);
                damaged = true;
                control.slowmo(1f);

                Tmp.v2.limit(60f);
                player.move(Tmp.v2.x, Tmp.v2.y);
                for(int i = 0; i < 8; i++){
                    Fx.spark.at(player.x + Mathf.range(8f), player.y + 6f + Mathf.range(8f), Pal.fireball);
                }
            }
        }
    }

    @Override
    public void draw(){
        Drawf.z(-999f);
        type.draw(this);
    }

    @Override
    public EntityGroup targetGroup(){
        return bulletGroup;
    }
}
