package inferno.type;

import inferno.entity.*;
import inferno.graphics.Effects;
import inferno.graphics.Effects.Effect;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.util.Time;
import io.anuke.arc.util.pooling.Pool.Poolable;
import io.anuke.arc.util.pooling.Pools;

import static inferno.Inferno.effectGroup;

public class EffectEntity extends Entity implements Poolable, ScaleTrait{
    public Effect effect;
    public Color color = new Color(Color.WHITE);
    public Object data;
    public float rotation = 0f;
    public float time;

    public Entity parent;
    public float poffsetx, poffsety;

    /** For pooling use only! */
    public EffectEntity(){
    }

    public void setParent(Entity parent){
        this.parent = parent;
        this.poffsetx = x - parent.getX();
        this.poffsety = y - parent.getY();
    }

    @Override
    public EntityGroup targetGroup(){
        //this should never actually be called
        return effectGroup;
    }

    @Override
    public float fin(){
        return time / lifetime();
    }

    public float lifetime(){
        return effect.lifetime;
    }

    @Override
    public float drawSize(){
        return effect.size;
    }

    @Override
    public void update(){
        if(effect == null){
            remove();
            return;
        }

        time = Mathf.clamp(time + Time.delta(), 0, lifetime());

        if(time >= lifetime()){
            remove();
        }

        if(parent != null){
            x = parent.getX() + poffsetx;
            y = parent.getY() + poffsety;
        }
    }

    @Override
    public void reset(){
        effect = null;
        color.set(Color.WHITE);
        rotation = time = poffsetx = poffsety = 0f;
        parent = null;
        data = null;
        id = lastID++;
    }

    @Override
    public void draw(){
        Effects.renderEffect(id, effect, color, time, rotation, x, y, data);
    }

    @Override
    public void removed(){
        Pools.free(this);
    }
}
