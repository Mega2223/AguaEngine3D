package net.mega2223.aguaengine3d.physics.objects;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.PhysicsManager;
import net.mega2223.aguaengine3d.physics.PhysicsUtils;

import java.util.Arrays;

public class RigidBodySystem extends PhysicsSystem {

    protected static final float ANGULAR_MOMENTUM_SCALE_FACTOR = .1F;
    protected final float[] orientation = {1,0,0,0}; //quaterinon that stores the orientation and it's axis
    protected final float[] spin = new float[3];
    protected final float[] accumulatedTorque = new float[3];
    protected final float[] orientationTransforms = new float[3];
    protected final float[] inverseInertialTensor = new float[9];

    public RigidBodySystem(float mass, float[] innertialTensor) {
        super(mass);
        MatrixTranslator.getInverseMatrix3(innertialTensor,this.inverseInertialTensor);
    }

    //variables below are meant to be buffer for certain operations rather than object specific information
    private final float[] rw = new float[4]; //stores the rotation quaternion to be used in update operations
    private final float[] rotationMatrix = new float[16];//stores the translation matrix multiplied by the rotation matrix
    private final float[] rotationRadians = new float[3];//to be updated each object update, dependent on the quaternion rotation representation
    private final float[] inverseInnertialTensorWorldspace = new float[9];//stores the inverse inertia tensor in worldspace coords, updates each iteration
    //static variables meant to be used for physics calculations
    private static final float[] bufferM4 = new float[16];
    private static final float[] bufferVec = new float[4];

    @Override
    public void doLogic(float time) {
        super.doLogic(time);
        VectorTranslator.getRotationRadians(orientation[0],orientation[1],orientation[2],orientation[3],rotationRadians);

        for (int i = 0; i < 3; i++) {
            spin[i] += accumulatedTorque[i];
        }

        rw[0] = 0;
        rw[1] = spin[0] * time + orientationTransforms[0];
        rw[2] = spin[1] * time + orientationTransforms[1];
        rw[3] = spin[2] * time + orientationTransforms[2];
        //VectorTranslator.debugVector(rw); 
        PhysicsManager.addScaledQuaternions(orientation,rw, .1F); //todo should this really be .1F? INVESTIGATE NOW !!!!!!!!

        //VectorTranslator.debugVector(orientation);
        Arrays.fill(accumulatedTorque,0);
        Arrays.fill(orientationTransforms,0);
        VectorTranslator.getRotationRadians(orientation[0],orientation[1],orientation[2],orientation[3], rotationRadians);
        MatrixTranslator.getRotationMat4FromQuaternion(orienW(),orienX(),orienY(),orienZ(),rotationMatrix);
        PhysicsUtils.rotateInertiaTensor(inverseInertialTensor,rotationMatrix,inverseInnertialTensorWorldspace);
        for (int i = 0; i < 3; i++) {
            coords[i] = Float.isNaN(coords[i]) ? 0 : coords[i];
        }
        VectorTranslator.normalize(orientation);
        if(VectorTranslator.getMagnitude(orientation) == 0){orientation[0] = 1;}
    }

    public void setOrientation(float w, float x, float y, float z){
        orientation[0] = w;
        orientation[1] = x;
        orientation[2] = y;
        orientation[3] = z;
        VectorTranslator.normalize(orientation);
    }
    public void applyOrientationTransform(float x, float y, float z){
        orientationTransforms[0]+=x;
        orientationTransforms[1]+=y;
        orientationTransforms[2]+=z;
    }

    //forces that do not have any specific point will be treated as appiled to the center of mass
    //therefore there's no need to override the original superclass method

    public void applyForce(float fx, float fy, float fz, float px, float py, float pz) {
        VectorTranslator.getCrossProduct(bufferVec,fx,fy,fz,px,py,pz);
        applyTorque(bufferVec[0], bufferVec[1], bufferVec[2]);
        applyForce(Math.max(fx-px,0),Math.max(fy-py,0),Math.max(fz-pz,0));
    }

//    public void applyRotationalTransformation(float tx, float ty, float tz, float px, float py, float pz) {
//        VectorTranslator.getCrossProduct(bufferVec,tx,ty,tz,px,py,pz);
//        applyOrientationTransform(bufferVec[0], bufferVec[1], bufferVec[2]);
//        applyTransformation(Math.max(tx-px,0),Math.max(ty-py,0),Math.max(tz-pz,0));
//    }

