package net.mega2223.aguaengine3d.physics.objects.actors;

import net.mega2223.aguaengine3d.physics.PhysicsContext;
import net.mega2223.aguaengine3d.physics.PhysicsObject;

/**
 Simulates a floor
 @deprecated use a Plane hitbox instead
  * */
@Deprecated
public class FloorActor implements PhysicsActor{
    float y;
    public FloorActor(float y){
        this.y = y;
    }
    @Override
    public void act(float deltaT, PhysicsContext context) {
        for(PhysicsObject object : context.getObjects()) {
            if(object.y() < 1+y){
                object.setVelocity(object.vx(), -object.vy()* .7F, object.vz());
                object.setCoordinates(object.x(),1,object.z());
            }
        }
    }
}
