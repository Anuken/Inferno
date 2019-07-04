package inferno.type.boss;

import inferno.type.Bullets;
import inferno.type.Fx;

import static inferno.Inferno.player;
import static io.anuke.arc.math.Angles.loop;
import static io.anuke.arc.math.Angles.shotgun;
import static io.anuke.arc.util.Time.run;

public class Phases{
    public static final Phase

    first = new Phase(){
        @Override
        public void update(){
            if(time.get(50f)){
                loop(5,
                    i -> run(i * 4,
                        () -> shotgun(4, 10f, boss.aim() + (i -2.5f) * 9f,
                            f -> boss.shoot(Bullets.lbasic, f))));

            //    loop(8, j -> Time.run(j * 5, () -> circle(5, j * 5f, f -> shotgun(10, 5f, f, i -> boss.shoot(Bullets.lbasic, i)))));
            }

            //boss.toward(player, 0.9f);

            if(time.get(1, 60f * 3) && !boss.seesPlayer()){
                Fx.spark.at(boss.x, boss.y);
                run(5f, () -> boss.set(player.x, player.y));
            }
        }
    };
}
