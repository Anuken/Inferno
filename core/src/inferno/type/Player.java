package inferno.type;

import inferno.*;
import inferno.entity.*;
import inferno.graphics.*;
import inferno.world.*;
import io.anuke.arc.*;
import io.anuke.arc.collection.*;
import io.anuke.arc.graphics.*;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.math.*;
import io.anuke.arc.math.geom.*;
import io.anuke.arc.util.*;

import static inferno.Inferno.*;

public class Player extends Char{
    private final static boolean snap = true;
    private final static int scytheDamage = 14;
    private final static float speed = 3f, reload = 12f, rotspeed = 18f, slashdur = 6f, slasharc = 210f, slashreload = 100f, scytheJump = 24f;
    private final static Color hand = Color.valueOf("202334").mul(2f);

    private Vector2 movement = new Vector2();
    private Direction direction = Direction.right;
    private Interval timer = new Interval();
    private float scytherot, movetime, glowtime, slashtime = -100f, slashrot;
    private boolean slashdir, hitBoss;

    private Array<Vector3> removals = new Array<>();
    private Array<Vector3> slashes = new Array<>();
    private float px, py;

    @Override
    public void draw(){
        if(snap){
            px = x;
            py = y;
            x = (int)x;
            y = (int)y;
        }

        Draw.mixcol(Color.SCARLET, Mathf.clamp(hitTime));
        TextureRegion region = movetime > 0 ? direction.frames[(int)(movetime / 6) % direction.frames.length] : direction.region;
        TextureRegion scythe = Core.atlas.find("scythe");

        float len = 3f;
        Tmp.v1.set(Core.input.mouseWorld()).sub(x, y + 13f).limit(len);
        int dir = Mathf.sign(direction.flipped);

        Draw.rect(region, x, y + 13, region.getWidth() * -dir, region.getHeight());
        Drawf.z(y + Tmp.v1.y);
        int sdir = Mathf.sign(slashdir);

        float rot = angleOffset();

        Draw.rect("scythe", x + Tmp.v1.x, y + 7 + scythe.getHeight()/2f + Tmp.v1.y, scythe.getWidth() * sdir, scythe.getHeight(), scythe.getWidth()/2f * sdir, 4f, rot);

        if(slashtime > -slashreload/slashdur){
            float fract = (1f - Interpolation.pow10In.apply(-slashtime / (slashreload/slashdur)));

            Draw.alpha(fract);
            Draw.rect("scytheglow", x + Tmp.v1.x, y + 7 + scythe.getHeight()/2f + Tmp.v1.y, scythe.getWidth() * sdir, scythe.getHeight(), scythe.getWidth()/2f * sdir, 4f, rot);
            Draw.color();

            Drawf.light(x + Tmp.v1.x - dir*7f, y + 13 + Tmp.v1.y, 50f * glowtime, Color.WHITE);
        }

        //hands
        /*
        Tmp.v2.trns((50f - scytherot + slashrot), 3f);
        Draw.color(hand);
        Fill.square(x + Tmp.v1.x - dir*(7f + Tmp.v2.x) + 0.5f, y + 14 + Tmp.v1.y + 0.5f + Tmp.v2.y - slashrot/10f, 1f);
        Fill.square(x + Tmp.v1.x - dir*(7f - Tmp.v2.x) + 0.5f, y + 14 + Tmp.v1.y + 0.5f - Tmp.v2.y - slashrot/10f, 1f);
*/

        if(slashes.size > 0){
            removals.clear();
            Color startColor = Color.WHITE, endColor = Color.CYAN;
            float basethick = 30f;

            for(int i = 0; i < slashes.size; i ++){
                Vector3 cur = slashes.get(i);
                float offsetx = x, offsety = y;
                float thick = (1f - cur.z) * basethick * Mathf.lerp((float)i / (slashes.size-1), 1f, 0.4f);
                Draw.color(startColor, endColor, cur.z);

                if(i != slashes.size-1) {
                    Vector3 next = slashes.get(i + 1);

                    Lines.stroke(thick);
                    Lines.line(offsetx + cur.x, offsety + cur.y, offsetx + next.x, offsety + next.y, i == 0 ? CapStyle.round : CapStyle.none, 1f);
                }

                if(!control.isPaused()){
                    cur.z += 1f/slashdur * Time.delta();
                }

                if(cur.z > 1f){
                    removals.add(cur);
                }
            }

            slashes.removeAll(removals);
            removals.clear();
        }

        Draw.color();
        Draw.mixcol();

        Drawf.light(x, y + 10f, 150f, Color.CYAN, 0.75f);

        if(snap){
            x = px;
            y = py;
        }
    }

    public float mouseAngle(){
        return Angles.mouseAngle(x, y + 13f);
    }

    void addSlashPoint(float offset){
        Tmp.v1.set(Core.input.mouseWorld()).sub(x, y + 13f).limit(3f);
        int sdir = Mathf.sign(slashdir);
        float rot = mouseAngle() + slasharc/2f * sdir + (slashtime > 0 ? (slasharc * Mathf.clamp(1f - slashtime)) * -sdir : 0) - 20*sdir;

        Tmp.v2.trns(rot, 26f);
        slashes.add(new Vector3(Tmp.v2.x, 7 + Tmp.v2.y, offset));
    }

