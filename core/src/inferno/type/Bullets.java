package inferno.type;

import inferno.graphics.Drawf;
import inferno.graphics.Pal;
import inferno.world.Tile;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.Time;
import arc.util.Tmp;

import static inferno.Inferno.*;
import static arc.math.Angles.circle;
import static arc.math.Angles.loop;
import static arc.util.Time.run;

public class Bullets{
    public static final BulletType

    basic = new BulletType(){
        {
            speed = 7f;
            lightColor = Pal.player;
            damage = 1f;
            lifetime = 50f;
        }

        @Override
        public void draw(Bullet bullet){
            Draw.color(Pal.player);
            Fill.circle(bullet.x, bullet.y, 5f);
            Draw.color(Color.white);
            Fill.circle(bullet.x, bullet.y, 2f);
        }

    },
    lbasic = new BulletType(){
        {
            speed = 3f;
            damage = 5;
            lightColor = Pal.lucine;
        }

        @Override
        public void draw(Bullet bullet){
            Draw.color(Pal.lucine);
            Fill.circle(bullet.x, bullet.y, 5f);
            Draw.color(Color.white);
            Fill.circle(bullet.x, bullet.y, 2f);
        }
    },
    lbasicslow = new BulletType(){
        float fs = 0.1f;
        {
            speed = 1.5f;
            damage = 5;
            lightColor = Pal.lucine;
        }

        @Override
        public void draw(Bullet bullet){
            float sizemult = Mathf.clamp(bullet.fin() / fs);

            Draw.color(Pal.lucine);
            Fill.circle(bullet.x, bullet.y, 5f * sizemult);
            Draw.color(Color.white);
            Fill.circle(bullet.x, bullet.y, 2f * sizemult);
        }

        @Override
        public void drawLight(Bullet bullet){
            if(light <= 0) return;

            Drawf.light(bullet.x, bullet.y, light *  Mathf.clamp(bullet.fin() / fs), lightColor);
        }
    },
    lfast = new BulletType(){
        {
            speed = 7f;
            damage = 6;
            lightColor = Pal.lucine;
        }

        @Override
        public void draw(Bullet bullet){
            Draw.color(Pal.lucine);
            Draw.rect("circle", bullet.x, bullet.y, 14f, 9f, bullet.angle());
            Draw.color(Color.white);
            Draw.rect("circle", bullet.x, bullet.y, 6f, 3.5f, bullet.angle());
        }
    },
    firebreath = new BulletType(){
        {
            speed = 2f;
            lightColor = Pal.candle;
            lifetime = 500f;
            size = 10f;
            deflect = false;
            damage = 15f;
        }

        @Override
        public void draw(Bullet bullet){
            Drawf.z(bullet.y - tilesize*4f);

            float glow = Mathf.absin(Time.time(), 5f, 0.5f);

            Draw.color(Pal.fireball, Color.white, glow);
            Draw.alpha(0.3f);
            Fill.circle(bullet.x, bullet.y, 10f + Mathf.absin(Time.time(), 6f, 4f));

            Draw.color(Pal.fireball, Color.white, glow);
            Fill.circle(bullet.x, bullet.y, 7f);

            Draw.color(Color.white);
            Fill.circle(bullet.x, bullet.y, 4f);
        }

        @Override
        public void update(Bullet bullet){
            if(Mathf.chance(0.15 * Time.delta())){
                Fx.fireballtrail.at(bullet.x, bullet.y, Pal.fireball);
            }

            if(Mathf.chance(0.02 * Time.delta())){
                float s = 0f;
                float aimc = 0.2f;
                bullet.shooter.shoot(breathsmall, bullet.x, bullet.y, Mathf.chance(aimc) ? bullet.angleTo(player) : bullet.angle() + 110f - s);
                bullet.shooter.shoot(breathsmall, bullet.x, bullet.y, Mathf.chance(aimc) ? bullet.angleTo(player) : bullet.angle() - 110f + s);
            }
        }
    },
    breathsmall = new BulletType(){
        {
            speed = 1.2f;
            lightColor = Pal.candle;
            lifetime = 600f;
            damage = 8f;
        }

        @Override
        public void draw(Bullet bullet){
            Draw.color(Pal.candle);
            Fill.circle(bullet.x, bullet.y, 5f);
            Draw.color(Color.white);
            Fill.circle(bullet.x, bullet.y, 2f);
        }

        @Override
        public boolean solid(int x, int y){
            Tile tile = world.tileOpt(x, y);
            if(tile != null && tile.wall != null && tile.wall.clear){
                return false;
            }
            return world.solid(x, y);
        }
    },
    breath2 = new BulletType(){
        {
            speed = 2f;
            lightColor = Pal.candle;
            lifetime = 600f;
            damage = 8f;
        }

        @Override
        public void draw(Bullet bullet){
            Drawf.z(bullet.y - tilesize*4f);
            Draw.color(Pal.candle);
            Fill.circle(bullet.x, bullet.y, 5f);
            Draw.color(Color.white);
            Fill.circle(bullet.x, bullet.y, 2f);
        }
    },
    breathfast = new BulletType(){
        {
            speed = 7f;
            lightColor = Pal.candle;
            lifetime = 600f;
            damage = 8f;
        }

        @Override
        public void draw(Bullet bullet){
            Drawf.z(bullet.y - tilesize*4f);
            Draw.color(Pal.candle);
            Draw.rect("circle", bullet.x, bullet.y, 14f, 9f, bullet.angle());
            Draw.color(Color.white);
            Draw.rect("circle", bullet.x, bullet.y, 6f, 3.5f, bullet.angle());
        }
    },
    fireball = new BulletType(){
        {
            speed = 3f;
            lightColor = Color.orange;
            light = 120f;
            size = 10f;
            lifetime = 1000f;
            deflect = false;
            damage = 30f;
        }

        @Override
        public void init(Bullet bullet){
            super.init(bullet);
            renderer.shake(5f);
        }

        @Override
        public void update(Bullet bullet){
            super.update(bullet);

            if(Mathf.chance(0.3 * Time.delta())){
                Fx.fireballtrail.at(bullet.x, bullet.y, Tmp.c1.set(Pal.fireball).lerp(Color.white, 0.1f + bullet.fin() * 0.6f));
            }
        }

        @Override
        public void hit(Bullet bullet){
            super.hit(bullet);

            loop(7, i -> run(3f * i, () -> circle(30, i * 9f, f -> bullet.shooter.shoot(candle, bullet.x, bullet.y, f))));
            renderer.shake(10f);

            float aim = bullet.velocity.angle();
            run(15f, () -> loop(7, i -> {
                run(10f + i * 4, () -> circle(7, f -> boss.shoot(candle, bullet.x, bullet.y, f + 25 + aim)));
                run(i * 4, () -> circle(7, f -> boss.shoot(candle, bullet.x, bullet.y, f + aim)));
            }));

            Fx.fireballfire.at(bullet.x, bullet.y);
        }

        @Override
        public void despawn(Bullet bullet){
            hit(bullet);
        }

        @Override
        public void draw(Bullet bullet){
            Drawf.z(bullet.y - tilesize*4f);
            float glow = 0.1f + bullet.fin() * 0.6f;//Mathf.absin(Time.time(), 5f, 0.5f);

            Draw.color(Pal.fireball, Color.white, glow);
            Draw.alpha(0.3f);
            Fill.circle(bullet.x, bullet.y, 20f + Mathf.absin(Time.time(), 6f, 4f) + bullet.fin() * 4f);

            Draw.color(Pal.fireball, Color.white, glow);
            Fill.circle(bullet.x, bullet.y, 14f);

            Draw.color(Color.white, Color.white, glow);
            Fill.circle(bullet.x, bullet.y, 8f);
        }
    },
    candle = new BulletType(){
        {
            speed = 2.5f;
            lightColor = Pal.candle;
            damage = 8;
            lifetime = 500f;
        }

        @Override
        public void draw(Bullet bullet){
            Draw.color(Pal.candle);
            Fill.circle(bullet.x, bullet.y, 5f);
            Draw.color(Color.white);
            Fill.circle(bullet.x, bullet.y, 2f);
        }
    },
    meteor = new BulletType(){
        {
            pierce = true;
            speed = 0.001f;
            light = 60f;
            lifetime = 10f;
            size = 50f;
            shake = 4f;
            deflect = false;
            lightColor = Pal.lucine;
            damage = 15f;
        }

        @Override
        public void init(Bullet bullet){
            super.init(bullet);
            Fx.meteorpost.at(bullet.x, bullet.y);
        }

        @Override
        public void draw(Bullet b){
            Drawf.z(100000f);
            Draw.color(Color.white, Pal.lucine, b.fin());
            Lines.circle(b.x, b.y, 40f * b.fin());

            Draw.color(Color.white);
            Draw.alpha(b.fout());
            Fill.circle(b.x, b.y, b.fout() * 40f);

            Draw.color(Pal.lucine);
            Draw.alpha(b.fout());
            Drawf.symbols(b.id, b.x, b.y, 40f);
        }
    },
    laser = new BulletType(){
        {
            damage = 15;
            lifetime = 30f;
            shake = 5f;
        }

        @Override
        public void init(Bullet bullet){
            super.init(bullet);
        }

        @Override
        public void draw(Laser laser){
            float f = laser.fout(Interp.exp5Out);
            Lines.stroke(14f * f, Pal.fireball);
            Lines.lineAngle(laser.x, laser.y, laser.angle, Laser.length);

            Lines.stroke(6f * f, Color.white);
            Lines.lineAngle(laser.x, laser.y, laser.angle, Laser.length);

            Tmp.v1.trns(laser.angle, Laser.length);

            Drawf.light(Color.white, 1f, () -> {
                Lines.stroke(20f * f);
                Lines.lineAngle(laser.x, laser.y, laser.angle, Laser.length, CapStyle.round);
            });
        }

    };
}
