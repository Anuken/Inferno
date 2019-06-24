package inferno.type;

import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.Fill;
import io.anuke.arc.math.geom.Rectangle;

public class Boss extends Char{

    @Override
    public void draw(){
        Draw.color(Color.RED);
        Fill.square(x, y + 3f, 3f);
        Draw.color();
    }

    @Override
    public void hitbox(Rectangle rectangle){
        float w = 4f, h = 4f;
        rectangle.set(x - w / 2f, y, w, h);
    }
}
