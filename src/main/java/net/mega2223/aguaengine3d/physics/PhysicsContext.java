package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.physics.actors.PhysicsActor;
import net.mega2223.aguaengine3d.physics.collisions.CollisionManager;
import net.mega2223.aguaengine3d.physics.collisions.SimpleCollisionManager;
import net.mega2223.aguaengine3d.physics.forces.Force;
import net.mega2223.aguaengine3d.physics.objects.Particle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhysicsContext {

    protected List<PhysicsObject> objects = new ArrayList<>(32);
    protected List<PhysicsActor> actors = new ArrayList<>(16);
    protected List<Force> forces = new ArrayList<>(16);
    protected CollisionManager collisionManager = new SimpleCollisionManager(this);

    public long iteration = 0;

    public PhysicsContext(){

    }

    public void update(float deltaT){
        update(deltaT,1);
    }

    public void update(float deltaT, int collisionPasses){
        for(PhysicsActor actor : actors){
            actor.act(deltaT,this);
        }
        for(Force f : forces){
            for(PhysicsObject o : objects){
                f.apply(o.getActor(), deltaT);
            }
        }
        for(PhysicsObject o : objects){
            o.update(deltaT);
        }
        collisionManager.manageCollisions(deltaT,collisionPasses);
        iteration++;
    }

    public void addObject(PhysicsObject object){
        objects.add(object);
    }

    public void removeObject(PhysicsObject object){
        objects.remove(object);
    }

    public List<PhysicsObject> getObjects() {
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
