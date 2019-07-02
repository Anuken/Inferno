package inferno;

import inferno.type.Boss;
import inferno.type.Player;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.Core;
import io.anuke.arc.input.KeyCode;
import io.anuke.arc.util.Time;

import static inferno.Inferno.*;

public class Control implements ApplicationListener{
    private boolean paused = false;

    public Control(){
        //Time.setDeltaProvider(() -> 0.05f);
        Core.keybinds.setDefaults(Binding.values());
        Core.settings.setAppName("Inferno");
        Core.settings.load();
    }

    @Override
    public void init(){
        boss = new Boss();
        player = new Player();
        player.set(world.width() * tilesize/2f + 0.5f, world.height() * tilesize/2f + 0.5f);
        Core.camera.position.set(player);

        boss.set(world.width() * tilesize/2f, world.height() * tilesize/2f + tilesize*25);

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
