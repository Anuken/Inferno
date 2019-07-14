package inferno;

import inferno.type.*;
import io.anuke.arc.*;
import io.anuke.arc.input.*;
import io.anuke.arc.util.*;

import static inferno.Inferno.*;

public class Control implements ApplicationListener{
    private static final float slowmodir = 50f;
    private boolean paused = false;
    private float slowmo;

    public Control(){
        //uncomment for slow motion effect on hit
        //Time.setDeltaProvider(() -> Core.graphics.getDeltaTime() * 60f * Mathf.lerp(1f, 0.6f, Mathf.clamp(slowmo)));
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

    public void reset(){
        bulletGroup.clear();
        effectGroup.clear();

        player.set(world.width() * tilesize/2f + 0.5f, world.height() * tilesize/2f + 0.5f);
        player.heal();
        Core.camera.position.set(player);

        boss.set(world.width() * tilesize/2f, world.height() * tilesize/2f + tilesize*25);
        boss.heal();

        boss.add();
        player.add();
    }

    @Override
    public void update(){

        if(Core.input.keyTap(KeyCode.ESCAPE)){
            Core.app.exit();
        }

        if(Core.input.keyTap(KeyCode.SPACE)){
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
    }

    public boolean isPaused(){
        return paused || ui.hasDialogue();
    }

    public void setPaused(boolean paused){
        this.paused = paused;
    }
}
