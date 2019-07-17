package inferno.entity;

import io.anuke.arc.math.geom.*;
import io.anuke.arc.math.geom.QuadTree.QuadTreeObject;
import io.anuke.arc.util.Tmp;

public abstract class SolidEntity extends Entity implements QuadTreeObject{
    public Vector2 lastPosition = new Vector2();

    public abstract void hitbox(Rectangle rectangle);

    public abstract void hitboxTile(Rectangle rectangle);

    public boolean collides(SolidEntity other){
        return true;
    }

    public void collision(SolidEntity other, float x, float y){
    }

    public void toward(Position other, float speed){
        move(Tmp.v1.set(other).sub(x, y).limit(speed).rotate(speed < 0 ? 180f : 0));
    }

    public void move(Vector2 vector2){
        move(vector2.x, vector2.y);
    }

    public void move(float x, float y){
        EntityCollisions.move(this, x, y);
    }
}
