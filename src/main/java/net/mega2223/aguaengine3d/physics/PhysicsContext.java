package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhysicsContext {

    List<PhysicsSystem> objects = new ArrayList<>();

    float drag, gravity[];
    boolean isActive;

    public PhysicsContext(float drag){
        this(drag,9.8F);
    }

    public PhysicsContext(float drag, float gravity){
        this(drag,new float[]{0,-1,0});
    }

    public PhysicsContext(float drag, float[] gravity){
        this.drag = drag;
        this.gravity = gravity;
    }

    public void doLogic(){
        doLogic(1);
    }

    public void doLogic(float time){
        if(time == 0){return;}
        for(PhysicsSystem sy : objects){sy.doLogic(time,drag,gravity);}
    }

    public List<PhysicsSystem> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public void addObject(PhysicsSystem object){
        objects.add(object);
    }

    public void remove(PhysicsSystem object){
        objects.remove(object);
    }

    public float getDrag() {
        return drag;
    }

    public void setDrag(float drag) {
        this.drag = drag;
    }

    public float[] getGravity() {
        return gravity.clone();
    }

    public void setGravity(float[] gravity) {
        System.arraycopy(gravity,0,this.gravity,0,this.gravity.length);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
