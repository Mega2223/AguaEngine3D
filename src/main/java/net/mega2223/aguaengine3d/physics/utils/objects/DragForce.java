package net.mega2223.aguaengine3d.physics.utils.objects;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.PhysicsManager;
import net.mega2223.aguaengine3d.physics.objects.PhysicsForce;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

public class DragForce implements PhysicsForce {
    final float k1,k2;
    float[] buffer = new float[3];

    public DragForce(float k1, float k2){
        this.k1 = k1;
        this.k2 = k2;
    }

    @Override
    public void update(PhysicsSystem system, float time) {
        float x = system.getVelocityX(), y = system.getVelocityY(), z = system.getVelocityZ();
        float mag = VectorTranslator.getMagnitude(x,y,z);
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
        VectorTranslator.normalizeVector(buffer);
        VectorTranslator.flipVector(buffer);

    }
}
