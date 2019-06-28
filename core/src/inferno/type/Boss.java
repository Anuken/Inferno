package inferno.type;

import inferno.entity.SolidEntity;
import io.anuke.arc.Core;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.math.Angles;
import io.anuke.arc.math.geom.Rectangle;

public class Boss extends Char{

    @Override
    public void onDeath(){
        Angles.circle(100, f -> shoot(Bullets.basic, f));
        remove();
    }

    @Override
    public void draw(){
        TextureRegion region = Core.atlas.find("lucine");
        Draw.rect(region, x, y + region.getHeight()/2f);
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
