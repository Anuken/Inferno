package inferno.graphics;

import inferno.entity.ScaleTrait;
import inferno.type.EffectEntity;
import io.anuke.arc.func.*;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.graphics.g2d.Draw;
import io.anuke.arc.math.geom.Position;
import io.anuke.arc.util.pooling.Pools;

public class Effects{
    private static final EffectContainer container = new EffectContainer();
    private static float shakeFalloff = 1000f;

    public static void renderEffect(int id, Effect render, Color color, float life, float rotation, float x, float y, Object data){
        Drawf.z(y - 10f);
        container.set(id, color, life, render.lifetime, rotation, x, y, data);
        render.draw.get(container);
        Draw.reset();
    }

    public static void effect(Effect effect, float x, float y, float rotation){
        effect(effect, Color.white, x, y, rotation, null);
    }

    public static void effect(Effect effect, float x, float y){
        effect(effect, x, y, 0);
    }

    public static void effect(Effect effect, Color color, float x, float y){
       effect(effect, color, x, y, 0f, null);
    }

    public static void effect(Effect effect, Position loc){
       effect(effect, Color.white, loc.getX(), loc.getY(), 0f, null);
    }

    public static void effect(Effect effect, Color color, float x, float y, float rotation){
       effect(effect, color, x, y, rotation, null);
    }

    public static void effect(Effect effect, Color color, float x, float y, float rotation, Object data){
        EffectEntity entity = Pools.obtain(EffectEntity.class, EffectEntity::new);
        entity.effect = effect;
        entity.color.set(color);
        entity.set(x, y);
        entity.rotation = rotation;
        entity.data = data;
        entity.add();
    }

    public static void effect(Effect effect, float x, float y, float rotation, Object data){
        effect(effect, Color.white, x, y, rotation, data);
    }

    public static class Effect{
        private static int lastid = 0;
        public final int id;
        public final Cons<EffectContainer> draw;
        public final float lifetime;
        /** Clip size. */
        public float size;

        public Effect(float life, float clipsize, Cons<EffectContainer> draw){
            this.id = lastid++;
            this.lifetime = life;
            this.draw = draw;
            this.size = clipsize;
        }

        public Effect(float life, Cons<EffectContainer> draw){
            this(life, 28f, draw);
        }

        public void at(float x, float y){
            effect(this, x, y);
        }

        public void at(float x, float y, Color color){
            effect(this, color, x, y, 0f);
        }

        public void at(float x, float y, float rotation){
            effect(this, x, y, rotation);
        }

        public void at(float x, float y, Object data){
            effect(this, x, y, 0, data);
        }

        public void at(float x, float y, float rotation, Color color){
            effect(this, color, x, y, rotation);
        }
    }

    public static class EffectContainer implements ScaleTrait{
        public float x, y, time, lifetime, rotation;
        public Color color;
        public int id;
        public Object data;
        private EffectContainer innerContainer;

        public void set(int id, Color color, float life, float lifetime, float rotation, float x, float y, Object data){
            this.x = x;
            this.y = y;
            this.color = color;
            this.time = life;
            this.lifetime = lifetime;
            this.id = id;
            this.rotation = rotation;
            this.data = data;
        }

        public void scaled(float lifetime, Cons<EffectContainer> cons){
            if(innerContainer == null) innerContainer = new EffectContainer();
            if(time <= lifetime){
                innerContainer.set(id, color, time, lifetime, rotation, x, y, data);
                cons.get(innerContainer);
            }
        }

        @Override
        public float fin(){
            return time / lifetime;
        }
    }
}
