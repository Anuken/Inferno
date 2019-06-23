package inferno.graphics;

import io.anuke.arc.collection.Array;
import io.anuke.arc.graphics.g2d.SpriteBatch;
import io.anuke.arc.graphics.g2d.TextureRegion;
import io.anuke.arc.util.pooling.Pools;

public class LayerBatch extends SpriteBatch{
    private Array<DrawRequest> requests = new Array<>();
    private boolean sort;
    private boolean flushing;

    public float z;

    public void sort(boolean sort){
        if(this.sort != sort){
            flush();
        }
        this.sort = sort;
    }

    @Override
    protected void draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float rotation){
        if(sort){
            DrawRequest req = Pools.obtain(DrawRequest.class, DrawRequest::new);
            req.x = x;
            req.y = y;
            req.z = z;
            req.originX = originX;
            req.originY = originY;
            req.width = width;
            req.height = height;
            req.color = colorPacked;
            req.rotation = rotation;
            req.region = region;
            requests.add(req);
        }else{
            super.draw(region, x, y, originX, originY, width, height, rotation);
        }
    }

    @Override
    protected void flush(){
        if(!flushing && !requests.isEmpty()){
            flushing = true;
            requests.sort();

            for(DrawRequest req : requests){
                colorPacked = req.color;
                super.draw(req.region, req.x, req.y, req.originX, req.originY, req.width, req.height, req.rotation);
            }

            Pools.free(requests);
            requests.clear();
            flushing = false;
        }
        super.flush();
    }

    private static class DrawRequest implements Comparable<DrawRequest>{
        TextureRegion region;
        float x, y, z, originX, originY, width, height, rotation, color;

        @Override
        public int compareTo(DrawRequest o){
            return Float.compare(o.z, z);
        }
    }
}
