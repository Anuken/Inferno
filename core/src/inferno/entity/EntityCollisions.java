package inferno.entity;

import arc.struct.*;
import arc.math.*;
import arc.math.geom.*;

import static inferno.Inferno.*;

@SuppressWarnings("unchecked")
public class EntityCollisions{
    //range for tile collision scanning
    private static final int r = 1;
    //move in 1-unit chunks
    private static final float seg = 1f;

    //tile collisions
    private static Rect tmp = new Rect();
    private static Vec2 vector = new Vec2();
    private static Vec2 l1 = new Vec2();
    private static Rect r1 = new Rect();
    private static Rect r2 = new Rect();

    //entity collisions
    private static Seq<SolidEntity> arrOut = new Seq<>();

    public static void move(SolidEntity entity, float deltax, float deltay){

        boolean movedx = false;

        while(Math.abs(deltax) > 0 || !movedx){
            movedx = true;
            moveDelta(entity, Math.min(Math.abs(deltax), seg) * Mathf.sign(deltax), 0, true);

            if(Math.abs(deltax) >= seg){
                deltax -= seg * Mathf.sign(deltax);
            }else{
                deltax = 0f;
            }
        }

        boolean movedy = false;

        while(Math.abs(deltay) > 0 || !movedy){
            movedy = true;
            moveDelta(entity, 0, Math.min(Math.abs(deltay), seg) * Mathf.sign(deltay), false);

            if(Math.abs(deltay) >= seg){
                deltay -= seg * Mathf.sign(deltay);
            }else{
                deltay = 0f;
            }
        }
    }

    public static void moveDelta(SolidEntity entity, float deltax, float deltay, boolean x){

        Rect rect = r1;
        entity.hitboxTile(rect);
        entity.hitboxTile(r2);
        rect.x += deltax;
        rect.y += deltay;

        int tilex = Math.round((rect.x + rect.width / 2) / tilesize), tiley = Math.round((rect.y + rect.height / 2) / tilesize);

        for(int dx = -r; dx <= r; dx++){
            for(int dy = -r; dy <= r; dy++){
                int wx = dx + tilex, wy = dy + tiley;
                if(solid(wx, wy)){
                    tmp.setSize(tilesize).setCenter(wx * tilesize, wy * tilesize);

                    if(tmp.overlaps(rect)){
                        Vec2 v = Geometry.overlap(rect, tmp, x);
                        rect.x += v.x;
                        rect.y += v.y;
                    }
                }
            }
        }

        entity.x = (entity.x + rect.x - r2.x);
        entity.y = (entity.y + rect.y - r2.y);
    }

    public static boolean overlapsTile(Rect rect){
        return overlapsTile(rect, EntityCollisions::solid);
    }

    public static boolean overlapsTile(Rect rect, SolidChecker solid){
        rect.getCenter(vector);
        int r = 1;

        //assumes tiles are centered
        int tilex = Math.round(vector.x / tilesize);
        int tiley = Math.round(vector.y / tilesize);

        for(int dx = -r; dx <= r; dx++){
            for(int dy = -r; dy <= r; dy++){
                int wx = dx + tilex, wy = dy + tiley;
                if(solid.solid(wx, wy)){
                    r2.setSize(tilesize).setCenter(wx * tilesize, wy * tilesize);

                    if(r2.overlaps(rect)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean solid(int x, int y){
        return world.solid(x, y);
    }

    private static void checkCollide(Entity entity, Entity other){
        SolidEntity a = (SolidEntity)entity;
        SolidEntity b = (SolidEntity)other;
        /*

        a.hitbox(EntityCollisions.r1);
        b.hitbox(EntityCollisions.r2);

        r1.x += (a.lastPosition.x - a.x);
        r1.y += (a.lastPosition.y - a.y);
        r2.x += (b.lastPosition.x - b.x);
        r2.y += (b.lastPosition.y - b.y);

        float vax = a.x - a.lastPosition.x;
        float vay = a.y - a.lastPosition.y;
        float vbx = b.x - b.lastPosition.x;
        float vby = b.y - b.lastPosition.y;*/

        if(a != b && a.collides(b) && b.collides(a)){
            //l1.set(a.x, a.y);
            //boolean collide = r1.overlaps(r2) || collide(r1.x, r1.y, r1.width, r1.height, vax, vay,
            //r2.x, r2.y, r2.width, r2.height, vbx, vby, l1);
            //if(collide){
            a.collision(b, l1.x, l1.y);
            b.collision(a, l1.x, l1.y);
            //}
        }
    }

    private static boolean collide(float x1, float y1, float w1, float h1, float vx1, float vy1,
                            float x2, float y2, float w2, float h2, float vx2, float vy2, Vec2 out){
        float px = vx1, py = vy1;

        vx1 -= vx2;
        vy1 -= vy2;

        float xInvEntry, yInvEntry;
        float xInvExit, yInvExit;

        if(vx1 > 0.0f){
            xInvEntry = x2 - (x1 + w1);
            xInvExit = (x2 + w2) - x1;
        }else{
            xInvEntry = (x2 + w2) - x1;
            xInvExit = x2 - (x1 + w1);
        }

        if(vy1 > 0.0f){
            yInvEntry = y2 - (y1 + h1);
            yInvExit = (y2 + h2) - y1;
        }else{
            yInvEntry = (y2 + h2) - y1;
            yInvExit = y2 - (y1 + h1);
        }

        float xEntry, yEntry;
        float xExit, yExit;

        xEntry = xInvEntry / vx1;
        xExit = xInvExit / vx1;

        yEntry = yInvEntry / vy1;
        yExit = yInvExit / vy1;

        float entryTime = Math.max(xEntry, yEntry);
        float exitTime = Math.min(xExit, yExit);

        if(entryTime > exitTime || xExit < 0.0f || yExit < 0.0f || xEntry > 1.0f || yEntry > 1.0f){
            return false;
        }else{
            float dx = x1 + w1 / 2f + px * entryTime;
            float dy = y1 + h1 / 2f + py * entryTime;

            out.set(dx, dy);

            return true;
        }
    }

    public static void collideGroups(EntityGroup<?> groupa, EntityGroup<?> groupb){

        for(Entity entity : groupa.all()){
            if(!(entity instanceof SolidEntity))
                continue;

            SolidEntity solid = (SolidEntity)entity;

            solid.hitbox(r1);
            //r1.x += (solid.lastPosition.x - solid.x);
            //r1.y += (solid.lastPosition.y - solid.y);

            solid.hitbox(r2);
            r2.merge(r1);

            arrOut.clear();
            groupb.tree().intersect(r2, arrOut);

            for(SolidEntity sc : arrOut){
                sc.hitbox(r1);
                if(r2.overlaps(r1)){
                    checkCollide(entity, sc);
                }
            }
        }
    }

    public interface SolidChecker{
        boolean solid(int x, int y);
    }
}
