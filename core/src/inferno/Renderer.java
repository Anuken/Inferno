package inferno;

import inferno.entity.Entity;
import inferno.graphics.*;
import inferno.world.Tile;
import io.anuke.arc.ApplicationListener;
import io.anuke.arc.Core;
import io.anuke.arc.function.IntPositionConsumer;
import io.anuke.arc.graphics.*;
import io.anuke.arc.graphics.Texture.TextureFilter;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.graphics.glutils.FrameBuffer;
import io.anuke.arc.graphics.glutils.Shader;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.arc.math.geom.Point2;
import io.anuke.arc.util.ScreenRecorder;
import io.anuke.arc.util.Structs;

import static inferno.Inferno.*;

public class Renderer implements ApplicationListener{
    public LayerBatch zbatch;
    public QueueBatch lbatch;

    private FrameBuffer buffer = new FrameBuffer(2, 2);
    private FrameBuffer shadow = new FrameBuffer(2, 2);
    private FrameBuffer lights = new FrameBuffer(2, 2);
    private FrameBuffer fogs;
    private float lim = 10f;

    private Shader fog = new Shader(Core.files.local("dshaders/default.vertex.glsl"), Core.files.local("dshaders/fog.fragment.glsl"));
    private Shader light = new Shader(Core.files.local("dshaders/default.vertex.glsl"), Core.files.local("dshaders/light.fragment.glsl"));
    private SpriteCache cache;

    private Bloom bloom;

    public Renderer(){
        Core.atlas = new TextureAtlas(Core.files.internal("sprites/sprites.atlas"));
        Core.batch = zbatch = new LayerBatch();
        Core.camera = new Camera();

        lbatch = new QueueBatch();

        buffer.getTexture().setFilter(TextureFilter.Nearest);
        bloom = new Bloom();
    }

    @Override
    public void init(){
        makeShadow();
        makeFloor();
    }

