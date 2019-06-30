package inferno.type;

import inferno.entity.SolidEntity;
import inferno.graphics.Layer;
import inferno.type.boss.Phase;
import inferno.type.boss.Phases;
import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.math.geom.Rectangle;

import static io.anuke.arc.math.Angles.circle;

public class Boss extends Char{
    Phase phase = Phases.first;

    @Override
    public void onDeath(){
        circle(100, f -> shoot(Bullets.basic, f));
        remove();
    }

    @Override
    public void update(){
        phase.boss = this;
        phase.update();
    }

    @Override
    public void draw(){
        TextureRegion region = Core.atlas.find("lucine");
        Draw.rect(region, x, y + region.getHeight()/2f);

        Layer.light(x, y + height(), 160f, Color.SCARLET);
    }

    @Override
    public void hitbox(Rectangle rectangle){
        float w = 12f, h = 24f;
        rectangle.set(x - w / 2f, y, w, h);
    }

    @Override
    public boolean collides(SolidEntity other){
        return other instanceof Bullet && ((Bullet) other).shooter instanceof Player;
    }

    @Override
    public void drawShadow(){
        Draw.rect("circle", (int)x, (int)y, 16f, 7f);
    }

}
