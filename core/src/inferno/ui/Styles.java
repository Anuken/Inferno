package inferno.ui;

import io.anuke.arc.*;
import io.anuke.arc.graphics.*;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.scene.style.*;
import io.anuke.arc.scene.ui.Label.*;

public class Styles{
    public static BitmapFont font = new BitmapFont(Core.files.internal("sprites/default.fnt"));
    public static Drawable dim = ((TextureRegionDrawable)Core.atlas.drawable("white")).tint(0f, 0f, 0f, 0.45f);

    public static void load(){

        Core.scene.addStyle(LabelStyle.class, new LabelStyle(){{
            font = Styles.font;
            fontColor = Color.white;
        }});
    }
}
