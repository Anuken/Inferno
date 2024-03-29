package inferno.type;

import inferno.*;
import inferno.entity.*;
import inferno.graphics.*;
import inferno.type.Bullet.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;

import static inferno.Inferno.*;
import static arc.math.Angles.circle;

public class Boss extends Char{
    public static final Anim adash = new Anim("lucine-sprint"), awave = new Anim("lucine-wave-1", "lucine-wave-2");

    Direction direction = Direction.down;
    boolean dialogged, midSpeech;
    Phases.Phase phase = Phases.phases.first();
    Anim anim = null;
    float animdur, animtime;

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

    //switches to next phase, for mid phases only
    public void midPhase(){
        dialogged = false;
        phase = Phases.phases.get(Phases.phases.indexOf(phase) + 1);
        bulletGroup.all().each(b -> Fx.spark.at(b.x, b.y, b.type.lightColor));
        Time.clear();
        bulletGroup.clear();
    }

    public void nextPhase(Phases.Phase phase){
        heal();
        phase.reset();
        dead = false;
        this.phase = phase;
        dialogged = false;
        phase.begin();
        player.health += player.maxHealth()/2f;
        player.health = Mathf.clamp(player.health, 0, player.maxHealth());
        effectGroup.clear();
        bulletGroup.all().each(b -> Fx.spark.at(b.x, b.y, b.type.lightColor));
        Time.clear();
        bulletGroup.clear();
    }

    public void reset(){
        world.wallUndetonate();
        phase = Phases.phases.get(Inferno.debug ? Inferno.debugPhase : 0);
        if(debug){
            world.wallDetonate();
            world.wallExtinguish();
        }
        phase.reset();
        phase.begin();
    }

    public void anim(Anim anim, float duration){
        this.anim = anim;
        this.animdur = duration;
        this.animtime = 0f;
    }

    @Override
    public void update(){
        if(!dialogged && phase.startText != null){
            midSpeech = true;
            Time.run(Phases.phases.first() == phase ? 0f : 60f, () -> {
                if(!debug){
                    ui.displayText(phase.startText);
                }
                midSpeech = false;
            });
            dialogged = true;
        }

        hitTime -= 1f/hitdur*Time.delta;
        if(!midSpeech){
            phase.update();
            renderer.ambient.lerp(phase.ambient, 0.1f);
        }
        direction = player.x < x ? Direction.left : Direction.right;

        if(anim != null){
            animtime += Time.delta / animdur;

            if(animtime >= 1f){
                anim = null;
            }
        }
    }

    @Override
    public float maxHealth(){
        return 350;
    }

    @Override
    public void draw(){
        //dragon susbstitute, don't draw
        if(isStatue()){
            drawStatue();
        }else{

            Draw.mixcol(Color.white, Mathf.clamp(hitTime));
            TextureRegion region = anim == null ? Core.atlas.find("lucine-side") : anim.frame(animtime);
            Draw.rect(region, x, y + region.height / 2f + Mathf.absin(Time.time, 6f, 2f), region.width * -Mathf.sign(direction.flipped), region.height);

            Drawf.light(x, y + height(), 160f, Color.scarlet);

            Draw.mixcol();

            Draw.color(Pal.lucine, Color.white, Mathf.clamp(hitTime));
            Drawf.z(y + 600f);
            Lines.stroke(2f);
            //Lines.swirl(x, y, 20f, health / maxHealth(), Time.time * 2f);
            Draw.reset();
        }
    }

    boolean isStatue(){
        return phase == Phases.phases.get(4);
    }

    void drawStatue(){
        float x = 40.5f * tilesize, y = (world.height() - 10.5f) * tilesize;
        Drawf.z(y - 1f);
        TextureRegion r = Core.atlas.find("statue-enraged");
        Draw.mixcol(Color.white, hitTime /2f);
        Draw.rect(r, x, y + r.height/2f);
        Draw.reset();
        Drawf.light(x, y + r.height/2f, 150f + Mathf.absin(Time.time, 6f, 10f), Color.red, 0.9f);
    }

    @Override
    public void move(float x, float y){
        super.move(x, y);
    }

    @Override
    public void hitbox(Rect rectangle){
        float w = isStatue() ? tilesize * 6f : 12f, h = isStatue() ? tilesize * 5 : 24f;
        rectangle.set(x - w / 2f, y - (isStatue() ? h /2f : 0), w, h);
    }

    @Override
    public void hitboxTile(Rect rectangle){
        float w = 7, h = 12f;
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

    public float dash(float speed, Runnable done){
        float seg = 10f;
        float moved = speed;
        int i = 0;
        while(moved > seg){
            Time.run(i++ * 1f, () -> {
                toward(player, seg);
                Fx.dash.at(x, y + 6f, angleTo(player) + 180f, Pal.lucine);
                Fx.ldash.at(x, y, direction.flipped ? -1f : 1f);
            });

            moved -= seg;
        }

        Time.run(i * 1f, done);

        toward(player, moved);
        Fx.wave.at(x, y);
        renderer.shake(5f);
        return i * 1f;
    }

    public void shoot(float angle, Mover mover){
        shoot(Bullets.lbasic, angle, mover);
    }

    public void shoot(float angle){
        shoot(Bullets.lbasic, angle);
    }

    public void laser(BulletType type, float angle){
        Fx.indlaser.at(x, y, angle);
        Time.run(Fx.indlaser.lifetime + 1f, () -> {
            new Laser(type, x, y, angle).add();
        });
    }

    public void laser(BulletType type, float x, float y, float angle){
        Fx.indlaser.at(x, y, angle);
        Time.run(Fx.indlaser.lifetime + 1f, () -> {
            new Laser(type, x, y, angle).add();
        });
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
