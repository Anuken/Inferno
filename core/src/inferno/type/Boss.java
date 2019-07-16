package inferno.type;

import inferno.entity.SolidEntity;
import inferno.graphics.Drawf;
import inferno.graphics.Pal;
import inferno.type.Bullet.*;
import inferno.type.boss.Phase;
import inferno.type.boss.Phases;
import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.arc.math.geom.Rectangle;
import io.anuke.arc.util.Time;

import static inferno.Inferno.*;
import static io.anuke.arc.math.Angles.circle;

public class Boss extends Char{
    Direction direction = Direction.down;
    boolean dialogged, midSpeech;
    Phase phase = Phases.phases.first();

    @Override
    public void onDeath(){
        dialogged = false;
        int index = Phases.phases.indexOf(phase);

        //proceed to next phase
        if(index < Phases.phases.size - 1){
            nextPhase(Phases.phases.get(index + 1));
        }else{
            circle(100, f -> shoot(Bullets.basic, f));
            remove();
        }
    }

    public void nextPhase(Phase phase){
        heal();
        dead = false;
        this.phase = phase;
        dialogged = false;
        player.heal();
        bulletGroup.all().each(b -> {
            Fx.spark.at(b.x, b.y, b.type.lightColor);
        });
        Time.clear();
        bulletGroup.clear();
    }

    public void reset(){
        phase = Phases.phases.first();
    }

    @Override
    public void update(){
        if(!dialogged){
            midSpeech = true;
            Time.run(Phases.phases.first() == phase ? 0f : 60f, () -> {
                ui.displayText(phase.startText);
                midSpeech = false;
            });
            dialogged = true;
        }

        hitTime -= 1f/hitdur;
        phase.update();
        direction = Direction.fromAngle(angleTo(player));
    }

    @Override
    public float maxHealth(){
        return 300;
    }

    @Override
    public void draw(){
        Draw.mixcol(Color.WHITE, Mathf.clamp(hitTime));
        TextureRegion region = Core.atlas.find("lucine-" + direction.name);
        Draw.rect(region, x, y + region.getHeight()/2f, region.getWidth() * -Mathf.sign(direction.flipped), region.getHeight());

        Drawf.light(x, y + height(), 160f, Color.SCARLET);

        Draw.mixcol();
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

    public void dash(float speed, Runnable done){
        float seg = 10f;
        float moved = speed;
        int i = 0;
        while(moved > seg){
            Time.run(i++ * 1f, () -> {
                toward(player, seg);
                Fx.dash.at(x, y + 6f, angleTo(player) + 180f, Pal.lucine);
            });

            moved -= seg;
        }

        Time.run(i * 1f, done);

        toward(player, moved);
        Fx.wave.at(x, y);
        renderer.shake(5f);
    }

    public void shoot(float angle, Mover mover){
        shoot(Bullets.lbasic, angle, mover);
    }

    public void shoot(float angle){
        shoot(Bullets.lbasic, angle);
    }

    public void shootf(float angle){
        shoot(Bullets.lfast, angle);
    }

    public float aim(){
        return angleTo(player);
    }

    public boolean seesPlayer(){
        return !Geometry.raycast(world.world(x), world.world(y), world.world(player.x), world.world(player.y),
            (x, y) -> world.solid(x, y));
    }
}
