package inferno.type.boss;

import inferno.type.Bullets;
import io.anuke.arc.util.Time;

import static io.anuke.arc.math.Angles.*;

public class Phases{
    public static final Phase

    first = new Phase(){
        @Override
        public void update(){
            if(time.get(70f)){
                loop(8, j -> Time.run(j * 5, () -> circle(5, j * 5f, f -> shotgun(10, 5f, f, i -> boss.shoot(Bullets.lbasic, i)))));
            }
        }
    };
}
