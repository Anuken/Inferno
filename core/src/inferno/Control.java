package inferno;

import inferno.type.Boss;
import inferno.type.Player;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.Core;
import io.anuke.arc.input.KeyCode;

import static inferno.Inferno.*;

public class Control implements ApplicationListener{

    public Control(){
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

        boss.add();
        player.add();
    }

    @Override
    public void update(){
        if(Core.input.keyTap(KeyCode.ESCAPE)){
            Core.app.exit();
        }

        charGroup.update();
        bulletGroup.update();
        effectGroup.update();

        bulletGroup.collide(charGroup);
    }
}
