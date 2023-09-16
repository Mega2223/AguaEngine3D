package net.mega2223.aguaengine3d.physics.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PhysicsSystem {

    protected final float[] coords = new float[4];
    protected final float[] velocity = new float[3];
    protected final float[] accumulatedForce = new float[3];
    protected final float inverseMass;

    List<PhysicsForce> forces = new ArrayList<>(10); //fixme not performant

    public PhysicsSystem(float mass) {
        this.inverseMass = 1/mass;
    }

    public void doLogic(float time, float drag){
        //assureVariablesAreOK(); not necessary as long as no forces generate a NaN value
        for(PhysicsForce act : forces){act.update(this,time);}
        for (int i = 0; i < 3; i++) {
            coords[i] += velocity[i]* time;
            velocity[i] += accumulatedForce[i];
        }
        Arrays.fill(accumulatedForce,0);
    }

    public float getInverseMass(){
        return inverseMass;
    }
    public float getMass(){
        return 1F/inverseMass;
    }

    public float[] getCoords() {
        return coords.clone();
    }

    public void applyForce(float[] force){
        for (int i = 0; i < accumulatedForce.length; i++) {
            accumulatedForce[i] += force[i];
        }
    }

    public void applyForce(float x, float y, float z){
        accumulatedForce[0]+=x;
        accumulatedForce[1]+=y;
        accumulatedForce[2]+=z;
    }

    public void addForce(PhysicsForce force){
        forces.add(force);
    }

    public float getVelocityX(){
        return velocity[0];
    }

    public float getVelocityY(){
        return velocity[1];
    }

    public float getVelocityZ(){
        return velocity[2];
    }

    public float getCoordX(){
        return coords[0];
    }

    public float getCoordY(){
        return coords[1];
    }

    public float getCoordZ(){
        return coords[2];
    }

    public void setCoordX(float c){
        coords[0] = c;
    }

    public void setCoordY(float c){
        coords[1] = c;
    }

    public void setCoordZ(float c){
        coords[2] = c;
    }

    public void setCoords(float x, float y, float z){
        coords[0] = x;
        coords[1] = y;
        coords[2] = z;
    }

    protected void assureVariablesAreOK(){
        for (int i = 0; i < coords.length; i++) {
            coords[i] = getOkVersion(coords[i]);
        }
        for (int i = 0; i < velocity.length; i++) {
            velocity[i] = getOkVersion(velocity[i]);
        }
        for (int i = 0; i < accumulatedForce.length; i++) {
            accumulatedForce[i] = getOkVersion(accumulatedForce[i]);
        }
    }

    static float getOkVersion(float f){
        return Float.isNaN(f) || Float.isInfinite(f) ? 0 : f;
    }

    /*void addConstantForce(float[] force){
        for (int i = 0; i < constantAcceleration.length; i++) {
            constantAcceleration[i]+=force[i];
        }
    }
    void setConstantAcceleration(float[] accel){
        System.arraycopy(accel, 0, constantAcceleration, 0, constantAcceleration.length);
    }
    void resetConstantAcceleration(){
        Arrays.fill(constantAcceleration,0);
    }*/

}
