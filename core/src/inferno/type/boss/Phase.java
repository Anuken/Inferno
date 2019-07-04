package inferno.type.boss;

import inferno.type.Boss;
import inferno.type.Bullets;
import io.anuke.arc.util.Interval;

public abstract class Phase{
    public Interval time = new Interval(10);
    public Boss boss;

    protected void shoot(float angle){
        boss.shoot(Bullets.lbasic, angle);
    }

    public abstract void update();
}
