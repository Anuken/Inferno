package inferno.type.boss;

import inferno.type.Boss;
import io.anuke.arc.util.Interval;

public abstract class Phase{
    public Interval time = new Interval(10);
    public Boss boss;

    public abstract void update();
}
