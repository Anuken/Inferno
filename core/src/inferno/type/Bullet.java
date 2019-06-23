package inferno.type;

import inferno.entity.EntityGroup;
import inferno.entity.SolidEntity;
import io.anuke.arc.math.geom.Rectangle;
import io.anuke.arc.math.geom.Vector2;

import static inferno.Inferno.bulletGroup;

public class Bullet extends SolidEntity{
    public BulletType type;
    public Char shooter;

    public Vector2 velocity = new Vector2();

    public static void shoot(BulletType type, Char shooter, float x, float y, float rotation){
        Bullet bullet = new Bullet();
        bullet.x = x;
        bullet.y = y;
        bullet.velocity.set(type.speed, 0).rotate(rotation);
        bullet.shooter = shooter;
        bullet.add();
    }

    private Bullet(){

    }

    @Override
    public void update(){
        super.update();

        x += velocity.x;
        y += velocity.y;
    }

    @Override
    public void hitbox(Rectangle rectangle){
        rectangle.setSize(type.size).setCenter(x, y);
    }

    @Override
    public void hitboxTile(Rectangle rectangle){
        hitbox(rectangle);
    }

    @Override
    public EntityGroup targetGroup(){
        return bulletGroup;
    }
}
