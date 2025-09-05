package net.mega2223.aguaengine3d.physics.forces;

import net.mega2223.aguaengine3d.physics.objects.Particle;

public interface Force {
    void apply(Particle object);
}
