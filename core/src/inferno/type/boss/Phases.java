package inferno.type.boss;

import inferno.type.*;
import io.anuke.arc.collection.Array;
import io.anuke.arc.function.Consumer;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Vector2;

import static inferno.Inferno.player;
import static inferno.Inferno.world;
import static io.anuke.arc.math.Angles.*;
import static io.anuke.arc.util.Time.run;

@SuppressWarnings("unchecked")
public class Phases{

    //fun dragonfire attack
    /*
    Vector2 s = world.statue();
    loop(60, i -> run(i * 3f, () -> shotgun(2 + i %10, 4f + (i + 5) % 10, 270f, f -> boss.shoot(Bullets.firebreath, s.x, s.y, f))));
     */

    private static final Array<Consumer<Boss>> attacks = Array.with(
        //rays
        boss -> {
            float aim =  boss.aim();
            loop(7, i -> {
                run(10f + i * 4, () -> circle(5, f -> boss.shoot(f + 36 + aim)));
                run(i * 4, () -> circle(5, f -> boss.shoot(f + aim)));
            });
        },
        //star
        boss -> {
            float aim =  boss.aim();
            int length = 15;
            loop(length - 1, i -> {
                run(i * 2, () -> circle(5, f -> shotgun(2, 360f / 5 * i/(float)length, f + aim, boss::shoot)));
            });
        },

        //waves
        boss -> {
            float aim =  boss.aim();
            loop(20, i -> {
                run(i * 2, () -> boss.shoot(aim + 50f - i *5));
                run(i * 2, () -> boss.shoot(aim - 50f + i *5));
            });
        },

        //shotgun rays
        boss -> {
            float aim =  boss.aim();
            loop(20, i -> {
                run(i * 2, () -> shotgun(3, 15f, aim + Mathf.sin(i, 1f, 5f), boss::shoot));
            });
        },

        //circle of bullets
        boss -> {
            float aim =  boss.aim();
            loop(40, i -> {
                run(i * 3, () -> shotgun(2, 180f, aim + i *10f, boss::shoot));
            });
        }
    );

    private static final Array<Consumer<Boss>> cycle = Array.with(
        //teleport
    /*
        boss -> {
            Fx.wave.at(boss.x, boss.y);
            run(15f, () -> {
                Tmp.v1.trns(player.mouseAngle(), 40f);
                boss.set(player.x + Tmp.v1.x, player.y + Tmp.v1.y);
                boss.hitboxTile(Tmp.r1);
                //warp at player, TODO fix
                if(EntityCollisions.overlapsTile(Tmp.r1)){
                    boss.set(player.x, player.y);
                }
                Fx.wave.at(boss.x, boss.y);
            });
        },

        //meteors
        boss -> {
            PositionConsumer met = (x, y) -> {
                Fx.meteorpre.at(x, y);
                run(Fx.meteorpre.lifetime, () -> {
                    boss.shoot(Bullets.meteor, x, y, 0f);
                });
            };

            for(int i = 0; i < 30; i++){
                run(Mathf.random(30f), () -> met.accept(player.x + Mathf.range(400f), player.y + Mathf.range(400f)));
            }

            met.accept(player.x, player.y);
        }


        //candles
        boss -> {
            for(Point2 tile : world.candles()){
                float x = tile.x * tilesize, y = tile.y * tilesize + 14f;

                Fx.candlespiral.at(x, y);

                run(Fx.candlespiral.lifetime, () -> {
                    circle(20, f -> boss.shoot(Bullets.candle, x, y, f));
                    Fx.candlefire.at(x, y);
                    renderer.shake(4f);
                });
            }
        }

        //ball
        boss -> {
            Bullet b = Bullet.shoot(Bullets.fireball, boss, boss.x, boss.y + boss.height(), boss.aim());
            b.lifetime = b.dst(player) / b.type.speed;
        }*/



        //dragonfire
        boss -> {
            Vector2 s = world.statue();
            loop(20, i -> run(i * 8f, () -> boss.shoot(Bullets.firebreath, s.x, s.y, 270f + Mathf.range(7f))));
        }
    );

    public static final Phase

    first = new Phase(){
        @Override
        public void update(){
            if(time.get(60f * 2f)){
                cycle.random().accept(boss);

            //    loop(8, j -> Time.run(j * 5, () -> circle(5, j * 5f, f -> shotgun(10, 5f, f, i -> boss.shoot(Bullets.lbasic, i)))));
            }

            if(time.get(1, 130f) && boss.seesPlayer()){
                //boss.dash(boss.dst(player) / 1.5f);
                //loop(8, i -> {
                //    run(i * 2, () -> shotgun(6, 12f, boss.aim() + i * 4, this::shoot));
                //});
            }

            //boss.toward(player, 0.9f);

            if(time.get(2, 140f) && !boss.seesPlayer()){
                Fx.wave.at(boss.x, boss.y);
                run(5f, () -> {
                    boss.set(player.x, player.y);
                    Fx.wave.at(boss.x, boss.y);
                });
            }
        }
    };
}
