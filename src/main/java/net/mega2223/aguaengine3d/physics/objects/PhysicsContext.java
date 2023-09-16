package net.mega2223.aguaengine3d.physics.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhysicsContext {

    List<PhysicsSystem> objects = new ArrayList<>();
    List<PhysicsForce> globalForces = new ArrayList<>();

    float drag;
    final float[] gravity;
    boolean isActive;

    public PhysicsContext(float drag){
        this(drag,.98F);
    }

    public PhysicsContext(float drag, float gravity){
        this(drag,new float[]{0,-1*gravity,0});
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
        for(PhysicsSystem sy : objects){
            for(PhysicsForce act : globalForces){
                act.update(sy,time);
            }
            sy.doLogic(time,drag,gravity);
        }
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

    public void addForce(PhysicsForce force){
        globalForces.add(force);
    }

    public void removeForce(PhysicsForce force){
        globalForces.remove(force);
    }

    public List<PhysicsForce> getGlobalForces(){
        return Collections.unmodifiableList(globalForces);
    }

}
