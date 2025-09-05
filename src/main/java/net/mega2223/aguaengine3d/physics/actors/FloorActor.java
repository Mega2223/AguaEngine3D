package net.mega2223.aguaengine3d.physics.actors;

import net.mega2223.aguaengine3d.physics.PhysicsContext;
import net.mega2223.aguaengine3d.physics.objects.Particle;

public class FloorActor implements PhysicsActor{
    @Override
    public void act(float deltaT, PhysicsContext context) {
        for(Particle object : context.getObjects()) {
            if(object.y() < 1){
                object.setVelocity(object.vx(), -object.vy(), object.vz());
                object.setCoordinates(object.x(),1,object.z());
            }
        }
    }
}
