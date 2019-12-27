package inferno.graphics;

import arc.struct.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.pooling.*;

public class QueueBatch extends SpriteBatch{
    private Array<DrawRequest> requests = new Array<>();
    private boolean flushing;

    public void blend(Blending blending){
        this.blending = blending;
    }

    @Override
    protected void draw(Texture texture, float[] spriteVertices, int offset, int count){
        throw new UnsupportedOperationException();
    }

    @Override
    protected void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float rotation){
        DrawRequest req = Pools.obtain(DrawRequest.class, DrawRequest::new);
        req.x = x;
        req.y = y;
        req.originX = originX;
        req.originY = originY;
        req.width = width;
        req.height = height;
        req.color = colorPacked;
        req.rotation = rotation;
        req.region = region;
        req.blendColor = mixColorPacked;
        requests.add(req);
    }

    @Override
    public void flush(){

        if(!flushing && !requests.isEmpty()){
            flushing = true;
            requests.sort();

            for(DrawRequest req : requests){
                colorPacked = req.color;
                mixColorPacked = req.blendColor;
                super.draw(req.region, req.x, req.y, req.originX, req.originY, req.width, req.height, req.rotation);
            }

            Pools.freeAll(requests);
            requests.clear();
            flushing = false;
        }
        super.flush();
    }
}
