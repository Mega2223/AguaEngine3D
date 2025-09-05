package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.physics.actors.PhysicsActor;
import net.mega2223.aguaengine3d.physics.forces.Force;
import net.mega2223.aguaengine3d.physics.objects.Particle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhysicsContext {

    protected List<Particle> objects = new ArrayList<>(32);
    protected List<PhysicsActor> actors = new ArrayList<>(16);
    protected List<Force> forces = new ArrayList<>(16);

    public PhysicsContext(){

    }

    public void update(float deltaT){
        for(PhysicsActor actor : actors){
            actor.act(deltaT,this);
        }
        for(Force f : forces){
            for(Particle o : objects){
                f.apply(o);
            }
        }
        for(Particle o : objects){
            o.update(deltaT);
        }
    }

    public void addObject(Particle object){
        objects.add(object);
    }

    public void removeObject(Particle object){
        objects.remove(object);
    }

    public List<Particle> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    public void addActor(PhysicsActor actor){
        actors.add(actor);
    }

    public void removeActor(PhysicsActor actor){
        actors.remove(actor);
    }

    public void addForce(Force force){
        forces.add(force);
    }

    public void removeForce(Force force){
        forces.remove(force);
    }
}
