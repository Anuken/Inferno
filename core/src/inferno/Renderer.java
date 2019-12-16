package inferno;

import inferno.entity.*;
import inferno.graphics.*;
import inferno.world.*;
import io.anuke.arc.*;
import io.anuke.arc.func.*;
import io.anuke.arc.graphics.*;
import io.anuke.arc.graphics.Texture.*;
import io.anuke.arc.graphics.g2d.*;
import io.anuke.arc.graphics.glutils.*;
import io.anuke.arc.maps.*;
import io.anuke.arc.maps.objects.*;
import io.anuke.arc.math.*;
import io.anuke.arc.math.geom.*;
import io.anuke.arc.util.*;
import io.anuke.arc.util.Log.*;
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
    private Color ambient = new Color(0.2f, 0.06f, 0.02f, 0.5f);

    private Shader fog = new Shader(Core.files.internal("dshaders/default.vertex.glsl"), Core.files.internal("dshaders/fog.fragment.glsl"));
    private Shader light = new Shader(Core.files.internal("dshaders/default.vertex.glsl"), Core.files.internal("dshaders/light.fragment.glsl")){
        @Override
        public void apply(){
            light.setUniformf("u_ambient", ambient);
        }
    };
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
        if(prof) Time.mark();
        Drawf.sort(true);

        Entity target = ui.hasDialogue() ? ui.getDialogueFace() : player;

        if(target != null){
            Core.camera.position.lerpDelta(target.x, target.y, target == player && ui.hasDialogue() ? 0.1f : 0.03f);
        }
        if(!ui.hasDialogue()){
            camera.position.clamp(player.x - lim, player.x + lim, player.y - lim, player.y + lim);
        }
        updateShake(1f);
        float px = Core.camera.position.x, py = Core.camera.position.y;
        Core.camera.position.snap();
        if((Core.graphics.getHeight() / scale) % 2 == 1){
            camera.position.y += 0.5f;
        }

        Core.camera.update();

        Core.batch = lbatch;
        Draw.proj(Core.camera.projection());
        Core.batch = zbatch;
        Draw.proj(Core.camera.projection());

        if(prof) Time.mark();
        shadow.beginDraw(Color.clear);
        drawShadows();
        shadow.endDraw();
        if(prof) Log.info("| Shadows: " + Time.elapsed());

        buffer.beginDraw(Color.black);

        if(prof) Time.mark();
        drawWorld();
        if(prof) Log.info("| World: " + Time.elapsed());

        if(prof) Time.mark();
        charGroup.draw(this::draw);
        bulletGroup.draw(this::draw);
        effectGroup.draw(this::draw);
        if(prof) Log.info("| Bullets/chars/effects: " + Time.elapsed());

        Drawf.sort(false);

        if(prof) Time.mark();
        Draw.shader(fog);
        Draw.fbo(fogs.getTexture(), world.width(), world.height(), tilesize);
        Draw.shader();

        buffer.endDraw();

        Draw.color();
        lights.beginDraw(Color.clear);
        lbatch.blend(Blending.additive);
        lbatch.flush();
        lights.endDraw();

        buffer.begin();

        Draw.color();
        Draw.shader(light);
        Draw.rect(Draw.wrap(lights.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
        Draw.shader();

        buffer.endDraw();
        if(prof) Log.info("| Lights & stuff: " + Time.elapsed());

        if(prof) Time.mark();
        if(dobloom) bloom.capture();
        Draw.color();
        Draw.blend(Blending.disabled);
        Draw.rect(Draw.wrap(buffer.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
        Draw.blend();
        if(dobloom) bloom.render();

        Core.camera.position.set(px, py);
        if(prof) Log.info("| Bloom: " + Time.elapsed());

        ScreenRecorder.record();
        if(prof) Log.info("Draw: " + Time.elapsed());

        if(prof) Log.info("---END---");
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
        Draw.color();
        cull((x, y) -> {
            Tile tile = world.tile(x, y);
            if(tile.shadowed && tile.wall != null){
                tile.wall.drawShadow(x, y);
            }
        });

        charGroup.draw(Entity::drawShadow);
    }

    void drawWorld(){
        Draw.color();

        //draw cached floor
        Draw.flush();
        if(prof) Time.mark();
        cache.setProjectionMatrix(Core.camera.projection());
        cache.begin();
        cache.draw(0);
        cache.end();
        if(prof) Log.info("| | Cached floor: " + Time.elapsed());

        //do not sort base layer for efficiency
        Drawf.sort(false);

        if(prof) Time.mark();
        //overlays
        cull((x, y) -> {
            Tile tile = world.tile(x, y);
            if(!world.solid(x, y) && tile.overlay != null){
                tile.overlay.draw(x, y);
            }
        });
        if(prof) Log.info("| | Overlay: " + Time.elapsed());

        if(prof) Time.mark();
        Draw.color(0f, 0f, 0f, 0.3f);
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

        if(prof) Log.info("| | Walls: " + Time.elapsed());
    }

    void cull(Intc2 cons){
        int xrange = (int)(Core.camera.width / tilesize / 2 + 3);
        int yrange = (int)(Core.camera.height / tilesize / 2 + 3);
        int wx = (int)(Core.camera.position.x / tilesize);
        int wy = (int)(Core.camera.position.y / tilesize);

        for(int x = -xrange; x < xrange; x++){
            for(int y = -yrange; y < yrange; y++){
                int cx = wx + x, cy = wy + y;
                if(Structs.inBounds(cx, cy, world.width(), world.height())){
                    cons.get(cx, cy);
                }
            }
        }
    }

    void makeFloor(){
        cache = new SpriteCache(world.width() * world.height() + world.getObjects().size*2 + 500, false);
        CacheBatch batch = new CacheBatch(cache);
        Core.batch = batch;

        batch.beginCache();

        Draw.color();

        for(int x = 0; x < world.width(); x++){
            for(int y = 0; y < world.height(); y++){
                Tile tile = world.tile(x, y);
                if((!world.solid(x, y) || (tile.wall != null && tile.wall.clear) || (tile.wall != null && tile.wall.name.equals("shelf"))) && tile.floor != null){
                    Draw.rect(tile.floor.regions[Block.rand(x, y, tile.floor.regions.length) - 1], x * tilesize, y * tilesize, tile.rotation);
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

        fogs.beginDraw(Color.white);

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
