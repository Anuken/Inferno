package inferno.type;

import inferno.entity.*;
import inferno.graphics.Layer;
import io.anuke.arc.math.geom.Rectangle;
import io.anuke.arc.math.geom.Vector2;
import io.anuke.arc.util.Time;
import io.anuke.arc.util.Tmp;

import static inferno.Inferno.bulletGroup;

public class Bullet extends SolidEntity{
    public BulletType type;
    public Char shooter;

    public Vector2 velocity = new Vector2();

    public static void shoot(BulletType type, Char shooter, float x, float y, float rotation){
        Bullet bullet = new Bullet();
        bullet.x = x;
        bullet.y = y;
        bullet.type = type;
        bullet.velocity.set(type.speed, 0).rotate(rotation);
        bullet.shooter = shooter;
        bullet.add();
    }

    private Bullet(){

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

        x += velocity.x * Time.delta();
        y += velocity.y * Time.delta();

        hitboxTile(Tmp.r3);

        if(EntityCollisions.overlapsTile(Tmp.r3)){
            type.hit(this);
        }
    }

    @Override
    public void draw(){
        Layer.z(y - 8f);
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
}
