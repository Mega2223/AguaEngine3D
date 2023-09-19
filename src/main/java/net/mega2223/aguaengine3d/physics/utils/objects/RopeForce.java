package net.mega2223.aguaengine3d.physics.utils.objects;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.objects.PhysicsForce;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

public class RopeForce implements PhysicsForce {
    final float[] coord;
    final float len;
    final float stiffness;
    protected float[] buffer = new float[3];

    public RopeForce(float len, float stiffness, float cX, float cY, float cZ){
        coord = new float[]{cX,cY,cZ};
        this.len = len;
        this.stiffness = stiffness;
    }

    @Override
    public void update(PhysicsSystem system, float time) {
        buffer[0] = coord[0] - system.getCoordX();
        buffer[1] = coord[1] - system.getCoordY();
        buffer[2] = coord[2] - system.getCoordZ();
        float f = -stiffness * (VectorTranslator.getMagnitudeVec3(buffer) - len);
        if(f >= 0){return;}
        VectorTranslator.normalizeVec3(buffer);
        VectorTranslator.scaleVector(buffer,f);
        VectorTranslator.scaleVector(buffer,time);
        VectorTranslator.flipVector(buffer);
        system.applyForce(buffer);
    }

}
