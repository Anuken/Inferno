package inferno.entity;

import io.anuke.arc.math.geom.QuadTree.QuadTreeObject;
import io.anuke.arc.math.geom.Rectangle;
import io.anuke.arc.math.geom.Vector2;

public abstract class SolidEntity extends Entity implements QuadTreeObject{
    public Vector2 lastPosition = new Vector2();

    public abstract void hitbox(Rectangle rectangle);

    public abstract void hitboxTile(Rectangle rectangle);

    public boolean collides(SolidEntity other){
        return true;
    }

    public void collision(SolidEntity other, float x, float y){
    }

    public void move(float x, float y){
        EntityCollisions.move(this, x, y);
    }
}
