package inferno.type;

import inferno.entity.*;
import inferno.graphics.*;
import io.anuke.arc.math.*;
import io.anuke.arc.math.geom.*;
import io.anuke.arc.util.*;

import static inferno.Inferno.bulletGroup;

public class Bullet extends SolidEntity implements ScaleTrait{
    public BulletType type;
    public Char shooter;
    public Interval timer = new Interval(4);
    public float lifetime;
    public float time;
    public boolean hit;
    public Mover mover = time -> Tmp.v1.setZero();

    public Vector2 velocity = new Vector2();

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

    private Bullet(){

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
        super.update();

        time = Mathf.clamp(time + Time.delta(), 0, type.lifetime);

        if(time >= lifetime){
            type.despawn(this);
            remove();
        }

        Vector2 v = mover.move(time);
        v.rotate(velocity.angle());
        velocity.add(v).limit(type.speed);

        x += (velocity.x) * Time.delta();
        y += (velocity.y ) * Time.delta();

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
    public void hitbox(Rectangle rectangle){
        rectangle.setSize(type.size).setCenter(x, y);
    }

    @Override
    public void hitboxTile(Rectangle rectangle){
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
        Vector2 move(float time);
    }
}
