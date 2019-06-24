package inferno.entity;

import io.anuke.arc.math.geom.Position;

@SuppressWarnings("unchecked")
public abstract class Entity implements Position{
    protected static int lastID = 0;

    public int id;
    public EntityGroup group;
    public float x, y;

    public Entity(){
        id = lastID ++;
    }

    public void update(){}
    public void removed(){}
    public void added(){}
    public void draw(){}
    public void drawLight(){}
    public abstract EntityGroup targetGroup();

    public float drawSize(){
        return 10f;
    }

    public void add(){
        if(targetGroup() != null){
            targetGroup().add(this);
        }
    }

    public void remove(){
        if(group != null){
            group.remove(this);
        }

        group = null;
    }

    public boolean isAdded(){
        return group != null;
    }

    public void set(float x, float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public float getX(){
        return x;
    }

    @Override
    public float getY(){
        return y;
    }
}
