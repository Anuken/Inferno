package inferno.type;

import inferno.entity.SolidEntity;
import inferno.graphics.Layer;
import inferno.graphics.Pal;
import inferno.type.boss.Phase;
import inferno.type.boss.Phases;
import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.arc.math.geom.Rectangle;

import static inferno.Inferno.*;
import static io.anuke.arc.math.Angles.circle;

public class Boss extends Char{
    Direction direction = Direction.down;
    Phase phase = Phases.first;

    @Override
    public void onDeath(){
        circle(100, f -> shoot(Bullets.basic, f));
        remove();
    }

    @Override
    public void update(){
        phase.boss = this;
        phase.update();
        direction = Direction.fromAngle(angleTo(player));
    }

    @Override
    public float maxHealth(){
        return 100;
    }

    @Override
    public void draw(){
        TextureRegion region = Core.atlas.find("lucine-" + direction.name);
        Draw.rect(region, x, y + region.getHeight()/2f, region.getWidth() * -Mathf.sign(direction.flipped), region.getHeight());

        Layer.light(x, y + height(), 160f, Color.SCARLET);
    }

    @Override
    public void move(float x, float y){
        super.move(x, y);
    }

    @Override
    public void hitbox(Rectangle rectangle){
        float w = 12f, h = 24f;
        rectangle.set(x - w / 2f, y, w, h);
    }

    @Override
    public boolean collides(SolidEntity other){
        return other instanceof Bullet && ((Bullet) other).shooter instanceof Player;
    }

    @Override
    public void drawShadow(){
        Draw.rect("circle", (int)x, (int)y, 16f, 7f);
    }

    public void dash(float speed){
        float seg = 10f;
        float moved = speed;
        while(moved > seg){
            toward(player, seg);
            moved -= seg;
            Fx.dash.at(x, y + 6f, angleTo(player) + 180f, Pal.lucine);
        }

        toward(player, moved);
        Fx.wave.at(x, y);
        renderer.shake(5f);
    }

    public void shoot(float angle){
        boss.shoot(Bullets.lbasic, angle);
    }

    public float aim(){
        return angleTo(player);
    }

    public boolean seesPlayer(){
        return !Geometry.raycast(world.world(x), world.world(y), world.world(player.x), world.world(player.y),
            (x, y) -> world.solid(x, y));
    }
}
