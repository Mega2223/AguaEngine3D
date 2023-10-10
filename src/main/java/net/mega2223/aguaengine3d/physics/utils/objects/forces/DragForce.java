package net.mega2223.aguaengine3d.physics.utils.objects.forces;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.objects.PhysicsForce;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;

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
        if(system instanceof RigidBodySystem){calculateForAngularMomentum((RigidBodySystem) system,time);}
        if(x == 0 && y == 0 && z == 0){return;}
        float drag = VectorTranslator.getMagnitudeVec3(x,y,z);
        drag = k1 * drag + k2 * drag * drag;
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
        VectorTranslator.normalizeVec3(buffer);
        VectorTranslator.scaleVec3(buffer,drag);
        VectorTranslator.scaleVec3(buffer,time);
        VectorTranslator.flipVector(buffer);
        system.applyForce(buffer);
    }

    private void calculateForAngularMomentum(RigidBodySystem system, float time){
        float x = system.getSpinX(), y = system.getSpinY(), z = system.getSpinZ();
        if(x == 0 && y == 0 && z == 0){return;}
        float drag = VectorTranslator.getMagnitudeVec3(x,y,z);
        drag = k1 * drag + k2 * drag * drag;
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
        VectorTranslator.normalizeVec3(buffer);
        VectorTranslator.scaleVec3(buffer,drag);
        VectorTranslator.scaleVec3(buffer,time);
        VectorTranslator.scaleVec3(buffer,0.1F);
        VectorTranslator.flipVector(buffer);
        system.applyTorque(buffer);
    }
}