    /**Applies impulse with rotation component*/
    public void applyImpulse(float ix, float iy, float iz, float px, float py, float pz) {
        bufferVec[0] = px; bufferVec[1] = py; bufferVec[2] = pz;
        applyImpulse(ix- bufferVec[0],iy- bufferVec[1],iz- bufferVec[2]);
        VectorTranslator.getCrossProduct(bufferVec,ix,iy,iz,px,py,pz);
        //No idea why it's not I x T but it works so
        applySpin(bufferVec);
    }

    public void applyTorque(float[] torque){
        MatrixTranslator.multiplyVec3Mat3(torque,inverseInnertialTensorWorldspace);
        //todo should I put the multiplication in the doLogic function?
        accumulatedTorque[0] += torque[0];
        accumulatedTorque[1] += torque[1];
        accumulatedTorque[2] += torque[2];
    }

    public void applyTorque(float x, float y, float z){
        bufferVec[0] = x; bufferVec[1] = y; bufferVec[2] = z;
        applyTorque(bufferVec);
    }

    /** Applies immediate change to angular velocity regardless of Inertia tensor */
    public void applySpin(float x, float y, float z){
        spin[0] += x;
        spin[1] += y;
        spin[2] += z;
    }

    public void applySpin(float[] spin){
        applySpin(spin[0],spin[1],spin[2]);
    }

    void getInverseInertiaTensorTranslated(float[] dest){
        PhysicsUtils.rotateInertiaTensor(inverseInertialTensor,rotationMatrix,dest);
    }

    public float orienX(){
        return orientation[1];
    }
    public float orienY(){
        return orientation[2];
    }
    public float orienZ(){
        return orientation[3];
    }
    public float orienW(){return orientation[0];}

    public float getSpinX(){return spin[0];}
    public float getSpinY(){return spin[1];}
    public float getSpinZ(){return spin[2];}

    @Override
    public void toWorldspaceCoords(float[] worldspaceCoords) {
        //the inverse of a rotation matrix is it's transpose
        MatrixTranslator.getTransposeMatrix4(rotationMatrix,bufferM4);
        MatrixTranslator.multiplyVec4Mat4(worldspaceCoords,bufferM4);
        super.toWorldspaceCoords(worldspaceCoords);
    }

    @Override
    public void toLocalCoords(float[] localCoords) {
        super.toLocalCoords(localCoords);
        MatrixTranslator.multiplyVec4Mat4(localCoords,bufferM4);
    }

    public float[] getOrientation() {//todo maybe switch to a void and a array argument?
        return orientation.clone();
    }

    @Override
    public float getInteractionRadius() {
        return boundHitbox == null ? 0 : boundHitbox.getEffectiveInteractionRadius();
    }

    public void getRotationMatrix(float[] dest){
        System.arraycopy(rotationMatrix,0,dest,0,16);
    }

    public void getInverseInertiaTensor(float[] dest){
        System.arraycopy(inverseInertialTensor,0,dest,0,dest.length);
    }

    public void toLocalRotation(float[] vec){
        MatrixTranslator.multiplyVec4Mat4(vec,rotationMatrix);
    }

    public void toGlobalRotation(float[] vec){
        MatrixTranslator.multiplyVec4Mat4(vec,rotationMatrix);
    }

    public void getWorldspacePointVelocity(float px, float py, float pz, float[] dest){
        dest[0] = px; dest[1] = py; dest[2] = pz;
        MatrixTranslator.multiplyVec4Mat4(dest,rotationMatrix);
        VectorTranslator.getCrossProduct(dest,spin,dest);
        for (int i = 0; i < 3; i++) {
            dest[i] += velocity[i];
        }
    }

    @Override
    public String toString() {
        return "RigidBodySystem{" +
                "orientation=" + Arrays.toString(orientation) +
                ", spin=" + Arrays.toString(spin) +
                ", rotationRadians=" + Arrays.toString(rotationRadians) +
                ", position= " + Arrays.toString(this.coords) +
                '}';
    }
}
