package inferno.type;

import inferno.entity.*;
import inferno.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;

import static inferno.Inferno.bulletGroup;

public class Bullet extends SolidEntity implements ScaleTrait{
    public BulletType type;
    public Char shooter;
    public Interval timer = new Interval(4);
    public float lifetime;
    public float time;
    public boolean hit;
    public Mover mover = time -> Tmp.v1.setZero();

    public Vec2 velocity = new Vec2();

    public static Bullet shoot(BulletType type, Char shooter, float x, float y, float rotation){
        Bullet bullet = new Bullet();
        bullet.x = x;
        bullet.y = y;
        bullet.type = type;
        bullet.lifetime = type.lifetime;
        bullet.velocity.set(type.speed, 0).rotate(rotation);
        bullet.shooter = shooter;
        bullet.add();
        type.init(bullet);

        return bullet;
    }

    protected Bullet(){

    }

    public float angle(){
        return velocity.angle();
    }

    @Override
    public void collision(SolidEntity other, float x, float y){
        type.hit(this);
    }

    @Override
    public boolean collides(SolidEntity other){
        return true;
    }

    @Override
    public void update(){
        time = Mathf.clamp(time + Time.delta, 0, type.lifetime);

        if(time >= lifetime){
            type.despawn(this);
            remove();
        }

        Vec2 v = mover.move(time);
        v.rotate(velocity.angle()).scl(Time.delta);
        velocity.add(v).limit(type.speed);

        x += (velocity.x) * Time.delta;
        y += (velocity.y ) * Time.delta;

        type.update(this);

        hitboxTile(Tmp.r3);
        if(EntityCollisions.overlapsTile(Tmp.r3, type::solid)){
            type.hit(this);
        }
    }

    @Override
    public void draw(){
        Drawf.z(y - 8f);
        type.draw(this);
        type.drawLight(this);
    }

    @Override
    public void hitbox(Rect rectangle){
        rectangle.setSize(type.size).setCenter(x, y);
    }

    @Override
    public void hitboxTile(Rect rectangle){
        rectangle.setSize(4f).setCenter(x, y - 4f);
    }

    @Override
    public EntityGroup targetGroup(){
        return bulletGroup;
    }

    @Override
    public float fin(){
        return time / lifetime;
    }

    public interface Mover{
        Vec2 move(float time);
    }
}
