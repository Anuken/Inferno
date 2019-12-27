package inferno.entity;

import arc.math.geom.*;
import arc.math.geom.QuadTree.QuadTreeObject;
import arc.util.*;

public abstract class SolidEntity extends Entity implements QuadTreeObject{
    public Vec2 lastPosition = new Vec2();

    public abstract void hitbox(Rect rectangle);

    public abstract void hitboxTile(Rect rectangle);

    public boolean collides(SolidEntity other){
        return true;
    }

    public void collision(SolidEntity other, float x, float y){
    }

    public void toward(Position other, float speed){
        move(Tmp.v1.set(other).sub(x, y).limit(speed).rotate(speed < 0 ? 180f : 0));
    }

    public void move(Vec2 Vec2){
        move(Vec2.x, Vec2.y);
    }

    public void move(float x, float y){
        EntityCollisions.move(this, x, y);
    }
}
