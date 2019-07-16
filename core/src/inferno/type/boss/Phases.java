package inferno.type.boss;

import inferno.entity.*;
import inferno.type.*;
import io.anuke.arc.collection.*;
import io.anuke.arc.function.*;
import io.anuke.arc.math.*;
import io.anuke.arc.math.geom.*;
import io.anuke.arc.util.*;

import static inferno.Inferno.*;
import static io.anuke.arc.math.Angles.*;
import static io.anuke.arc.math.Mathf.*;
import static io.anuke.arc.util.Time.run;

public class Phases{

    //fun dragonfire attack
    /*
    Vector2 s = world.statue();
    loop(60, i -> run(i * 3f, () -> shotgun(2 + i %10, 4f + (i + 5) % 10, 270f, f -> boss.shoot(Bullets.firebreath, s.x, s.y, f))));
     */

    private static final Array<Runnable> allAttacks = Array.with(
    //rays
    () -> {
        float aim =  boss.aim();
        loop(7, i -> {
            run(10f + i * 4, () -> circle(5, f -> boss.shoot(f + 36 + aim)));
            run(i * 4, () -> circle(5, f -> boss.shoot(f + aim)));
        });
    },
    //star flower
    () -> {
        float aim =  boss.aim();
        int length = 15;
        loop(length - 1, i -> {
            run(i * 2, () -> circle(5, f -> shotgun(2, 360f / 5 * i/(float)length, f + aim, boss::shoot)));
            run(i * 2 + 30, () -> circle(5, f -> shotgun(2, 360f / 5 * i/(float)length, f + aim + 180, boss::shoot)));
        });
    },

    //waves
    () -> {
        float aim =  boss.aim();
        loop(20, i -> {
            run(i * 2, () -> boss.shoot(aim + 50f - i *5));
            run(i * 2, () -> boss.shoot(aim - 50f + i *5));
        });
    },

    //shotgun rays
    () -> {
        float aim =  boss.aim();
        loop(20, i -> {
            run(i * 2, () -> shotgun(3, 15f, aim + sin(i, 1f, 5f), boss::shoot));
        });
    },

    //circle of bullets
    () -> {
        float aim =  boss.aim();
        loop(40, i -> {
            run(i * 3, () -> shotgun(2, 180f, aim + i *10f, boss::shoot));
        });
    },

    //spiral of bullets
    () -> {
        float aim =  boss.aim();
        loop(30, i -> {
            run(i * 1f, () -> shotgun(3, 360f/3, aim + i *10f, boss::shoot));
        });
    },

    //shotgun wave
    () -> {
        float aim = boss.aim();
        loop(8, i -> {
            run(i * 3f, () -> shotgun(2 + i, 8f, aim, boss::shootf));
        });
    },

    //line burst
    () -> {
        float aim = boss.aim();
        loop(8, i -> run(i * 3f, () -> circle(3, aim, f -> shotgun(1 + i, 3f + i, f, boss::shootf))));
    },


    //lots of fast lines
    () -> {
        float aim = boss.aim();
        loop(2, j -> run(j * 20, () -> {
            run(Fx.indline.lifetime, () -> loop(8, i -> run(i * 3f, () -> circle(3, aim + j * 60f, boss::shootf))));
            circle(3, aim + j * 60f, f -> Fx.indline.at(boss.x, boss.y, f));
        }));

    },

    //big wave of fast bullets
    () -> {
        float aim = boss.aim();
        Fx.indwave.at(boss.x, boss.y, aim);
        run(Fx.indline.lifetime, () -> {
            loop(8, i -> {
                run(i * 3f, () -> shotgun(2 + i, 8f, aim, boss::shootf));
            });
        });

    }
    );

    private static final Array<Runnable> cycle = Array.with(
        //teleport

        () -> {
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
        () -> {
            PositionConsumer met = (x, y) -> {
                Fx.meteorpre.at(x, y);
                run(Fx.meteorpre.lifetime, () -> boss.shoot(Bullets.meteor, x, y, 0f));
            };

            for(int i = 0; i < 30; i++){
                run(Mathf.random(30f), () -> met.accept(player.x + Mathf.range(400f), player.y + Mathf.range(400f)));
            }

            met.accept(player.x, player.y);
        },


        //candles
        () -> {
            for(Point2 tile : world.candles()){
                float x = tile.x * tilesize, y = tile.y * tilesize + 14f;

                Fx.candlespiral.at(x, y);

                run(Fx.candlespiral.lifetime, () -> {
                    circle(20, f -> boss.shoot(Bullets.candle, x, y, f));
                    Fx.candlefire.at(x, y);
                    renderer.shake(4f);
                });
            }
        },

        //ball
        () -> {
            Bullet b = Bullet.shoot(Bullets.fireball, boss, boss.x, boss.y + boss.height(), boss.aim());
            b.lifetime = b.dst(player) / b.type.speed;
        },

        //dragonfire
        () -> {
            Vector2 s = world.statue();
            loop(20, i -> run(i * 8f, () -> boss.shoot(Bullets.firebreath, s.x, s.y, 270f + Mathf.range(7f))));
        }
    );
    
    private static final Runnable
    
    spiral = () -> {
        loop(10, i -> run(i * 2f, () -> circle(6, i * 4f, f -> boss.shoot(f))));
    };

    public static final Phase

    first = new Phase(){
        Array<Runnable> attacks = Array.with(

        //simple circle of bullets
        () -> {
            float aim = boss.aim();
            loop(6, i -> run(i * 5f, () -> circle(30, aim, f -> boss.shoot(f, t -> v(sin(t, 10f, 1f), 0f)))));
        }/*,

        //dash
        () -> {
            boss.dash(boss.dst(player) / 2f, () -> {
                float aim = boss.aim();
                loop(4, i -> run(i * 3f, () -> shotgun(4 + i/2, 8f, aim, boss::shoot)));
            });
        }*/
        );

        Runnable currentAttack = attacks.random();

        @Override
        public void update(){
            if(time.get(60f * 10f)){
                //cycle.random().run();
            }

            //switch to new attack
            if(time.get(3, 60f * Mathf.random(10f, 20f))){
                Runnable last = currentAttack;
                while(currentAttack == last && attacks.size != 1){
                    currentAttack = attacks.random();
                }
            }

            if(time.get(1, 120f)){
                currentAttack.run();
            }

            if(boss.seesPlayer()){
                //boss.toward(player, 1f);
            }

            if(time.get(2, 140f) && !boss.seesPlayer()){
                Fx.wave.at(boss.x, boss.y);
                float x = player.x, y = player.y;
                Fx.tpwave.at(x, y);
                run(Fx.tpwave.lifetime, () -> {
                    spiral.run();
                    boss.set(x, y);
                    Fx.wave.at(boss.x, boss.y);
                    renderer.shake(4f, 4f);
                });
            }
        }
    };

    private static Vector2 v(float x, float y){
        return Tmp.v1.set(x / 10f, y / 10f);
    }
}
