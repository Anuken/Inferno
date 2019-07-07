package inferno.graphics;

import io.anuke.arc.collection.Array;
import io.anuke.arc.graphics.Texture;
import io.anuke.arc.graphics.g2d.SpriteBatch;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.util.pooling.Pools;

public class QueueBatch extends SpriteBatch{
    private Array<DrawRequest> requests = new Array<>();
    private boolean flushing;

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
