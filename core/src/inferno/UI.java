package inferno;

import inferno.entity.Entity;
import inferno.graphics.HealthBar;
import inferno.graphics.Drawf;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.Core;
import io.anuke.arc.collection.Array;
import io.anuke.arc.graphics.Color;
import io.anuke.arc.input.KeyCode;
import io.anuke.arc.scene.Scene;
import io.anuke.arc.scene.Skin;
import io.anuke.arc.scene.ui.Image;
import io.anuke.arc.typelabel.TypeLabel;
import io.anuke.arc.typelabel.TypingListener;
import io.anuke.arc.util.*;

import static inferno.Inferno.boss;
import static inferno.Inferno.player;

public class UI implements ApplicationListener{
    private TypeLabel label;
    private Image image;

    private Array<String> text = new Array<>();
    private String displayName = "";
    private int textIndex;
    private float dialogueTime;
	
	@Override
	public void init(){
        Core.scene = new Scene(new Skin(Core.files.internal("sprites/uiskin.json"), Core.atlas));
        Core.input.addProcessor(Core.scene);
        image = new Image("dialogDim");
        label = new TypeLabel("");
        label.setTypingListener(new TypingListener(){
            @Override
            public void event(String event){
                if(event.startsWith("face:")){
                    image.setDrawable(event.substring("face:".length()));
                }else{
                    displayName = event;
                }
            }
        });

        setup();
	}

	@Override
    public void resize(int width, int height){
        Core.scene.resize(width, height);
    }

    @Override
    public void update(){
        Drawf.sort(false);

        if(label.getText().length() > 0){
            dialogueTime = 1f;
        }else{
            dialogueTime -= Time.delta() / 30f;
        }

        Core.scene.act();
        Core.scene.draw();
    }

    public Entity getDialogueFace(){
	    return image.getRegion().asAtlas().name.contains("lucine") && label.getText().length() > 0 ? boss : image.getRegion().asAtlas().name.contains("asmus") || label.getText().length() == 0 ? player : null;
    }

    public boolean hasDialogue(){
	    return label.getText().length() > 0 || dialogueTime > 0f;
    }

    public void displayText(Array<String> text){
	    this.text = text;
	    this.textIndex = 0;
	    label.restart(text.first());
        label.act(0.01f);
    }

    void setup(){

	    Core.scene.table(t -> {
	        t.bottom();
	        t.table("dialogDim", c -> {
	            c.visible(() -> label.getText().length() != 0);
	            c.margin(14f).top().left().defaults().top().left();

                c.add(image).size(128f).padRight(8f);
                c.table(text -> {
                    text.left();
                    text.label(() -> displayName).color(Color.CORAL).padBottom(3).left();
                    text.row();
                    text.add(label).growX().wrap();
                }).growX();

                label.setAlignment(Align.topLeft);
                label.update(() -> {
                    if(Core.input.keyTap(KeyCode.SPACE)){
                        if(label.hasEnded()){
                            if(textIndex < text.size - 1){
                                label.restart(text.get(++textIndex));
                                label.act(0.01f);
                            }else{
                                image.setDrawable("dialogDim");
                                label.setText("");
                            }
                        }else{
                            label.skipToTheEnd(false);
                        }
                    }
                });
            }).width(600f);
        });

	    Core.scene.table(t -> {
	        t.top().left().table("dialogDim", b -> {
	            //b.setColor(Color.BLACK);
	            b.margin(8f).add(new HealthBar(player)).size(340f, 20f);
            });
        });

        Core.scene.table(t -> {
            t.top().right().table("dialogDim", b -> {
                //b.setColor(Color.BLACK);
                b.margin(8f).add(new HealthBar(boss)).size(340f, 20f);
            });
        });

        Core.scene.table(t -> {
            t.top().right();
            t.label(() -> Core.graphics.getFramesPerSecond() + " FPS");
        });
    }
}
