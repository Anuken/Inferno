package inferno.type;

import inferno.Binding;
import inferno.graphics.Layer;
import io.anuke.arc.Core;
import io.anuke.arc.collection.Array;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.math.*;
import io.anuke.arc.math.geom.Rectangle;
import io.anuke.arc.math.geom.Vector2;
import io.anuke.arc.util.Time;
import io.anuke.arc.util.Tmp;

public class Player extends Char{
    private static final boolean snap = true;
    private final static float speed = 5f;
    private static final Color hand = Color.valueOf("58adb6");

    private Vector2 movement = new Vector2();
    private Direction direction = Direction.right;

    private Array<Vector2> slashes = new Array<>();

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
        TextureRegion scythe = Core.atlas.find("scythe");

        float len = 3f;
        Tmp.v1.set(Core.input.mouseWorld()).sub(x, y + 13f).limit(len);
        int dir = Mathf.sign(direction.flipped);

        Draw.rect(region, x, y + 13, region.getWidth() * -dir, region.getHeight());

        Layer.z(y + Tmp.v1.y);

        Draw.rect("scythe", x + Tmp.v1.x - dir*7f, y + 13 + Tmp.v1.y, scythe.getWidth() * -dir, scythe.getHeight(), 50f * dir);
        Draw.color(hand);
        Fill.square(x + Tmp.v1.x - dir*9f + 0.5f, y + 15 + Tmp.v1.y + 0.5f, 1f);
        Fill.square(x + Tmp.v1.x - dir*5f + 0.5f, y + 11 + Tmp.v1.y + 0.5f, 1f);

        Draw.color();

        Layer.light(x, y + 10f, 150f, Color.CYAN, 0.75f);

        if(snap){
            x = px;
            y = py;
        }
    }

    @Override
    public void drawShadow(){
        Draw.rect("circle", (int)x, (int)y, 16f, 7f);
    }

    @Override
    public void update(){
        movement.set(Core.input.axis(Binding.move_x), Core.input.axis(Binding.move_y)).nor().scl(speed).scl(Time.delta());

        if(!movement.isZero()){
            direction = Direction.fromAngle(movement.angle());
        }

        move(movement.x, movement.y);

        float angle = Angles.mouseAngle(x, y + 13f);

        if(Core.input.keyDown(Binding.shoot)){
            shoot(Bullets.basic, angle);
        }

        direction = Direction.fromAngle(angle);
    }

    @Override
    public void hitbox(Rectangle rectangle){
        float w = 6f, h = 8f;
        rectangle.set(x - w / 2f, y, w, h);
    }
}