    @Override
    public void onDeath(){
        Core.app.post(() -> control.reset());
    }

    @Override
    public float maxHealth(){
        return 60;
    }

    @Override
    public void drawShadow(){
        Draw.rect("circle", (int)x, (int)y, 16f, 7f);
    }

    @Override
    public boolean collides(SolidEntity other){
        return other instanceof Bullet && ((Bullet) other).shooter instanceof Boss && hitTime <= 0f;
    }

    @Override
    public void update(){
        //apply fire damage
        Tile tile = world.tileOpt((int)((x + tilesize/2f) / tilesize), (int)((y + tilesize/2f) / tilesize));
        if(tile.wall != null && tile.wall.damage > 0){
            damage(tile.wall.damage * Time.delta());
            if(Mathf.chance(0.5 * Time.delta())){
                Fx.spark.at(player.x + Mathf.range(4f), player.y + height() + Mathf.range(5f), Pal.player);
            }
        }

        hitTime -= 1f/hitdur;
        movement.set(Core.input.axis(Binding.move_x), Core.input.axis(Binding.move_y)).nor().scl(speed).scl(Time.delta());

        if(!movement.isZero()){
            direction = Direction.fromAngle(movement.angle());
            movetime += Time.delta();
        }else{
            movetime = 0f;
        }

        move(movement.x, movement.y);

        float angle = mouseAngle();
        glowtime = Mathf.lerpDelta(glowtime, 0f, 0.1f);

        if(Core.input.keyTap(Binding.alt) && slashtime <= -slashreload/slashdur && !boss.midSpeech){
            hitBoss = false;
            Tmp.v3.trns(mouseAngle(), scytheJump);
            move(Tmp.v3.x, Tmp.v3.y);
            renderer.jump(mouseAngle() + 180f, 10f);
            renderer.shake(4f, 4f);
            slashtime = 1f;
            addSlashPoint(0);
        }

        if(Core.input.keyDown(Binding.shoot) && !boss.midSpeech){
            scytherot -= Time.delta() * rotspeed;

            if(timer.get(reload) && slashtime <= -1f){
                shoot(Bullets.basic, angle);
                glowtime = 1f;
            }
        }else{
            scytherot = Mathf.slerpDelta(scytherot, slasharc/2f, 0.2f);
        }

        float targetarcrot = slashtime <= 0 ? 0 : (Mathf.clamp(1f - slashtime) - 0.5f) * slasharc;
        slashrot = Mathf.lerp(slashrot, targetarcrot, slashtime > 0 ? 0.8f : 0.2f);

        if(slashtime > -0.7f){
            float qw = 120f;
            float qh = 120f;
            float qx = player.x - qw/2f;
            float qy = player.y - qh/2f;
            float length = 35f;

            bulletGroup.intersect(qx, qy, qw, qh, b -> {
                if(b.shooter.isPlayer() || !b.type.deflect) return;
                b.hitbox(Tmp.r2);

                if(b.withinDst(player.x, player.y + 7, length) && Angles.angleDist(angle, Angles.angle(player.x, player.y + 7f, b.x, b.y)) <= slasharc/2f){
                    b.velocity.setAngle(b.angleTo(player.x, player.y + 7f) + 180f).scl(1.1f);
                    Fx.spark.at(b.x, b.y, Pal.player);
                    b.shooter = this;
                    b.mover = f -> Vector2.ZERO;
                    b.velocity.setLength(b.type.speed);
                    //control.slowmo();
                }
            });

            boolean hitsBoss = boss.withinDst(player.x, player.y + 7f, length) && Angles.angleDist(angle, Angles.angle(player.x, player.y + 7f, boss.x, boss.y + 7f)) <= slasharc/2f;

            if(hitsBoss){
                if(!hitBoss){
                    boss.damage(scytheDamage);
                    Fx.spark.at(boss.x, boss.y, Pal.player);
                    renderer.jump(-mouseAngle(), 30f);
                    renderer.shake(4f, 3f);
                    hitBoss = true;
                    //control.slowmo();
                }

                boss.move(Tmp.v1.trns(angleTo(boss), 1.5f));
            }
        }

        if(slashtime > 0){
            float increment = 1f / slashdur * 0.01f;
            float toMove = Time.delta() / slashdur;
            for(float f = 0; f < toMove && slashtime > 0; f += increment){
                addSlashPoint(toMove - f);
                slashtime -= increment;
            }

            slashtime -= (toMove % increment);

            if(slashtime < increment){
                slashtime = -.0001f;
            }

            if(slashtime <= 0){
                slashdir = !slashdir;
            }
        }else{
            slashtime -= Time.delta() / slashdur;
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

    public float angleOffset(){
        int sdir = Mathf.sign(slashdir);
        return mouseAngle() - 90 + scytherot * sdir + (slashtime > 0 ? (slasharc * Mathf.clamp(1f - Interpolation.pow3In.apply(slashtime))) * -sdir : 0);
    }
}
