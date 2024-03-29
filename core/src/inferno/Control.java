package inferno;

import inferno.type.*;
import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.input.*;
import arc.math.*;
import arc.util.*;

import static inferno.Inferno.*;

public class Control implements ApplicationListener{
    private static final float slowmodir = 50f;
    private boolean paused = false;
    private float slowmo;

    public Control(){
        Time.setDeltaProvider(() -> (slowmo <= 0f ? 1f : Mathf.lerp(1f, 0.6f, Mathf.clamp(slowmo))) * Math.min(Core.graphics.getDeltaTime() * 60f, 1f));
        Core.keybinds.setDefaults(Binding.values());
        Core.settings.setAppName("Inferno");
        Core.settings.load();
    }

    @Override
    public void init(){
        boss = new Boss();
        player = new Player();
        reset();
    }

    public void slowmo(){
        slowmo = 1f;
    }

    public void slowmo(float amount){
        slowmo = amount;
    }

    public void reset(){
        Time.clear();
        bulletGroup.clear();
        effectGroup.clear();

        player.set(world.width() * tilesize/2f + 0.5f, world.height() * tilesize/2f + 0.5f);
        player.heal();
        Core.camera.position.set(player);

        boss.set(world.width() * tilesize/2f, world.height() * tilesize/2f + tilesize*25);
        boss.heal();
        boss.reset();

        boss.add();
        player.add();
    }

    @Override
    public void update(){
        if(prof) Time.mark();
        Color.white.set(1f, 1f, 1f, 1f);
        Draw.mixcol();

        if(Core.input.keyTap(KeyCode.escape)){
            Core.app.exit();
        }

        if(Core.input.keyTap(KeyCode.space)){
            setPaused(!isPaused());
        }

        if(!isPaused()){
            Time.update();

            slowmo -= Core.graphics.getDeltaTime() * 60f / slowmodir;

            charGroup.update();
            bulletGroup.update();
            effectGroup.update();

            charGroup.collide(bulletGroup);
        }
        if(prof) Log.info("Update: " + Time.elapsed());
    }

    public boolean isPaused(){
        return paused || ui.hasDialogue();
    }

    public void setPaused(boolean paused){
        this.paused = paused;
    }
}
