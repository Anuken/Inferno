package inferno;

import inferno.entity.*;
import inferno.graphics.*;
import inferno.world.*;
import io.anuke.arc.*;
import io.anuke.arc.function.*;
import io.anuke.arc.graphics.*;
import io.anuke.arc.graphics.Texture.*;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.graphics.glutils.*;
import io.anuke.arc.maps.*;
import io.anuke.arc.maps.objects.*;
import io.anuke.arc.math.*;
import io.anuke.arc.math.geom.*;
import io.anuke.arc.util.*;
import io.anuke.arc.util.noise.*;

import static inferno.Inferno.*;
import static io.anuke.arc.Core.*;

public class Renderer implements ApplicationListener{
    public LayerBatch zbatch;
    public QueueBatch lbatch;

    private FrameBuffer buffer = new FrameBuffer(2, 2);
    private FrameBuffer shadow = new FrameBuffer(2, 2);
    private FrameBuffer lights = new FrameBuffer(2, 2);
    private FrameBuffer fogs;
    private float lim = 10f;
    private float shakeIntensity, shaketime;

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
        Drawf.sort(true);

        Entity target = ui.hasDialogue() ? ui.getDialogueFace() : player;

        Core.camera.position.lerpDelta(target.x, target.y, target == player && ui.hasDialogue() ? 0.1f : 0.03f);
        if(!ui.hasDialogue()){
            camera.position.clamp(player.x - lim, player.x + lim, player.y - lim, player.y + lim);
        }
        updateShake(1f);
        float px = Core.camera.position.x, py = Core.camera.position.y;
        Core.camera.position.snap();
        if(Core.graphics.getHeight() % scale == 1 || scale == 2){
            camera.position.y += 0.5f;
        }

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

        Drawf.sort(false);

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

    public void shake(float intensity){
        shake(intensity, intensity);
    }

    public void shake(float intensity, float duration){
        shakeIntensity = Math.max(intensity, shakeIntensity);
        shaketime = Math.max(shaketime, duration);
    }

    public void jump(float angle, float intensity){
        camera.position.add(Tmp.v4.trns(angle, intensity));
    }

    void updateShake(float scale){
        if(shaketime > 0){
            float intensity = shakeIntensity * (settings.getInt("screenshake", 4) / 4f) * scale;
            camera.position.add(Mathf.range(intensity), Mathf.range(intensity));
            shakeIntensity -= 0.25f * Time.delta();
            shaketime -= Time.delta();
            shakeIntensity = Mathf.clamp(shakeIntensity, 0f, 100f);
        }else{
            shakeIntensity = 0f;
        }
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
        Drawf.sort(false);

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

        Drawf.sort(true);

        cull((x, y) -> {
            Drawf.z(y * tilesize - tilesize / 2f);
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
        cache = new SpriteCache(world.width() * world.height() + world.getObjects().size*2 + 200, true);
        CacheBatch batch = new CacheBatch(cache);
        Core.batch = batch;

        batch.beginCache();

        Draw.color();

        for(int x = 0; x < world.width(); x++){
            for(int y = 0; y < world.height(); y++){
                Tile tile = world.tile(x, y);
                if((!world.solid(x, y) || (tile.wall != null && tile.wall.clear) || (tile.wall != null && tile.wall.name.equals("shelf"))) && tile.floor != null){
                    int rand = Mathf.randomSeed(x + y * world.width(), 1, 3);
                    Draw.rect(tile.floor.region2 == null ? tile.floor.region : rand == 1 ? tile.floor.region : rand == 2 ? tile.floor.region2 : tile.floor.region3,
                            x * tilesize, y * tilesize, tile.rotation);
                }
            }
        }

        Draw.colorl(0.237f);
        Lines.stroke(2f);
        circle(world.width() * tilesize / 2f, world.height() * tilesize/2f, 290f);
        circle(world.width() * tilesize / 2f, world.height() * tilesize/2f, 370f);
        circle(world.width() * tilesize / 2f, world.height() * tilesize/2f, 450f);
        Draw.color();

        for(MapObject object : world.getObjects()){
            TextureMapObject tex = (TextureMapObject)object;
            Draw.rect(tex.textureRegion, tex.x + tex.textureRegion.getWidth()/2f, tex.y + tex.textureRegion.getHeight()/2f);
        }

        batch.endCache();

        Core.batch = zbatch;
    }

    public void updateShadows(){
        makeShadow();
    }

    void circle(float x, float y, float radius){
        int vertices = (int)(radius * 1.5f);

        float step = 360f/vertices;
        outer:
        for(int i = 0; i < vertices; i++){
            Tmp.v1.trns(i*step, radius);
            if(Noise.nnoise((int)(Tmp.v1.x + x), (int)(Tmp.v1.y + y), 2f, 1f) + Noise.nnoise((int)(Tmp.v1.x + x), (int)(Tmp.v1.y + y), 20f, 1f)*2 < -0.04){
                continue;
            }

            for(MapObject object : world.getObjects()){
                TextureMapObject tex = (TextureMapObject)object;
                float w = tex.textureRegion.getWidth()/2f - 4f;
                if(Tmp.v1.dst2(tex.x + tex.textureRegion.getWidth()/2f - x, tex.y + tex.textureRegion.getHeight()/2f - y) < w*w){
                    continue outer;
                }
            }

            float rand = Noise.nnoise((int)Tmp.v1.x, -(int)Tmp.v1.y, 20f, 5f);

            Tmp.v2.trns((i+1)*step, radius);
            Tmp.v1.add(Mathf.range(rand), Mathf.range(rand));
            Tmp.v2.add(Mathf.range(rand), Mathf.range(rand));
            Lines.line(x + Tmp.v1.x, y + Tmp.v1.y, x + Tmp.v2.x, y + Tmp.v2.y);
        }
    }

    void makeShadow(){
        if(fogs == null){
            fogs = new FrameBuffer(world.width(), world.height());
        }

        byte[][] dark = new byte[world.width()][world.height()];
        byte[][] writeBuffer = new byte[world.width()][world.height()];

        byte darkIterations = 1;
        for(int x = 0; x < world.width(); x++){
            for(int y = 0; y < world.height(); y++){
                Tile tile = world.tile(x, y);
                if(tile.solid() && !(tile.wall != null && tile.wall.clear)){
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
        Drawf.z(entity.y);
        entity.draw();
    }
}
