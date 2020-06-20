package inferno.type;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import inferno.*;
import inferno.entity.*;
import inferno.graphics.*;
import inferno.world.*;

import java.util.*;

import static arc.math.Angles.*;
import static arc.math.Mathf.*;
import static arc.util.Time.run;
import static inferno.Inferno.*;

public class Phases{

    //fun dragonfire attack
    /*
    Vec2 s = world.statue();
    loop(60, i -> run(i * 3f, () -> shotgun(2 + i %10, 4f + (i + 5) % 10, 270f, f -> boss.shoot(Bullets.firebreath, s.x, s.y, f))));
     */
    private static final Interval in = new Interval(20);

    private static final Seq<Runnable> allAttacks = Seq.with(
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

    private static final Seq<Runnable> cycle = Seq.with(
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
            Floatc2 met = (x, y) -> {
                Fx.meteorpre.at(x, y);
                run(Fx.meteorpre.lifetime, () -> boss.shoot(Bullets.meteor, x, y, 0f));
            };

            for(int i = 0; i < 50; i++){
                run(Mathf.random(60f * 4), () -> met.get(player.x + Mathf.range(400f), player.y + Mathf.range(400f)));
            }

            met.get(player.x, player.y);
        },


        //candles
        () -> {
            for(Point2 tile : world.candles){
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
            Vec2 s = world.statue;
            loop(80, i -> run(i * 8f, () -> boss.shoot(Bullets.firebreath, s.x, s.y, 270f + Mathf.range(7f))));
        }
    );
    
    private static final Runnable
    
    spiral = () -> {
        loop(10, i -> run(i * 2f, () -> circle(6, i * 4f, f -> boss.shoot(f))));
    };

    static int data = 0;

    public static final Seq<Phase> phases = Seq.with(

    //first phase, basics
    new Phase(Text.start){
        Seq<Runnable> attacks = Seq.with(

        //oscillating circle of bullets
        () -> {
            every(60f * 2.4f, () -> {
                float aim = boss.aim();
                //sin(t, 10f, 4f)
                loop(3, i -> run(i * 3f, () -> circle(20, aim + i * 6, f -> boss.shoot(f, t -> v(0, sin(t, 11f + i, 1f))))));
                wave();
            });

            boss.toward(player, 0.6f * Time.delta());
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
        Seq<Runnable> attacks = Seq.with(

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

    //phase 2; burn stuff down
    new Phase(Text.phase1){
        boolean detonated = false, detonating = false;

        Seq<Runnable> attacks = Seq.with(
        //double flower attack
        () -> {
            every(60f * 4f - healthi(60), () -> {
                float aim = boss.aim();
                int length = 15;
                loop(length - 1, i -> loop(2, sp -> run(i * 2 + sp * 5, () -> circle(5, f -> shotgun(2, 360f / 16 * i / (float)length, f + aim + sp * 180f, a -> boss.shoot(a, v -> v(0, cos(v, 20f, 2f))))))));

                wave();
            });
        },

        //rays
        () -> {
            every(60f * 3f, () -> {
                float aim = boss.aim();
                loop(7, i -> {
                    run(20f + i * 4, () -> circle(10, f -> boss.shoot(f + 18 + aim)));
                    run(i * 4, () -> circle(10, f -> boss.shoot(f + aim)));
                });

                wave();
            });
        },

        //trispiral of bullets
        () -> {
            every(15f - healthi(5), () -> {
                float aim = 0;
                int d = data++;
                seq(5, 3f, l -> shotgun(3, 360f / 3, aim + d * 20f, f -> boss.shoot(f, v -> v(0, cos(v, 9f, 2f)))));

                wave();
            });
        },

        //indicator shotguns
        () -> {
            every(60f * 0.9f - healthi(20), () -> {
                float space = 25 + (data++ % 3) * 15;
                int shots = (int)(360f / space);

                float aim = boss.aim() + space / 2f;
                shotgun(shots, space, aim, f -> seq(4 + healthi(2), 3, i -> shotgun(1 + i + healthi(2), 8f - i, f, boss::shoot)));

                wave();
            });
        }
        );

        @Override
        public void update(){
            boss.set(world.width() * tilesize / 2f, world.height() * tilesize / 2f);

            if(!detonated && !detonating){
                detonating = true;
                Fx.lspiral.at(boss.x, boss.y + 10f);
                Fx.blastind.at(boss.x, boss.y);
                boss.anim(Boss.awave, Fx.lspiral.lifetime);

                run(Fx.lspiral.lifetime, () -> {
                    control.slowmo(3f);
                    Fx.blast.at(boss.x, boss.y);
                    Fx.blastspark.at(boss.x, boss.y);
                    renderer.shake(30f);
                    run(5f, () -> {
                        world.wallDetonate();
                        detonating = false;
                        detonated = true;
                    });

                    //instakill player if within blast radius
                    if(player.within(boss.x, boss.y, 200)){
                        player.damage(player.health + 1);
                    }
                });
            }

            if(detonated){
                if(currentAttack == null || time.get(3, 60f * Mathf.random(15f, 40f))){
                    Runnable last = currentAttack;
                    while(currentAttack == last && (attacks.size != 1 || currentAttack == null)){
                        currentAttack = attacks.random();
                    }
                }

                currentAttack.run();

                if(time.get(5, 60f * 17f)){
                    for(Point2 candle : world.candles){
                        if(Mathf.chance(0.4)) continue;

                        run(Mathf.range(60f * 5), () -> {
                            float cx = candle.x * tilesize, cy = candle.y * tilesize + 14f;
                            Fx.candlespiral.at(cx, cy);
                            run(Fx.candlespiral.lifetime, () -> {
                                float angle = Angles.angle(cx, cy, player.x, player.y);
                                seq(6, 4, i -> {
                                    boss.shoot(Bullets.candle, cx, cy, angle, v -> v(0, cos(v, 9f, 1.5f)));
                                    Fx.spark.at(cx, cy, Pal.candle);
                                });
                            });
                        });
                    }
                }
            }
        }

        @Override
        public void reset(){
            super.reset();
            detonated = false;
            detonating = false;
        }
    },

    new Phase(Text.phase2){
        float windup;
        Seq<Runnable> attacks = Seq.with(
        () -> {
            every(30f, () -> {
                float f = (data++ % 2 - 0.5f) * 50f;
                float aim = boss.aim() + f;
                int sign = -Mathf.sign(f);
                seq(6, 3f, i -> shotgun(4, 3f, aim + i * sign * 5, boss::shoot));
            });
        },

        () -> {
            every(60f * 3f, () -> {
                seq(10, 20f, i -> {
                    float ang = Mathf.random(360f);
                    float dst = 100f;
                    float x = player.x, y = player.y + 10f;
                    loop(6, j -> {
                        run(j, () -> {
                            Tmp.v1.trns(ang, dst + j * 8f);
                            boss.shoot(Bullets.lbasicslow, x + Tmp.v1.x, y + Tmp.v1.y, ang + 180f, v -> v(0, 0));
                        });
                    });
                    Tmp.v1.trns(ang, dst + 7 * 8f);
                    Fx.ghost.at(x + Tmp.v1.x, y + Tmp.v1.y, Direction.fromAngle(180f + player.angleTo(x + Tmp.v1.x, y + Tmp.v1.y)));
                });
            });
        },

        () -> {
            every(60f * 2, () -> {
                float aim = boss.aim() + 45f;
                int s = Mathf.sign(data++ % 2 - 0.5f);
                seq(20, 4f, i -> shotgun(2, 180f, (i * 8f + aim) * s, f -> shotgun(6, 10f, f, boss::shoot)));
            });
        }
        );

        @Override
        public void update(){
            if(time.get(60f * 15f)){
                cycle.get((special++) % cycle.size).run();
            }

            if(currentAttack == null || time.get(3, 60f * Mathf.random(10f, 30f))){
                currentAttack = attacks.random(currentAttack);
            }

            currentAttack.run();
            windup += Time.delta();

            if(windup >= 50f * 60f + healthi(70) & time.get(2, 40f)){
                //circles

                float x = player.x;
                float y = player.y + 10f;
                float ang = Mathf.random(30f);
                circleVectors(17, 200f, ang, (cx, cy) -> boss.shoot(Bullets.lbasicslow, x + cx, y + cy, Mathf.angle(cx, cy) + 180f));

                //squares.
                /*
                seq(2,  60f, i -> {
                    float x = player.x;
                    float y = player.y + 10f;
                    float ang = Mathf.random(30f);
                    int am = 16;
                    float dst = 120f;
                    float space = 20f;
                    if(data++%2 == 0){
                        loop(am, j -> {
                            for(int s : signs){
                                float cx = x + dst * s;
                                float cy = y - am/2f*space + j * space;
                                boss.shoot(Bullets.lbasicslow, cx, cy, angle(cx, cy, x, y));
                            }
                        });
                    }else{
                        loop(am, j -> {
                            for(int s : signs){
                                float cx = x - am/2f*space + j * space;
                                float cy = y + dst * s;
                                boss.shoot(Bullets.lbasicslow, cx, cy, angle(cx, cy, x, y));
                            }
                        });
                    }
                });*/
            }

            teleport();
        }

        @Override
        public void begin(){
            world.wallExtinguish();
        }

        @Override
        public void reset(){
            super.reset();
            windup = 0f;

        }
    },

    new Phase(Text.phase3){
        Seq<Runnable> attacks = Seq.with(
        () -> {
            every(60f*0.6f, () -> {
                Vec2 s = world.statue;
                loop(7, i -> run(i * 3f, () -> shotgun(2 + i %10, 4f + (i + 5) % 10, 0f, f -> boss.shoot(Bullets.breath2, s.x, s.y, f + boss.aim()))));
            });
        },
        () -> {
            every(60f*0.9f, () -> {
                Vec2 s = world.statue;
                loop(9, i -> shotgun(2, 80f - i*6f, 0f, c -> run(i * 3f, () -> shotgun(2, 4f + (i + 5) % 10, c, f -> boss.shoot(Bullets.breath2, s.x, s.y, f + boss.aim())))));
                run(30f, () -> {
                    boss.laser(Bullets.laser, boss.aim() + range(30));
                });
            });
        },

        () -> {
            every(60f*2f, () -> {
                float aim = boss.aim();
                Fx.indwave.at(boss.x, boss.y, aim);
                run(Fx.indline.lifetime, () -> {
                    loop(8, i -> {
                        run(i * 3f, () -> shotgun(2 + i, 8f, aim, f -> boss.shoot(Bullets.breathfast, f)));
                    });
                });
            });

        }
        );

        @Override
        public void update(){

            if(time.get(60f * 15f)){
                int current = (special++) % cycle.size;
                if(current == 0){
                    TextureRegion r = Core.atlas.find("statue-eyes");
                    float x = 40.5f * tilesize, y = (world.height() - 10.5f) * tilesize + r.getHeight()/2f;
                    //blast thing
                    Fx.lspiral.at(x, y);
                    Fx.blastind.at(x, y);
                    Fx.eyes.at(x, y);

                    run(Fx.lspiral.lifetime, () -> {
                        control.slowmo(3f);
                        Fx.blast.at(x, y);
                        Fx.blastspark.at(x, y);
                        renderer.shake(30f);

                        //instakill player if within blast radius
                        if(player.within(x, y, 200)){
                            player.damage(player.health + 1);
                        }
                    });
                }else{
                    cycle.get(current).run();
                }
            }

            boss.set(world.statue.x, world.statue.y);

            if(time.get(6, 60f * 9f)){
                run(60f * 5, () -> {
                    seq(5, 26f, i -> {
                        float range = 45f;
                        float aim = Mathf.clamp(boss.angleTo(player), 270f - range, 270f + range);
                        boss.laser(Bullets.laser, aim + Mathf.random(20f));
                    });
                });
            }

            if(currentAttack == null || time.get(3, 60f * Mathf.random(10f, 30f))){
                currentAttack = attacks.random(currentAttack);
            }

            currentAttack.run();
        }
    },

    new Phase(Text.phase4){
        {
            ambient = new Color(0.1f, 0.01f, 0.02f, 0.7f);
        }

        @Override
        public void update(){

            //spam randomized lasers
            /*
            every(5f, () -> {
                Tmp.v1.setToRandomDirection().scl(500f);
                boss.laser(Bullets.laser, player.x + Tmp.v1.x, player.y + Tmp.v1.y, Tmp.v1.angle() + 180f);

            });*/


            //laser wall
            /*
            every(100f, () -> {
                float x = player.x, y = player.y;
                int ss = Mathf.sign(special++%2 - 0.5f);
                float space = 20f;
                seq(20, 4f, i -> {
                    run(50f, () -> {
                        int sign = Mathf.sign((i % 2) - 0.5f);
                        boss.laser(Bullets.laser, x + 80f * sign, y + space*5*ss - i * space*ss, 90f + 90*sign);
                    });
                });
            });

             */
        }

        @Override
        public void begin(){
            Block fire = Blocks.blocks.find(b -> b.name.equals("fire"));

            world.each(tile -> {

                if(tile.overlay != null && tile.overlay.name.contains("carpet") && tile.wall == null){
                    tile.wall = fire;
                }
            });
        }

        @Override
        public void reset(){
            world.each(tile -> {
                if(tile.wall != null && tile.wall.name.equals("fire")){
                    tile.wall = null;
                }
            });
        }
    }

    );

    private static void wave(){
        boss.anim(Boss.awave, 20f);
        renderer.shake(4f, 4f);
    }

    private static void seq(int amount, float space, Intc run){
        loop(amount, i -> run(i * space, () -> run.get(i)));
    }

    private static void every(float time, Runnable run){
        if(in.get(time)){
            run.run();
        }
    }

    private static int healthi(int max){
        return (int)((1f - boss.health / boss.maxHealth()) * max);
    }

    private static Vec2 v(float x, float y){
        return Tmp.v1.set(x / 10f, y / 10f);
    }

    public abstract static class Phase{
        static int special = 0;

        public final Interval time = new Interval(10);
        public final Seq<String> startText;

        public Runnable currentAttack = null;
        public Color ambient = new Color(0.2f, 0.06f, 0.02f, 0.5f);

        public Phase(Seq<String> text){
            this.startText = text;
        }

        public void begin(){

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

        public void reset(){
            special = 0;
            currentAttack = null;
            time.clear();
            Arrays.fill(time.getTimes(), Time.time());
        }
    }
}
