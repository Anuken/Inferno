package inferno.type.boss;

import inferno.type.Boss;
import inferno.type.Fx;
import io.anuke.arc.collection.Array;
import io.anuke.arc.function.Consumer;

import static inferno.Inferno.player;
import static io.anuke.arc.math.Angles.*;
import static io.anuke.arc.util.Time.run;

public class Phases{
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
        }
    );

    public static final Phase

    first = new Phase(){
        @Override
        public void update(){
            if(time.get(60f)){
                attacks.random().accept(boss);

            //    loop(8, j -> Time.run(j * 5, () -> circle(5, j * 5f, f -> shotgun(10, 5f, f, i -> boss.shoot(Bullets.lbasic, i)))));
            }

            if(time.get(1, 50f) && boss.seesPlayer()){
                boss.dash(boss.dst(player) / 1.5f);
                //loop(8, i -> {
                //    run(i * 2, () -> shotgun(6, 12f, boss.aim() + i * 4, this::shoot));
                //});
            }

            //boss.toward(player, 0.9f);

            if(time.get(1, 60f * 3) && !boss.seesPlayer()){
                Fx.spark.at(boss.x, boss.y);
                run(5f, () -> boss.set(player.x, player.y));
            }
        }
    };
}
