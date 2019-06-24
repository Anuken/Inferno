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
import io.anuke.arc.graphics.glutils.Shader;
import io.anuke.arc.math.Mathf;
import io.anuke.arc.math.geom.Geometry;
import io.anuke.arc.math.geom.Point2;
import io.anuke.arc.util.Structs;

import static inferno.Inferno.*;

public class Renderer implements ApplicationListener{
    public LayerBatch lbatch;

    private FrameBuffer buffer = new FrameBuffer(2, 2);
    private FrameBuffer shadow = new FrameBuffer(2, 2);
    private FrameBuffer lights = new FrameBuffer(2, 2);
    private FrameBuffer fogs;
    private float lim = 10f;

    private Shader fog = new Shader(Core.files.local("shaders/default.vertex.glsl"), Core.files.local("shaders/fog.fragment.glsl"));
    private Shader light = new Shader(Core.files.local("shaders/default.vertex.glsl"), Core.files.local("shaders/light.fragment.glsl"));

    public Renderer(){
        Core.atlas = new TextureAtlas(Core.files.internal("sprites/sprites.atlas"));
        Core.batch = lbatch = new LayerBatch();
        Core.camera = new Camera();

        buffer.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
    }

    @Override
    public void init(){
        makeShadow();
    }

    @Override
    public void update(){
        Layer.sort(true);

        Core.camera.position.lerpDelta(player.x, player.y, 0.03f).clamp(player.x - lim, player.x + lim, player.y - lim, player.y + lim);
        float px = Core.camera.position.x, py = Core.camera.position.y;
        Core.camera.position.snap();
        Core.camera.update();

        Draw.proj(Core.camera.projection());

        shadow.beginDraw(Color.CLEAR);
        drawShadows();
        shadow.endDraw();

        lights.beginDraw(Color.CLEAR);

        Draw.color(Color.RED, 1f);
        Fill.circle(player.x, player.y, 70f);

        lights.endDraw();


        buffer.beginDraw(Color.BLACK);

        drawWorld();

        charGroup.draw(this::draw);
        bulletGroup.draw(this::draw);
        effectGroup.draw(this::draw);

        //top wall edges
        Draw.color();
        cull((x, y) -> {
            Layer.z(y * tilesize - tilesize / 2f);
            Tile tile = world.tile(x, y);
            if(tile.wall != null && tile.wall == Core.atlas.find("wall")){
                int i = 0;
                for(Point2 p : Geometry.d4){
                    if(world.tile(x + p.x, y + p.y).wall != tile.wall){
                        Draw.rect("wall-edge", x * tilesize, y * tilesize + tilesize, i*90);
                    }
                    i++;
                }
            }
        });

        Layer.sort(false);

        Draw.shader(fog);
        Draw.fbo(fogs.getTexture(), world.width(), world.height(), tilesize);
        Draw.shader();

        Draw.color();
        Draw.shader(light);
        Draw.rect(Draw.wrap(lights.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
        Draw.shader();

        buffer.endDraw();

        Draw.color();
        Draw.blend(Blending.disabled);
        Draw.rect(Draw.wrap(buffer.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
        Draw.blend();

        Core.camera.position.set(px, py);
    }

    @Override
    public void resize(int width, int height){
        buffer.resize(width / scale, height / scale);
        shadow.resize(width / scale, height / scale);
        lights.resize(width / scale, height / scale);
        Core.camera.resize(width / scale, height / scale);
    }

    void drawShadows(){
        cull((x, y) -> {
            Tile tile = world.tile(x, y);
            if(tile.shadowed){
                Draw.rect("shadow", x * tilesize, y * tilesize);
            }
        });
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

        Draw.color(0f, 0f, 0f, 0.4f);
        Draw.rect(Draw.wrap(shadow.getTexture()), Core.camera.position.x, Core.camera.position.y, Core.camera.width, -Core.camera.height);
        Draw.color();

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

    void makeShadow(){
        fogs = new FrameBuffer(world.width(), world.height());

        byte[][] dark = new byte[world.width()][world.height()];
        byte[][] writeBuffer = new byte[world.width()][world.height()];

        byte darkIterations = 3;
        for(int x = 0; x < world.width(); x++){
            for(int y = 0; y < world.height(); y++){
                Tile tile = world.tile(x, y);
                if(tile.solid){
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
