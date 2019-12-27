package inferno.ui;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.Label.*;

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
