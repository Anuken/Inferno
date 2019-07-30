package inferno.graphics;

import inferno.type.Char;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.Fill;
import io.anuke.arc.math.*;
import io.anuke.arc.scene.Element;
import io.anuke.arc.util.Tmp;

public class HealthBar extends Element{
    private final Char c;
    private final Color color = Pal.player;

    private float fract = 1f;
    private float hit = 0f;

    public HealthBar(Char c){
        this.c = c;
    }

    @Override
    public void draw(){
        fract = Mathf.lerpDelta(fract, c.health / c.maxHealth(), 0.1f);
        if(fract > c.health / c.maxHealth() + 0.04f){
            hit = 1f;
        }

        Draw.colorl(0f);
        Fill.crect(x, y, width, height);
        Draw.color(color, Color.SCARLET, hit * 0.8f);
        Fill.crect(x, y, width * fract, height);

        Draw.color(Tmp.c1.set(color).lerp(Color.SCARLET, hit * 0.8f).shiftHue(0.1f).mul(0.7f));
        Fill.crect(x, y, width * fract, height* 0.3f);

        hit = Mathf.lerpDelta(hit, 0f, 0.3f);
    }
}
