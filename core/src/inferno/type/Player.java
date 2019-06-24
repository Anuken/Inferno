package inferno.type;

import inferno.Binding;
import io.anuke.arc.Core;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.math.geom.Rectangle;
import io.anuke.arc.math.geom.Vector2;
import io.anuke.arc.util.Time;

public class Player extends Char{
    private static final boolean snap = true;

    private Vector2 movement = new Vector2();
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

        Draw.rect("prince", x, y + 13);

        if(snap){
            x = px;
            y = py;
        }
    }

    @Override
    public void update(){
        movement.set(Core.input.axis(Binding.move_x), Core.input.axis(Binding.move_y)).nor().scl(speed).scl(Time.delta());

        move(movement.x, movement.y);

        if(Core.input.keyTap(Binding.shoot)){
            Fx.spark.at(x, y);
        }
    }

    @Override
    public void hitbox(Rectangle rectangle){
        float w = 6f, h = 8f;
        rectangle.set(x - w / 2f, y, w, h);
    }
}
