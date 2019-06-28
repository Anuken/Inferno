package inferno.type;

import inferno.Binding;
import inferno.entity.SolidEntity;
import inferno.graphics.*;
import io.anuke.arc.Core;
import io.anuke.arc.collection.Array;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.math.*;
import io.anuke.arc.math.geom.Rectangle;
import io.anuke.arc.math.geom.Vector2;
import io.anuke.arc.util.*;

public class Player extends Char{
    private static final boolean snap = true;
    private final static float speed = 3f;
    private final static float reload = 12f;
    private final static float rotspeed = 14f;
    private final static int[] seq = {2, 1, 0, 1};
    private static final Color hand = Color.valueOf("202334");

    private Vector2 movement = new Vector2();
    private Direction direction = Direction.right;
    private Interval timer = new Interval();
    private float scytherot, movetime, glowtime;

    private TextureRegion[] animation = new TextureRegion[3];
    private Array<Vector2> slashes = new Array<>();

    private float px, py;

    public Player(){
        for(int i = 0; i < 3; i++){
            animation[i] = Core.atlas.find("prince-move" + i);
        }
    }

    @Override
    public void draw(){
        if(snap){
            px = x;
            py = y;
            x = (int)x;
            y = (int)y;
        }

        TextureRegion region = movetime > 0 ? animation[seq[(int)(movetime / 6) % seq.length]] : Core.atlas.find("prince");
        TextureRegion scythe = Core.atlas.find("scythe");

        float len = 3f;
        Tmp.v1.set(Core.input.mouseWorld()).sub(x, y + 13f).limit(len);
        int dir = Mathf.sign(direction.flipped);

        Draw.rect(region, x, y + 13, region.getWidth() * -dir, region.getHeight());

        Layer.z(y + Tmp.v1.y);

        Draw.rect("scythe", x + Tmp.v1.x - dir*7f, y + 13 + Tmp.v1.y, scythe.getWidth() * -dir, scythe.getHeight(), (50f + scytherot) * dir);

        if(glowtime > 0f){
            Draw.alpha(glowtime);
            Draw.rect("scytheglow", x + Tmp.v1.x - dir*7f, y + 13 + Tmp.v1.y, scythe.getWidth() * -dir, scythe.getHeight(), (50f + scytherot) * dir);
            Draw.color();

            Layer.light(x + Tmp.v1.x - dir*7f, y + 13 + Tmp.v1.y, 50f * glowtime, Color.WHITE);
        }

        Tmp.v2.trns((50f - scytherot), 3f);
        Draw.color(hand);
        Fill.square(x + Tmp.v1.x - dir*(7f + Tmp.v2.x) + 0.5f, y + 14 + Tmp.v1.y + 0.5f + Tmp.v2.y, 1f);
        Fill.square(x + Tmp.v1.x - dir*(7f - Tmp.v2.x) + 0.5f, y + 14 + Tmp.v1.y + 0.5f - Tmp.v2.y, 1f);

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
    public boolean collides(SolidEntity other){
        return other instanceof Bullet && ((Bullet) other).shooter instanceof Boss;
    }

    @Override
    public void update(){
        movement.set(Core.input.axis(Binding.move_x), Core.input.axis(Binding.move_y)).nor().scl(speed).scl(Time.delta());

        if(!movement.isZero()){
            direction = Direction.fromAngle(movement.angle());
            movetime += Time.delta();
        }else{
            movetime = 0f;
        }

        move(movement.x, movement.y);

        float angle = Angles.mouseAngle(x, y + 13f);
        glowtime = Mathf.lerpDelta(glowtime, 0f, 0.1f);

        if(Core.input.keyDown(Binding.shoot)){
            scytherot += Time.delta() * rotspeed;

            if(timer.get(reload)){
                shoot(Bullets.basic, angle);
                glowtime = 1f;
            }
        }else{
            scytherot = Mathf.slerpDelta(scytherot, 0f, 0.2f);
        }

        direction = Direction.fromAngle(angle);
    }

    @Override
    public void shoot(BulletType type, float rot){
        TextureRegion scythe = Core.atlas.find("scythe");
        int dir = Mathf.sign(direction.flipped);
        Tmp.v1.set(Core.input.mouseWorld()).sub(x, y + 13f).limit(3f);
        float rotation = (0f + scytherot) * dir + (direction.flipped ? 180 : 0);
        float offsetx = 0;

        Tmp.v2.trns(rotation, scythe.getHeight()/2f);

        float fx = x + Tmp.v1.x - dir*7f + offsetx + Tmp.v2.x, fy = y + 13 + Tmp.v1.y + Tmp.v2.y;
        float angle = Angles.mouseAngle(fx, fy);

        Bullet.shoot(type, this, fx, fy, angle);
        Effects.effect(Fx.playershoot, fx, fy, angle);
    }

    @Override
    public void hitbox(Rectangle rectangle){
        float w = 6f, h = 8f;
        rectangle.set(x - w / 2f, y, w, h);
    }
}
