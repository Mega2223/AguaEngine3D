package net.mega2223.aguaengine3d.physics.objects;

import org.lwjgl.system.CallbackI;

public abstract class PhysicsSystem {

    public static final float DAMPLING = .99F;

    protected final float[] coords = new float[4];
    protected final float[] velocity = new float[3];
    protected final float[] acceleration = new float[3];
    protected final float inverseMass;

    public PhysicsSystem(float mass) {
        this.inverseMass = 1/mass;
    }

    public void doLogic(float time, float drag, float[] gravity){
        //double pow = Math.pow(DAMPLING, time);
        for (int i = 0; i < 3; i++) {
            coords[i] += velocity[i]* time;// + (acceleration[i] * time * time * .5F);
            //velocity[i] = velocity[i] * pow + acceleration[i*time;
            velocity[i] *= DAMPLING;
            velocity[i] += acceleration[i]*time;
        }
    }

    public float getInverseMass(){
        return inverseMass;
    }
    public float getMass(){
        return 1/inverseMass;
    }

    public void applyAccel(float[] accel){
        System.arraycopy(accel, 0, acceleration, 0, acceleration.length);
    }

    public float[] getCoords() {
        return coords.clone();
    }
}
