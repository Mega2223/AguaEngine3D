package net.mega2223.aguaengine3d.physics.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhysicsContext {

    List<PhysicsSystem> objects = new ArrayList<>();
    List<PhysicsForce> globalForces = new ArrayList<>();

    boolean isActive;

    public PhysicsContext(){

    }

    public void doLogic(float time){
        if(time == 0){return;}
        for(PhysicsSystem sy : objects){
            for(PhysicsForce act : globalForces){
                act.update(sy,time);
            }
            sy.doLogic(time);
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
