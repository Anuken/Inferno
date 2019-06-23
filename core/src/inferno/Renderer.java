package inferno;

import inferno.entity.Entity;
import inferno.graphics.Layer;
import inferno.graphics.LayerBatch;
import inferno.world.Tile;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.Core;
import io.anuke.arc.function.IntPositionConsumer;
import io.anuke.arc.graphics.*;
import io.anuke.arc.graphics.Texture.TextureFilter;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.graphics.glutils.FrameBuffer;
import io.anuke.arc.util.Structs;

import static inferno.Inferno.*;

public class Renderer implements ApplicationListener{
    public FrameBuffer buffer = new FrameBuffer(2, 2);
    public LayerBatch lbatch;

    public Renderer(){
        Core.atlas = new TextureAtlas(Core.files.internal("sprites/sprites.atlas"));
        Core.batch = lbatch = new LayerBatch();
        Core.camera = new Camera();

        buffer.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }

    @Override
    public void update(){
        Layer.sort(true);

        Core.camera.position.set((int)(player.x + 0.001f), (int)(player.y + 0.001f));
        Core.camera.update();

        Draw.proj(Core.camera.projection());

        Draw.flush();
        buffer.begin();
        Core.graphics.clear(Color.BLACK);

        drawWorld();

        charGroup.draw(this::draw);
        bulletGroup.draw(this::draw);

        Draw.flush();
        buffer.end();

        Draw.color();
        Draw.blend(Blending.disabled);
        Draw.rect(Draw.wrap(buffer.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
        Draw.blend();
    }

    @Override
    public void resize(int width, int height){
        buffer.resize(width / scale, height / scale);
        Core.camera.resize(width / scale, height / scale);
    }

    void drawWorld(){
        Draw.color();

        //do not sort base layer for efficiency
        Layer.sort(false);
        cull((x, y) -> {
            Tile tile = world.tile(x, y);
            if(!world.solid(x, y) && tile.floor != null){
                Draw.rect(tile.floor, x * tilesize, y * tilesize);
            }
        });
        Layer.sort(true);

        cull((x, y) -> {
            Layer.z(y * tilesize - tilesize / 2f);
            Tile tile = world.tile(x, y);
            if(tile.wall != null){
                Draw.rect(tile.wall, x * tilesize, y * tilesize - tilesize / 2f + tile.wall.getHeight() / 2f);
            }
        });
    }

    void cull(IntPositionConsumer cons){
        int xrange = (int)(Core.camera.width / tilesize / 2 + 2);
        int yrange = (int)(Core.camera.width / tilesize / 2 + 2);
        int wx = (int)(player.x / tilesize);
        int wy = (int)(player.y / tilesize);

        for(int x = -xrange; x < xrange; x++){
            for(int y = -yrange; y < yrange; y++){
                int cx = wx + x, cy = wy + y;
                if(Structs.inBounds(cx, cy, world.width(), world.height())){
                    cons.accept(cx, cy);
                }
            }
        }
    }

    private void draw(Entity entity){
        Layer.z(entity.y);
        entity.draw();
    }
}
