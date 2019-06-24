package inferno.type;

import inferno.entity.EntityGroup;
import inferno.entity.SolidEntity;
import io.anuke.arc.math.geom.Rectangle;

import static inferno.Inferno.charGroup;

public abstract class Char extends SolidEntity{
    public float health;

    public boolean isPlayer(){
        return this instanceof Player;
    }

    public void shoot(BulletType type, float rot){
        Bullet.shoot(type, this, x, y + height(), rot);
    }

    public float maxHealth(){
        return 100f;
    }

    public float height(){
        return 10f;
    }

    @Override
    public void hitboxTile(Rectangle rectangle){
        float w = 4f, h = 4f;
        rectangle.set(x - w / 2f, y, w, h);
    }

    @Override
    public EntityGroup targetGroup(){
        return charGroup;
    }
}
