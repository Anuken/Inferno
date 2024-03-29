package inferno.graphics;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;

public class Anim{
    private final TextureRegion[] frames;

    public Anim(String... names){
        this.frames = new TextureRegion[names.length];
        for(int i = 0; i < names.length; i++){
            frames[i] = Core.atlas.find(names[i]);
        }
    }

    public TextureRegion frame(float fract){
        return frames[Mathf.clamp((int)(fract * (frames.length)), 0, frames.length - 1)];
    }
}
