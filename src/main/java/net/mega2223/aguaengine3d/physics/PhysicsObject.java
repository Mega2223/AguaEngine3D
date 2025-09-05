package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;

import java.util.Arrays;

public class PhysicsObject {

    protected final float[] pos = new float[4];
    protected final float[] velocity =  new float[4];

    private final float[] accelerationAccumulator = new float[4];
    private final float[] posDerivative = new float[4];

    protected float mass, invMass;

    public PhysicsObject(float mass){
        this.mass = mass; this.invMass = 1F/mass;
    }

    public void update(float deltaT){
        VectorTranslator.addToVector(velocity,accelerationAccumulator);
        Arrays.fill(accelerationAccumulator,0);

        VectorTranslator.scaleVector(velocity,deltaT,posDerivative);
        VectorTranslator.addToVector(pos, posDerivative);
    }

    public void translate(float x, float y, float z){
        pos[0] += x; pos[1] += y; pos[2] += z;
    }

    public void setCoordinates(float x, float y, float z){
        pos[0] = x; pos[1] = y; pos[2] = z;
    }

    public void applyVelocity(float vx, float vy, float vz){
        velocity[0] += vx; velocity[1] += vy; velocity[2] += vz;
    }

    public void applyImpulse(float ix, float iy, float iz){
        this.applyVelocity(ix*invMass,iy*invMass,iz*invMass);
    }

    public void setVelocity(float vx, float vy, float vz){
        velocity[0] = vx; velocity[1] = vy; velocity[2] = vz;
    }

    public void applyAcceleration(float ax, float ay, float az){
        accelerationAccumulator[0] += ax; accelerationAccumulator[1] += ay; accelerationAccumulator[2] += az;
    }

    public void applyForce(float fx, float fy, float fz){
        // F = m * a
        // a = F / m
        this.applyAcceleration(fx * invMass, fy * invMass, fz * invMass);
    }

    /**
     * Applies the force in the relative position
     * The relative position is always the same point from the POV of the object
     * So if the object rotates, the relative position is also rotated along the object
     * */
    public void applyForce(float fx, float fy, float fz, float px, float py, float pz){
        this.applyForce(fx,fy,fz); // Overridable by more complex children
    }

    public void collideIfNecessary(PhysicsObject object){

    }

    public float maxCollisionRadius(){
        return 1F;
    }

    public float x(){return pos[0];}
    public float y(){return pos[1];}
    public float z(){return pos[2];}
    public void getPos(@Modified float[] dest){
        System.arraycopy(pos,0,dest,0,3);
    }

    public float vx(){return velocity[0];}
    public float vy(){return velocity[1];}
    public float vz(){return velocity[2];}
    public void getVelocity(@Modified float[] dest){
        System.arraycopy(velocity,0,dest,0,3);
    }

}
