package inferno.type.boss;

import inferno.type.Bullets;
import io.anuke.arc.collection.Array;
import io.anuke.arc.util.Interval;

import static inferno.Inferno.boss;

public abstract class Phase{
    public final Interval time = new Interval(10);
    public final Array<String> startText;

    public Phase(Array<String> text){
        this.startText = text;
    }

    protected void shoot(float angle){
        boss.shoot(Bullets.lbasic, angle);
    }

    public abstract void update();
}
