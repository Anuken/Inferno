package inferno.type;

import inferno.entity.*;
import inferno.graphics.*;
import io.anuke.arc.math.*;
import io.anuke.arc.math.geom.*;
import io.anuke.arc.util.*;

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
        time = Mathf.clamp(time + Time.delta(), 0, type.lifetime);

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
                renderer.shake(3f, 3f);
                damaged = true;
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
