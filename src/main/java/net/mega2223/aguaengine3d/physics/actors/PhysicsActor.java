package net.mega2223.aguaengine3d.physics.actors;

import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.physics.PhysicsContext;

public interface PhysicsActor {
    void act(float deltaT, PhysicsContext context);
}
