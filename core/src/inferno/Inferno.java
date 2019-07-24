package inferno;

import inferno.entity.EntityGroup;
import inferno.type.*;
import io.anuke.arc.ApplicationCore;

public class Inferno extends ApplicationCore {
	public static final int scale = 4;
	public static final int tilesize = 16;
	public static final boolean dobloom = true, debug = false;

	public static EntityGroup<Bullet> bulletGroup;
	public static EntityGroup<Char> charGroup;
	public static EntityGroup<EffectEntity> effectGroup;

	public static Player player;
	public static Boss boss;

    public static UI ui;
    public static Renderer renderer;
    public static Control control;
    public static World world;
	
	@Override
	public void setup(){
		bulletGroup = new EntityGroup<>(true);
		charGroup = new EntityGroup<>(true);
		effectGroup = new EntityGroup<>(false);

		add(control = new Control());
		add(renderer = new Renderer());
		add(world = new World());
		add(ui = new UI());
	}
	
}
