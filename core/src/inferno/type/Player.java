package inferno.type;

import inferno.Binding;
import inferno.graphics.Layer;
import io.anuke.arc.Core;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.math.*;
import io.anuke.arc.math.geom.Rectangle;
import io.anuke.arc.math.geom.Vector2;
import io.anuke.arc.util.Time;

public class Player extends Char{
    private static final boolean snap = true;

    private Vector2 movement = new Vector2();
    private Direction direction = Direction.right;
    private float speed = 5f;

    private float px, py;

    @Override
    public void draw(){
        if(snap){
            px = x;
            py = y;
            x = (int)x;
            y = (int)y;
        }

        TextureRegion region = Core.atlas.find("prince");

        Draw.rect(region, x, y + 13, region.getWidth() * -Mathf.sign(direction.flipped), region.getHeight());

        if(snap){
            x = px;
            y = py;
        }
    }

    @Override
    public void drawLight(){
        Layer.light(x, y, 150f);
    }

    @Override
    public void update(){
        movement.set(Core.input.axis(Binding.move_x), Core.input.axis(Binding.move_y)).nor().scl(speed).scl(Time.delta());

        if(!movement.isZero()){
            direction = Direction.fromAngle(movement.angle());
        }

        move(movement.x, movement.y);

        if(Core.input.keyDown(Binding.shoot)){
            shoot(Bullets.basic, Angles.mouseAngle(x, y));
        }
    }

    @Override
    public void hitbox(Rectangle rectangle){
        float w = 6f, h = 8f;
        rectangle.set(x - w / 2f, y, w, h);
    }
}
