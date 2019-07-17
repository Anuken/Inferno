package inferno.type.boss;

import inferno.*;
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
    private static final Interval in = new Interval(20);

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

            for(int i = 0; i < 50; i++){
                run(Mathf.random(60f * 4), () -> met.accept(player.x + Mathf.range(400f), player.y + Mathf.range(400f)));
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
            loop(80, i -> run(i * 8f, () -> boss.shoot(Bullets.firebreath, s.x, s.y, 270f + Mathf.range(7f))));
        }
    );
    
    private static final Runnable
    
    spiral = () -> {
        loop(10, i -> run(i * 2f, () -> circle(6, i * 4f, f -> boss.shoot(f))));
    };

    static int data = 0;

    public static final Array<Phase> phases = Array.with(

    //first phase, basics
    new Phase(Text.start){
        Array<Runnable> attacks = Array.with(

        //oscillating circle of bullets
        () -> {
            every(60f * 2.4f, () -> {
                float aim = boss.aim();
                //sin(t, 10f, 4f)
                loop(3, i -> run(i * 3f, () -> circle(20, aim + i * 6, f -> boss.shoot(f, t -> v(0, sin(t, 11f + i, 1f))))));
                wave();
            });

            boss.toward(player, 0.6f);
        },

        //dash with basic shotgun
        () -> {
            every(60f * 1.5f, () -> {
                boss.anim(Boss.adash, 1.6f * boss.dash(boss.dst(player) / 2f, () -> {
                    float aim = boss.aim();
                    loop(4, i -> run(i * 3f, () -> shotgun(3 + i, 8f, aim, boss::shoot)));
                }));
            });
        },

        //1-2 alternating shotguns with warning
        () -> {
            every(60f * 1.5f, () -> {
                int shots = data++ % 2 + 1;
                float space = 70f;

                float aim = boss.aim();
                run(Fx.indline.lifetime, () -> shotgun(shots, space, aim, f -> seq(3, 3, i -> shotgun(1 + i, 6f - i, f, boss::shootf))));
                shotgun(shots, space, aim, f -> Fx.indline.at(boss.x, boss.y, f));
                run(Fx.indline.lifetime + 10f, () -> boss.anim(Boss.adash, 1.6f * boss.dash(boss.dst(player) / 2f, () -> {
                })));
            });
        },

        //helix pattern thing
        () -> {
            every(60f * 0.65f, () -> {
                float aim = boss.aim();
                seq(10, 3f, i -> {
                    for(int s : signs){
                        boss.shoot(aim, t -> v(0, cos(t, 5f, 6f * s)));
                    }
                });

                wave();
            });
        }
        );

        @Override
        public void update(){
            if(time.get(60f * 18f)){
                cycle.get((special++) % cycle.size).run();
            }

            //switch to new attack
            if(currentAttack == null || time.get(3, 60f * Mathf.random(15f, 40f))){
                Runnable last = currentAttack;
                while(currentAttack == last && attacks.size != 1){
                    currentAttack = attacks.random();
                }
            }

            currentAttack.run();

            teleport();

            if(boss.health / boss.maxHealth() < 0.5f){
                boss.midPhase();
            }
        }
    },
    //phase 1 mid
    new Phase(null){
        Array<Runnable> attacks = Array.with(

        //oscillating circle of bullets
        () -> {
            every(60f * 2f, () -> {
                float aim = boss.aim();
                //sin(t, 10f, 4f)
                loop(5, i -> run(i * 3f, () -> circle(20, aim + i * 6, f -> boss.shoot(f, t -> v(0, sin(t, 11f + i, 1.4f))))));
                wave();
            });
        },

        //dash with basic shotgun
        () -> {
            every(60f * 1.1f, () -> {
                boss.anim(Boss.adash, 1.6f * boss.dash(boss.dst(player) / 2f, () -> {
                    float aim = boss.aim();
                    loop(5, i -> run(i * 3f, () -> shotgun(3 + i, 8f, aim, boss::shoot)));
                }));
            });
        },

        //1-2 alternating shotguns with warning
        () -> {
            every(60f * 1.2f, () -> {
                int shots = data++ % 2 + 1;
                float space = 70f;

                float aim = boss.aim();
                run(Fx.indline.lifetime, () -> shotgun(shots, space, aim, f -> seq(4, 3, i -> shotgun(1 + i, 6f - i, f, boss::shootf))));
                shotgun(shots, space, aim, f -> Fx.indline.at(boss.x, boss.y, f));
                run(Fx.indline.lifetime + 10f, () -> boss.anim(Boss.adash, 1.6f * boss.dash(boss.dst(player) / 2f, () -> {
                })));
            });
        },

        //helix pattern thing
        () -> {
            every(60f * 0.5f, () -> {
                float aim = boss.aim();
                seq(13, 3f, i -> {
                    for(int s : signs){
                        boss.shoot(aim, t -> v(0, cos(t, 5f, 6f * s)));
                    }
                });

                wave();
            });
        }
        );

        @Override
        public void update(){
            if(time.get(60f * 15f)){
                cycle.get((special++) % cycle.size).run();
            }

            //switch to new attack
            if(currentAttack == null || time.get(3, 60f * Mathf.random(15f, 40f))){
                Runnable last = currentAttack;
                while(currentAttack == last && attacks.size != 1){
                    currentAttack = attacks.random();
                }
            }

            currentAttack.run();
            boss.toward(player, boss.dst(player) < 200f ? -0.6f : 0.6f);

            teleport();
        }
    },

    //phase 2
    new Phase(Text.phase1){
        @Override
        public void update(){

        }
    }

    );

    private static void wave(){
        boss.anim(Boss.awave, 20f);
        renderer.shake(4f, 4f);
    }

    private static void seq(int amount, float space, IntConsumer run){
        loop(amount, i -> run(i * space, () -> run.accept(i)));
    }

    private static void every(float time, Runnable run){
        if(in.get(time)){
            run.run();
        }
    }

    private static Vector2 v(float x, float y){
        return Tmp.v1.set(x / 10f, y / 10f);
    }

    public abstract static class Phase{
        public final Interval time = new Interval(10);
        public final Array<String> startText;

        int special = 0;
        Runnable currentAttack = null;

        public Phase(Array<String> text){
            this.startText = text;
        }

        protected void shoot(float angle){
            boss.shoot(Bullets.lbasic, angle);
        }

        public abstract void update();

        void teleport(){
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
    }
}
