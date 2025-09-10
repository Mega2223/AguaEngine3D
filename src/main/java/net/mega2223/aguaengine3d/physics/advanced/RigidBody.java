package net.mega2223.aguaengine3d.physics.advanced;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.QuaternionTranslator;

import java.util.Arrays;

public class RigidBody implements Rotatable {

    private final static float[][] buffers = new float[2][4];

    protected final float[] pos = new float[3];
    protected final float[] velocity = new float[3];

    protected final float[] rotationQ4 = {1F,0F,0F,0F};
    protected final float[] angularVelocity = new float[4];

    protected float mass, invMass;
    protected float[] inertialTensor = new float[16], inverseInertialTensor =  new float[16];

    private final float[] accelerationAccumulator = new float[4];
    public final float[] angularAccelAccumulator = new float[4]; //TODO
    private final float[] posDerivative = new float[4];
    protected final float[] rotationMatrix = new float[16];
    protected final float[] inverseRotationMatrix = new float[16];
    protected final float[] inverseInertialTensorWorldCoords = new float[16];

    public RigidBody(float mass) {
        this.mass = mass;
        this.invMass = 1F/mass;
        MatrixTranslator.generateIdentity(inertialTensor);
        MatrixTranslator.generateIdentity(inverseInertialTensor);
    }

    public RigidBody(float mass, float[] inertialTensor){
        this.mass = mass;
        this.invMass = 1F/mass;
        this.setInertialTensor(inertialTensor);
    }

    public void update(float deltaT){
        // Calculations
        MatrixTranslator.multiply4x4Matrices(inverseInertialTensor,rotationMatrix,inverseInertialTensorWorldCoords);
        // TODO essa é a ordem certa?
        // TODO precisa fazer isso?

        // Linear Velocity
        VectorTranslator.addToVector(velocity,accelerationAccumulator);
        Arrays.fill(accelerationAccumulator,0);
        VectorTranslator.scaleVector(velocity,deltaT,posDerivative);
        VectorTranslator.addToVector(pos, posDerivative);

        //Angular Velocity
        VectorTranslator.addToVector(angularVelocity,angularAccelAccumulator);
        Arrays.fill(angularAccelAccumulator,0);
        QuaternionTranslator.addAngularVelocity(rotationQ4, angularVelocity,deltaT,buffers[0]);
        QuaternionTranslator.copy(buffers[0],rotationQ4);

        QuaternionTranslator.rotationMatrixFromQuaternion(rotationQ4,rotationMatrix);
        MatrixTranslator.transposeMat4(rotationMatrix,inverseRotationMatrix);
        // The inverse of a rotation matrix is it's transpose, much quicker to calculate :)
    }

    @Override
    public void setCoordinates(float x, float y, float z){
        pos[0] = x; pos[1] = y; pos[2] = z;
    }

    @Override
    public void setVelocity(float vx, float vy, float vz){
        velocity[0] = vx; velocity[1] = vy; velocity[2] = vz;
    }

    @Override
    public void applyAcceleration(float ax, float ay, float az) {
        accelerationAccumulator[0] += ax;
        accelerationAccumulator[1] += ay;
        accelerationAccumulator[2] += az;
    }

    @Override
    public void applyVelocity(float vx, float vy, float vz){
        velocity[0] += vx; velocity[1] += vy; velocity[2] += vz;
    }

    @Override
    public void applyTranslation(float x, float y, float z) {
        pos[0] += x; pos[1] += y; pos[2] += z;
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

    public void toLocalCoordinateSystem(@Modified float[] vec3) {
        VectorTranslator.subtractFromVector(vec3,pos);
    }

    public void toGlobalCoordinateSystem(@Modified float[] vec3) {
        VectorTranslator.addToVector(vec3,pos);
    }

    private final float[] pBuffer = new float[4];
    private final float[] fBuffer = new float[4];
    @Override
    public void applyForce(float fx, float fy, float fz, float px, float py, float pz) {
        VectorTranslator.copy(px,py,pz,pBuffer);
        VectorTranslator.copy(fx,fy,fz,fBuffer);
        toLocalCoordinateSystem(pBuffer); // TODO presumindo que este seja nosso centro de massa (p.197)
        VectorTranslator.crossProduct(pBuffer,fBuffer);
        applyTorque(pBuffer);
        applyForce(fBuffer);
    }

    @Override
    public void applyTorque(float tx, float ty, float tz) {
        VectorTranslator.copy(tx,ty,tz,torqueBuffer);
        MatrixTranslator.multiplyVectorMatrix(torqueBuffer,inverseInertialTensor);
        VectorTranslator.addToVector(angularAccelAccumulator,torqueBuffer);
    }
    float[] torqueBuffer = new float[4];

    @Override
    public void toLocalVelocity(float[] point, float[] pointVelocity, float[] dest) {
        dest[0] = pointVelocity[0] - velocity[0];
        dest[1] = pointVelocity[1] - velocity[1];
        dest[2] = pointVelocity[2] - velocity[2];
    }

    public float getMass() {
        return mass;
    }

    public float getInverseMass() {
        return invMass;
    }

    @Override
    public void toGlobalVelocity(float[] point, float[] pointVelocity, float[] dest) {

    }

    @Override
    public void getRotationMatrix(float[] m4) {
        MatrixTranslator.copy(rotationMatrix,m4);
    }

    @Override
    public void getRotationQuaternion(float[] q4) {
        QuaternionTranslator.copy(rotationQ4,q4);
    }

    @Override
    public void getLocalPointVelocity(float[] point, @Modified float[] dest) {
        // point e dest estão em world coordinates
        VectorTranslator.subtractFromVector(point,pos, pointVelBuffer);
        VectorTranslator.crossProduct(angularVelocity, pointVelBuffer,dest);
        VectorTranslator.addToVector(dest,velocity); // TODO isso funciona?
    }

    public void setInertialTensor(float[] inertialTensor){
        MatrixTranslator.copy(inertialTensor,this.inertialTensor);
        MatrixTranslator.getInverseMatrix4(inertialTensor,this.inverseInertialTensor);
    }

    public void setInverseInertialTensor(float[] inverseInertialTensor){
        MatrixTranslator.copy(inverseInertialTensor,this.inverseInertialTensor);
        MatrixTranslator.getInverseMatrix4(inverseInertialTensor,this.inertialTensor);
    }

    private final float[] pointVelBuffer = new float[4];
}
