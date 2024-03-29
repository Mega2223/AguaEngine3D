package net.mega2223.aguaengine3d.physics.objects;

import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PhysicsSystem {

    protected final float[] coords = new float[4];
    protected final float[] velocity = new float[3];
    protected final float[] accumulatedAcceleration = new float[3];
    protected final float[] accumulatedTranslations = new float[3];
    protected final float inverseMass;

    List<PhysicsForce> forces = new ArrayList<>(10); //fixme not performant

    Hitbox boundHitbox = null;

    public PhysicsSystem(float mass) {
        this(mass,0,0,0);
    }
    public PhysicsSystem(float mass, float x, float y, float z){
        this(mass,x,y,z,0,0,0);
    }
    public PhysicsSystem(float mass, float x, float y, float z, float sX, float sY, float sZ){
        this.inverseMass = 1/mass;
        coords[0] = x; coords[1] = y; coords[2] = z;
    }

    public void doLogic(float time){
        //assureVariablesAreOK(); not necessary as long as no forces generate a NaN value
        for(PhysicsForce act : forces){act.update(this,time);}
        for (int i = 0; i < 3; i++) {
            coords[i]+=accumulatedTranslations[i];
            coords[i] += velocity[i]* time;
            velocity[i] += accumulatedAcceleration[i];
        }
        Arrays.fill(accumulatedAcceleration,0);
        Arrays.fill(accumulatedTranslations,0);
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

    public void applyAcceleration(float[] force){
        for (int i = 0; i < accumulatedAcceleration.length; i++) {
            accumulatedAcceleration[i] += force[i];
        }
    }
    /** Applies a direct transform on the acceleration variable regradless of object mass*/
    public void applyAcceleration(float x, float y, float z){
        accumulatedAcceleration[0]+=x;
        accumulatedAcceleration[1]+=y;
        accumulatedAcceleration[2]+=z;
    }

    public void applyForce(float[] force){
        for (int i = 0; i < accumulatedAcceleration.length; i++) {
            accumulatedAcceleration[i] += force[i] * inverseMass;
        }
    }

    /**Applies force, taking object mass into account*/
    public void applyForce(float x, float y, float z){
        accumulatedAcceleration[0]+=x*inverseMass;
        accumulatedAcceleration[1]+=y*inverseMass;
        accumulatedAcceleration[2]+=z*inverseMass;
    }

    /**Applies a direct change to the object's speed regardless of mass*/
    public void applyImpulse(float x, float y, float z){
        velocity[0]+=x;
        velocity[1]+=y;
        velocity[2]+=z;
    }

    public void applyImpulse(float[] impulse){
        for (int i = 0; i < velocity.length; i++) {
            velocity[i] += impulse[i];
        }
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

    public void applyTransformationX(float amount){
        accumulatedTranslations[0] += amount;
    }

    public void applyTransformationY(float amount){
        accumulatedTranslations[1] += amount;
    }

    public void applyTransformationZ(float amount){
        accumulatedTranslations[2] += amount;
    }
    public void applyTransformation(float[] amount){
        accumulatedTranslations[0] += amount[0];
        accumulatedTranslations[1] += amount[1];
        accumulatedTranslations[2] += amount[2];
    }
    public void applyTransformation(float x, float y, float z){
        accumulatedTranslations[0] += x;
        accumulatedTranslations[1] += y;
        accumulatedTranslations[2] += z;
    }

    private void assureVariablesAreOK(){
        for (int i = 0; i < coords.length; i++) {
            coords[i] = getOkVersion(coords[i]);
        }
        for (int i = 0; i < velocity.length; i++) {
            velocity[i] = getOkVersion(velocity[i]);
        }
        for (int i = 0; i < accumulatedAcceleration.length; i++) {
            accumulatedAcceleration[i] = getOkVersion(accumulatedAcceleration[i]);
        }
    }

    static float getOkVersion(float f){
        return Float.isNaN(f) || Float.isInfinite(f) ? 0 : f;
    }

    public void toLocalCoords(float[] worldspaceCoords){
        for (int i = 0; i < 3; i++) {worldspaceCoords[i]-=coords[i];}
    }

    public void toWorldspaceCoords(float[] localCoords){
        for (int i = 0; i < 3; i++) {localCoords[i]+=coords[i];}
    }

    public Hitbox getBoundHitbox() {
        return boundHitbox;
    }
    /**Can only be done once*/
    public boolean bindHitbox(Hitbox boundHitbox) {
        if(this.boundHitbox == null){this.boundHitbox = boundHitbox; return true;} return false;
    }

    public abstract float getInteractionRadius();

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
