package net.mega2223.aguaengine3d.physics.actors;

import net.mega2223.aguaengine3d.physics.PhysicsContext;
import net.mega2223.aguaengine3d.physics.PhysicsObject;

public class FloorActor implements PhysicsActor{
    @Override
    public void act(float deltaT, PhysicsContext context) {
        for(PhysicsObject object : context.getObjects()) {
            if(object.y() < 1){
                object.setVelocity(object.vx(), -object.vy() * .66F, object.vz());
                object.setCoordinates(object.x(),1,object.z());
            }
        }
    }
}
