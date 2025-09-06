package net.mega2223.aguaengine3d.physics.forces;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.PhysicsObject;
import net.mega2223.aguaengine3d.physics.objects.Particle;

public class Gravity implements Force {
    private final float[] force; // stores both direction and magnitude

    public Gravity(){
        this(9.8F);
    }

    public Gravity(float acceleration){
        this(acceleration,new float[]{0,-1,0,0});
    }

    public Gravity(float acceleration, float[] direction){
        this.force = direction.clone();
        VectorTranslator.scaleVector(this.force,acceleration);
    }

    @Override
    public void apply(PhysicsObject object) {
        // since gravity is proportional to mass it cancels out
        // the inverse mass multiplication,
        // so it's easier to just apply the total acceleration
        object.applyAcceleration(force[0],force[1],force[2]);
    }
}