    @Override
    public void update(){
        Layer.sort(true);

        Core.camera.position.lerpDelta(player.x, player.y, 0.03f).clamp(player.x - lim, player.x + lim, player.y - lim, player.y + lim);
        float px = Core.camera.position.x, py = Core.camera.position.y;
        Core.camera.position.snap();
        Core.camera.update();

        Core.batch = lbatch;
        Draw.proj(Core.camera.projection());
        Core.batch = zbatch;
        Draw.proj(Core.camera.projection());

        shadow.beginDraw(Color.CLEAR);
        drawShadows();
        shadow.endDraw();

        buffer.beginDraw(Color.BLACK);

        drawWorld();

        charGroup.draw(this::draw);
        bulletGroup.draw(this::draw);
        effectGroup.draw(this::draw);

        Layer.sort(false);

        Draw.shader(fog);
        Draw.fbo(fogs.getTexture(), world.width(), world.height(), tilesize);
        Draw.shader();

        buffer.endDraw();

        Draw.color();
        lights.beginDraw(Color.CLEAR);
        lbatch.flush();
        lights.endDraw();

        buffer.begin();

        Draw.color();
        Draw.shader(light);
        Draw.rect(Draw.wrap(lights.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
        Draw.shader();

        buffer.endDraw();

        if(dobloom) bloom.capture();
        Draw.color();
        Draw.blend(Blending.disabled);
        Draw.rect(Draw.wrap(buffer.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
        Draw.blend();
        if(dobloom) bloom.render();

        Core.camera.position.set(px, py);

        ScreenRecorder.record();
    }

    @Override
    public void resize(int width, int height){
        buffer.resize(width / scale, height / scale);
        shadow.resize(width / scale, height / scale);
        lights.resize(width / scale, height / scale);
        bloom.dispose();
        bloom = new Bloom();
        Core.camera.resize(width / scale, height / scale);
    }

    void drawShadows(){
        cull((x, y) -> {
            Tile tile = world.tile(x, y);
            if(tile.shadowed){
                tile.wall.drawShadow(x, y);
            }
        });

        charGroup.draw(Entity::drawShadow);
    }

    void drawWorld(){
        Draw.color();

        //draw cached floor
        Draw.flush();
        cache.setProjectionMatrix(Core.camera.projection());
        cache.begin();
        cache.draw(0);
        cache.end();

        //do not sort base layer for efficiency
        Layer.sort(false);

        //overlays
        cull((x, y) -> {
            Tile tile = world.tile(x, y);
            if(!world.solid(x, y) && tile.overlay != null){
                tile.overlay.draw(x, y);
            }
        });

        Draw.color(0f, 0f, 0f, 0.4f);
        Draw.rect(Draw.wrap(shadow.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
        Draw.color();

        Layer.sort(true);

        cull((x, y) -> {
            Layer.z(y * tilesize - tilesize / 2f);
            Tile tile = world.tile(x, y);

            if(tile.wall != null){
                tile.wall.draw(x, y);
            }
        });
    }

    void cull(IntPositionConsumer cons){
        int xrange = (int)(Core.camera.width / tilesize / 2 + 3);
        int yrange = (int)(Core.camera.height / tilesize / 2 + 3);
        int wx = (int)(Core.camera.position.x / tilesize);
        int wy = (int)(Core.camera.position.y / tilesize);

        for(int x = -xrange; x < xrange; x++){
            for(int y = -yrange; y < yrange; y++){
                int cx = wx + x, cy = wy + y;
                if(Structs.inBounds(cx, cy, world.width(), world.height())){
                    cons.accept(cx, cy);
                }
            }
        }
    }

    void makeFloor(){
        cache = new SpriteCache(world.width() * world.height(), false);
        CacheBatch batch = new CacheBatch(cache);
        Core.batch = batch;

        batch.beginCache();

        for(int x = 0; x < world.width(); x++){
            for(int y = 0; y < world.height(); y++){
                Tile tile = world.tile(x, y);
                if(!world.solid(x, y) && tile.floor != null){
                    Draw.rect(tile.floor.region, x * tilesize, y * tilesize, tile.rotation);
                }
            }
        }

        batch.endCache();

        Core.batch = zbatch;
    }

    void makeShadow(){
        fogs = new FrameBuffer(world.width(), world.height());

        byte[][] dark = new byte[world.width()][world.height()];
        byte[][] writeBuffer = new byte[world.width()][world.height()];

        byte darkIterations = 1;
        for(int x = 0; x < world.width(); x++){
            for(int y = 0; y < world.height(); y++){
                Tile tile = world.tile(x, y);
                if(tile.solid()){
                    dark[x][y] = darkIterations;
                }
            }
        }

        for(int i = 0; i < darkIterations; i++){
            for(int x = 0; x < world.width(); x++){
                for(int y = 0; y < world.height(); y++){
                    boolean min = false;
                    for(Point2 point : Geometry.d4){
                        int newX = x + point.x, newY = y + point.y;
                        if(Structs.inBounds(newX, newY, world.width(), world.height()) && dark[newX][newY] < dark[x][y]){
                            min = true;
                            break;
                        }
                    }
                    writeBuffer[x][y] = (byte)Math.max(0, dark[x][y] - Mathf.num(min));
                }
            }

            for(int x = 0; x < world.width(); x++){
                System.arraycopy(writeBuffer[x], 0, dark[x], 0, world.height());
            }
        }

        Draw.proj().setOrtho(0, 0, fogs.getWidth(), fogs.getHeight());

        fogs.beginDraw(Color.WHITE);

        for(int x = 0; x < world.width(); x++){
            for(int y = 0; y < world.height(); y++){
                if(dark[x][y] > 0){
                    Draw.color(0f, 0f, 0f, dark[x][y] / (float)darkIterations + Mathf.random(0.12f));
                    Fill.square(x + 0.5f, y + 1.5f, 0.5f);
                }
            }
        }

        fogs.endDraw();

        Draw.color();
    }

    private void draw(Entity entity){
        Layer.z(entity.y);
        entity.draw();
    }
}
