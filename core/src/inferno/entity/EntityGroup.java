package inferno.entity;

import io.anuke.arc.Core;
import io.anuke.arc.collection.Array;
import io.anuke.arc.function.Consumer;
import io.anuke.arc.function.Predicate;
import io.anuke.arc.graphics.Camera;
import io.anuke.arc.math.geom.QuadTree;
import io.anuke.arc.math.geom.Rectangle;

public class EntityGroup<T extends Entity>{
    private final boolean useTree;
    private final Array<T> entityArray = new Array<>(false, 16);
    private final Array<T> entitiesToRemove = new Array<>(false, 16);
    private final Array<T> entitiesToAdd = new Array<>(false, 16);
    private boolean clip = false;
    private Rectangle viewport = new Rectangle();
    private QuadTree tree;

    public EntityGroup(boolean useTree){
        this.useTree = useTree;

        if(useTree){
            tree = new QuadTree<>(new Rectangle(0, 0, 0, 0));
        }
    }

    public void update(){
        updateEvents();

        if(useTree()){
            EntityCollisions.updatePhysics(this);
        }

        for(Entity e : all()){
            e.update();
        }
    }

    public void collide(EntityGroup other){
        EntityCollisions.collideGroups(this, other);
    }

    public void draw(){
        draw(e -> true);
    }

    public void draw(Predicate<T> toDraw){
        draw(toDraw, Entity::draw);
    }

    public void draw(Consumer<T> cons){
        draw(e -> true, cons);
    }

    public void draw(Predicate<T> toDraw, Consumer<T> cons){
        if(clip){
            Camera cam = Core.camera;
            viewport.set(cam.position.x - cam.width / 2, cam.position.y - cam.height / 2, cam.width, cam.height);
        }

        for(T e : entityArray){
            if(!toDraw.test(e) || !e.isAdded()) continue;

            if(!clip || viewport.overlaps(e.x - e.drawSize()/2f, e.y - e.drawSize()/2f, e.drawSize(), e.drawSize())){
                cons.accept((T)e);
            }
        }
    }

    public boolean useTree(){
        return useTree;
    }

    private void updateEvents(){

        for(T e : entitiesToAdd){
            if(e == null)
                continue;
            entityArray.add(e);
            e.added();
        }

        entitiesToAdd.clear();

        for(T e : entitiesToRemove){
            entityArray.removeValue(e, true);
            e.removed();
        }

        entitiesToRemove.clear();
    }

    @SuppressWarnings("unchecked")
    public void intersect(float x, float y, float width, float height, Consumer<? super T> out){
        //don't waste time for empty groups
        if(isEmpty()) return;
        tree().getIntersect(out, x, y, width, height);
    }

    public QuadTree tree(){
        if(!useTree) throw new RuntimeException("This group does not support quadtrees! Enable quadtrees when creating it.");
        return tree;
    }

    /** Resizes the internal quadtree, if it is enabled.*/
    public void resize(float x, float y, float w, float h){
        if(useTree){
            tree = new QuadTree<>(new Rectangle(x, y, w, h));
        }
    }

    public boolean isEmpty(){
        return entityArray.size == 0;
    }

    public int size(){
        return entityArray.size;
    }

    public int count(Predicate<T> pred){
        int count = 0;
        for(int i = 0; i < entityArray.size; i++){
            if(pred.test(entityArray.get(i))) count++;
        }
        return count;
    }

    public void add(T type){
        if(type == null) throw new RuntimeException("Cannot add a null entity!");
        if(type.group != null) return;
        type.group = this;
        entitiesToAdd.add(type);
    }

    public void remove(T type){
        if(type == null) throw new RuntimeException("Cannot remove a null entity!");
        type.group = null;
        entitiesToRemove.add(type);
    }

    public void clear(){
        for(T entity : entityArray)
            entity.group = null;

        for(T entity : entitiesToAdd)
            entity.group = null;

        for(T entity : entitiesToRemove)
            entity.group = null;

        entitiesToAdd.clear();
        entitiesToRemove.clear();
        entityArray.clear();
    }

    public T find(Predicate<T> pred){

        for(int i = 0; i < entityArray.size; i++){
            if(pred.test(entityArray.get(i))) return entityArray.get(i);
        }

        return null;
    }

    /** Returns the logic-only array for iteration. */
    public Array<T> all(){
        return entityArray;
    }
}
