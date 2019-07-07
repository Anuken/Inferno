package inferno.graphics;

import inferno.type.Char;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.Fill;
import io.anuke.arc.scene.Element;
import io.anuke.arc.util.Tmp;

public class HealthBar extends Element{
    private final Char c;

    public HealthBar(Char c){
        this.c = c;
    }

    @Override
    public void draw(){
        Draw.colorl(0f);
        Fill.crect(x, y, width, height);
        Draw.color(Color.SCARLET);
        Fill.crect(x, y, width * c.health / c.maxHealth(), height);

        Draw.color(Tmp.c1.set(Color.SCARLET).shiftHue(0.1f).mul(0.7f));
        Fill.crect(x, y, width * c.health / c.maxHealth(), height* 0.3f);
    }
}
