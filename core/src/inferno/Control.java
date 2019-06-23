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
        player.set(30, 30);

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

        bulletGroup.collide(charGroup);
    }
}
