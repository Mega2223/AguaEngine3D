package net.mega2223.aguaengine3d.physics.forces;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.PhysicsObject;
import net.mega2223.aguaengine3d.physics.objects.Particle;

public class Drag implements Force{
    float kLinear, kSquared;

    private final float[] buffer = new float[4];

    public Drag(float kLinear, float kSquared) {
        this.kLinear = Math.abs(kLinear);
        this.kSquared = Math.abs(kSquared);
    }

    public Drag(float kLinear){
        this(kLinear, 0);
    }

    @Override
    public void apply(PhysicsObject object,float deltaT) {
        object.getVelocity(buffer);
        final float vel = VectorTranslator.magnitude(buffer);
        VectorTranslator.normalize(buffer);
        VectorTranslator.flipVector(buffer);
        VectorTranslator.scaleVector(buffer,kLinear * vel + kSquared * vel * vel);
        object.applyForce(buffer[0]*deltaT,buffer[1]*deltaT,buffer[2]*deltaT);
    }
}
