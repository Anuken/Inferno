package inferno;

import inferno.type.Boss;
import inferno.type.Player;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.Core;
import io.anuke.arc.audio.Music;
import io.anuke.arc.input.KeyCode;
import io.anuke.arc.math.Interpolation;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.util.Time;

import static inferno.Inferno.*;

public class Control implements ApplicationListener{
    private boolean paused = false;
    private Music music = Core.audio.newMusic(Core.files.internal("music/out.ogg"));

    public Control(){
        Time.setDeltaProvider(() -> Mathf.clamp(music.getPosition() < 34f ? Interpolation.pow2In.apply(music.getPosition() / 34f) * 2f : 2f + (music.getPosition()-34f) / 25f, 0f, 5f));
        Core.keybinds.setDefaults(Binding.values());
        Core.settings.setAppName("Inferno");
        Core.settings.load();
        music.play();
    }

    @Override
    public void init(){

        boss = new Boss();
        player = new Player();
        reset();

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
