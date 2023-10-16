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
    protected final float[] accumulatedAngularTransformations = new float[3];
    protected final float[] inverseInnertialTensor = new float[9];

    public RigidBodySystem(float mass, float[] innertialTensor) {
        super(mass);
        MatrixTranslator.getInverseMatrix3(innertialTensor,this.inverseInnertialTensor);
    }

    //variables below are meant to be buffer for certain operations rather than object specific information
    private final float[] rw = new float[4]; //stores the rotation quaternion to be used in update operations
    private final float[] rotationMatrix = new float[16];//stores the translation matrix multiplied by the rotation matrix
    private final float[] rotationRadians = new float[3];//to be updated each object update, dependent on the quaternion rotation representation

    //static variables meant to be used for physics calculations
    private static final float[] bufferM4 = new float[16];
    private static final float[] bufferVec = new float[4];
    @Override
    public void doLogic(float time) {
        super.doLogic(time);
        VectorTranslator.normalize(orientation);
        VectorTranslator.getRotationRadians(orientation[0],orientation[1],orientation[2],orientation[3],rotationRadians);

        for (int i = 0; i < 3; i++) {
            spin[i] += accumulatedAngularTransformations[i];
            spin[i] += accumulatedTorque[i];
        }

        rw[0] = 0;
        rw[1] = spin[0];
        rw[2] = spin[1];
        rw[3] = spin[2];
        //VectorTranslator.debugVector(rw);
        PhysicsManager.addScaledQuaternions(orientation,rw, .1F);

        //VectorTranslator.debugVector(orientation);
        Arrays.fill(accumulatedTorque,0);
        VectorTranslator.getRotationRadians(orientation[0],orientation[1],orientation[2],orientation[3], rotationRadians);
        MatrixTranslator.getRotationMat4FromQuaternion(orienW(),orienX(),orienY(),orienZ(),rotationMatrix);

        for (int i = 0; i < 3; i++) {
            coords[i] = Float.isNaN(coords[i]) ? 0 : coords[i];
        }
        Arrays.fill(accumulatedAngularTransformations,0);
    }

    public void setOrientation(float w, float x, float y, float z){
        orientation[0] = w;
        orientation[1] = x;
        orientation[2] = y;
        orientation[3] = z;
        VectorTranslator.normalize(orientation);
    }
    public void applyOrientationTransform(float x, float y, float z){
        accumulatedAngularTransformations[0]+=x;
        accumulatedAngularTransformations[1]+=y;
        accumulatedAngularTransformations[2]+=z;
    }

    public void applyTorque(float x, float y, float z){
        accumulatedTorque[0]+=x;
        accumulatedTorque[1]+=y;
        accumulatedTorque[2]+=z;
    }

    public void applySpin(float x, float y, float z){
        spin[0] += x;
        spin[1] += y;
        spin[2] += z;
    }

    public void applyTorque(float[] torque){
        applyTorque(torque[0],torque[1],torque[2]);
    }

    public void applySpin(float[] spin){
        applySpin(spin[0],spin[1],spin[2]);
    }

    //forces that do not have any specific point will be treated as appiled to the center of mass
    //therefore there's no need to override the method

    public void applyForce(float fx, float fy, float fz, float px, float py, float pz){
        applyForce(fx,fy,fz,px,py,pz,true);
    }

    public void applyForce(float fx, float fy, float fz, float px, float py, float pz, boolean relative) { //FIXME
        bufferVec[0] = px; bufferVec[1] = py; bufferVec[2] = pz;
        if(!relative){
            toLocalCoords(bufferVec);
        }
        //applyForce(fx*bufferVec3[0],fy*bufferVec3[1],fz*bufferVec3[2]);
        VectorTranslator.debugVector("ROT:",bufferVec);
        VectorTranslator.getCrossProduct(bufferVec,fx,fy,fz,bufferVec[0],bufferVec[1],bufferVec[2]);
        //System.out.println("FX: " + fx + " FY: " + fy + "FZ: " + fz + " PX: " + px + "PY: " + px + " PZ: " + pz);
        VectorTranslator.debugVector("ROT:",bufferVec);
        applyOrientationTransform(bufferVec[0], bufferVec[1], bufferVec[2]);
    }

    public void applyImpulse(float ix, float iy, float iz, float px, float py, float pz, boolean relative) {
        bufferVec[0] = px; bufferVec[1] = py; bufferVec[2] = pz;
        if(!relative){toLocalCoords(bufferVec);}
        applyImpulse(ix- bufferVec[0],iy- bufferVec[1],iz- bufferVec[2]);
        VectorTranslator.getCrossProduct(bufferVec,ix,iy,iz,px,py,pz);
        applySpin(bufferVec);
    }

    void getInverseInertiaTensorTranslated(float[] dest){
        PhysicsUtils.transformInertiaTensorQuaternion(inverseInnertialTensor,rotationMatrix,dest);
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
    public void toLocalCoords(float[] worldspaceCoords) {
        super.toLocalCoords(worldspaceCoords);
        MatrixTranslator.multiplyVec4Mat4(worldspaceCoords,rotationMatrix);
    }

    @Override
    public void toWorldspaceCoords(float[] localCoords) {
        //the inverse of a rotation matrix is it's transpose
        MatrixTranslator.getTransposeMatrix4(rotationMatrix,bufferM4);
        MatrixTranslator.multiplyVec4Mat4(localCoords,bufferM4);
        super.toWorldspaceCoords(localCoords);
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
