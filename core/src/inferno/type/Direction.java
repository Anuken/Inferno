package inferno.type;

import arc.Core;
import arc.graphics.g2d.TextureRegion;

public enum Direction{
    left("side", true, 2, 1, 0, 1),
    up("back", false, 0, 1, 0, 2),
    right("side", false, 2, 1, 0, 1),
    down("front", false, 0, 1, 0, 2);

    final boolean flipped;
    final String name;
    final TextureRegion[] frames;
    final TextureRegion region;

    Direction(String name, boolean flipped, int... seq){
        this.flipped = flipped;
        this.name = name;
        this.region = Core.atlas.find("prince-" + name);
        this.frames = new TextureRegion[seq.length];
        for(int i = 0; i < seq.length; i++){
            frames[i] = Core.atlas.find("prince-" + name + "-" + seq[i]);
        }
    }

    static Direction fromAngle(float angle){
        if(angle < 45 || angle >= 315){
            return right;
        }else if(angle >= 45 && angle < 135){
            return up;
        }else if(angle >= 135 && angle < 225){
            return left;
        }else{
            return down;
        }
    }
}
