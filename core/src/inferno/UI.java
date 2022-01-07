package inferno;

import arc.flabel.*;
import inferno.entity.*;
import inferno.graphics.*;
import inferno.ui.*;
import arc.*;
import arc.struct.*;
import arc.graphics.*;
import arc.input.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.util.*;

import static inferno.Inferno.*;
import static inferno.ui.Styles.dim;

public class UI implements ApplicationListener{
    private FLabel label;
    private Image image;

    private Seq<String> text = new Seq<>();
    private String displayName = "";
    private int textIndex;
    private float dialogueTime;

	@Override
	public void init(){
        Core.scene = new Scene();
        Core.input.addProcessor(Core.scene);
        Styles.load();
        image = new Image(dim);
        label = new FLabel("");
        label.setTypingListener(new FListener(){
            @Override
            public void event(String event){
                if(event.startsWith("face:")){
                    image.setDrawable(Core.atlas.drawable(event.substring("face:".length())));
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
            dialogueTime -= Time.delta / 30f;
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

    public void displayText(Seq<String> text){
	    this.text = text;
	    this.textIndex = 0;
	    label.restart(text.first());
        label.act(0.01f);
    }

    void setup(){

	    Core.scene.table(t -> {
	        t.bottom();
	        t.table(dim, c -> {
	            c.visible(() -> label.getText().length() != 0);
	            c.margin(14f).top().left().defaults().top().left();

                c.add(image).size(128f).padRight(8f);
                c.table(text -> {
                    text.left();
                    text.label(() -> displayName).color(Color.coral).padBottom(3).left();
                    text.row();
                    text.add(label).growX().wrap();
                }).growX();

                label.setAlignment(Align.topLeft);
                label.update(() -> {
                    if(Core.input.keyTap(KeyCode.space)){
                        if(label.hasEnded()){
                            if(textIndex < text.size - 1){
                                label.restart(text.get(++textIndex));
                                label.act(0.01f);
                            }else{
                                image.setDrawable(dim);
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
	        t.top().left().table(dim, b -> {
	            //b.setColor(Color.black);
	            b.margin(8f).add(new HealthBar(player)).size(340f, 20f);
            });
        });

        Core.scene.table(t -> {
            t.top().right().table(dim, b -> {
                //b.setColor(Color.black);
                b.margin(8f).add(new HealthBar(boss)).size(340f, 20f);
            });
        });

        Core.scene.table(t -> {
            t.top().right();
            t.label(() -> Core.graphics.getFramesPerSecond() + " FPS");
        });
    }
}
