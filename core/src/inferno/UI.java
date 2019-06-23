package inferno;

import inferno.graphics.Layer;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.Core;
import io.anuke.arc.scene.Scene;
import io.anuke.arc.scene.Skin;
import io.anuke.arc.util.Time;

public class UI implements ApplicationListener{
	
	@Override
	public void init(){
        Core.scene = new Scene(new Skin(Core.files.internal("sprites/uiskin.json"), Core.atlas));
        Core.input.addProcessor(Core.scene);
        setup();
	}

	@Override
    public void resize(int width, int height){
        Core.scene.resize(width, height);
    }

    @Override
    public void update(){
        Layer.sort(false);
        Time.update();

        Core.scene.act();
        Core.scene.draw();
    }

    void setup(){
	    Core.scene.table(t -> {
	        t.top().left();
	        t.label(() -> Core.graphics.getFramesPerSecond() + " FPS");
        });
    }
}
