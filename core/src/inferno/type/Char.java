package inferno.type;

import inferno.entity.EntityGroup;
import inferno.entity.SolidEntity;
import inferno.type.Bullet.*;
import io.anuke.arc.math.Angles;
import io.anuke.arc.math.geom.Rectangle;

import static inferno.Inferno.charGroup;

public abstract class Char extends SolidEntity{
    public static final float hitdur = 5f;
    public float health, hitTime;
    public boolean dead;

    public Char(){
        health = maxHealth();
    }

    public boolean isPlayer(){
        return this instanceof Player;
    }

    public void shoot(BulletType type, float rot){
        Bullet.shoot(type, this, x, y + height(), rot);
    }

    public void shoot(BulletType type, float rot, Mover mover){
        Bullet.shoot(type, this, x, y + height(), rot).mover = mover;
    }

    public void shoot(BulletType type, float x, float y, float rot){
        Bullet.shoot(type, this, x + Angles.trnsx(rot, 3f), y + Angles.trnsy(rot, 3f), rot);
    }

    public void shoot(BulletType type, float x, float y, float rot, Mover mover){
        Bullet.shoot(type, this, x + Angles.trnsx(rot, 3f), y + Angles.trnsy(rot, 3f), rot).mover = mover;
    }

    public float maxHealth(){
        return 100f;
    }

    public void heal(){
        health = maxHealth();
        dead = false;
    }

    public float height(){
        return 10f;
    }

    public void onDeath(){

    }

    public void damage(float damage){
        health -= damage;
        hitTime = 1f;
        if(!dead && health <= 0){
            dead = true;
            onDeath();
        }
    }

    @Override
    public void collision(SolidEntity other, float x, float y){
        Bullet bullet = (Bullet)other;
        damage(bullet.shooter.isPlayer() ? 1f : bullet.type.damage);
    }

    @Override
    public void hitboxTile(Rectangle rectangle){
        float w = 6f, h = 6f;
        rectangle.set(x - w / 2f, y - 1, w, h);
    }

    @Override
    public EntityGroup targetGroup(){
        return charGroup;
    }
}
