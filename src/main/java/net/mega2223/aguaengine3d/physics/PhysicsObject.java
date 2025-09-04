package net.mega2223.aguaengine3d.physics;

public class PhysicsObject {

    protected final float[] coordinates = new float[4];
    protected final float[] velocity =  new float[4];

    private final float[] accelerationAccumulator = new float[4];

    float mass, invMass;

    public PhysicsObject(float mass){
        this.mass = mass; this.invMass = 1F/mass;
    }

    public void update(float deltaT){

    }

    public void translate(float x, float y, float z){
        coordinates[0] += x; coordinates[1] += y; coordinates[2] += z;
    }

    public void setCoordinates(float x, float y, float z){
        coordinates[0] = x; coordinates[1] = y; coordinates[2] = z;
    }

    public void applyVelocity(float vx, float vy, float vz){
        velocity[0] += vx; velocity[1] += vy; velocity[2] += vz;
    }

    public void applyAcceleration(float ax, float ay, float az){
        accelerationAccumulator[0] += ax; accelerationAccumulator[1] += ay; accelerationAccumulator[2] += az;
    }

    public void applyForce(float fx, float fy, float fz){
        // F = m * a
        // a = F / m
        applyAcceleration(fx * invMass, fy * invMass, fz * invMass);
    }

    /**
     * Applies the force in the relative position
     * The relative position is always the same point from the POV of the object
     * So if the object rotates, the relative position is also rotated along the object
     * */
    public void applyForce(float fx, float fy, float fz, float px, float py, float pz){
        this.applyForce(fx,fy,fz); // Overridable by more complex children
    }
}
