package inferno.graphics;

import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.Fill;
import io.anuke.arc.scene.Element;
import io.anuke.arc.util.Tmp;

import static inferno.Inferno.player;

public class HealthBar extends Element{

    @Override
    public void draw(){
        Draw.colorl(0f);
        Fill.crect(x, y, width, height);
        Draw.color(Color.SCARLET);
        Fill.crect(x, y, width * player.health / player.maxHealth(), height);

        Draw.color(Tmp.c1.set(Color.SCARLET).shiftHue(0.1f).mul(0.7f));
        Fill.crect(x, y, width * player.health / player.maxHealth(), height* 0.3f);
    }
}
