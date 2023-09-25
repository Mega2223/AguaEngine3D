package net.mega2223.aguaengine3d.physics.objects;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.PhysicsManager;
import net.mega2223.aguaengine3d.physics.PhysicsUtils;

import java.util.Arrays;

public class RigidBodySystem extends PhysicsSystem {

    protected final float[] orientation = new float[4]; //quaterinon that stores the orientation and it's axis
    protected final float[] spin = new float[3];
    protected final float[] accumulatedTorque = new float[3];
    protected final float[] inverseInnertialTensor = new float[9];

    public RigidBodySystem(float mass, float[] innertialTensor) {
        super(mass);
        spin[0] = 3;

        MatrixTranslator.getInverseMatrix3(innertialTensor,this.inverseInnertialTensor);
    }

    //variables below are meant to be buffer for certain operations rather than object specific information
    private final float[] rw = new float[4]; //stores the rotation quaternion to be used in update operations
    private final float[] rotationMatrix = new float[16];//stores the translation matrix multiplied by the rotation matrix
    private final float[] rotationRadians = new float[3];//to be updated each object update, dependent on the quaternion rotation representation
    private static final float[] bufferM4 = new float[16];

    @Override
    public void doLogic(float time) {
        super.doLogic(time);
        VectorTranslator.normalize(orientation);
        VectorTranslator.getRotationRadians(orientation[0],orientation[1],orientation[2],orientation[3],rotationRadians);

        for (int i = 0; i < 3; i++) {
            spin[i] += accumulatedTorque[i];
        }

        rw[0] = 0;
        rw[1] = spin[0];
        rw[2] = spin[1];
        rw[3] = spin[2];
        VectorTranslator.debugVector(orientation);
        PhysicsManager.addScaledQuaternions(orientation,rw,0.0001F);

        //VectorTranslator.debugVector(orientation);
        Arrays.fill(accumulatedTorque,0);

        VectorTranslator.getRotationRadians(orientation[0],orientation[1],orientation[2],orientation[3], rotationRadians);
        MatrixTranslator.generateRotationMatrix(rotationMatrix, rotationRadians[0], rotationRadians[1], rotationRadians[2]);
    }

    public void setOrientation(float w, float x, float y, float z){
        orientation[0] = w;
        orientation[1] = x;
        orientation[2] = y;
        orientation[3] = z;
        VectorTranslator.normalize(orientation);
    }

    public void applyTorque(float x, float y, float z){
        accumulatedTorque[0]+=x;
        accumulatedTorque[1]+=y;
        accumulatedTorque[2]+=z;
    }

    //forces that do not have any specific point will be treated as appiled to the center of mass
    //therefore there's no need to override the method
    public void applyForce(float fx, float fy, float fz, float px, float py, float pz) {
        
    }

    public void getWorldspacePos(float rx, float ry, float rz, float[] dest){
        dest[0] = rx;
        dest[1] = ry;
        dest[2] = rz;
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

    @Override
    public void toLocalCoords(float[] worldspaceCoords) {
        super.toLocalCoords(worldspaceCoords);
        MatrixTranslator.multiplyVec4Mat4(worldspaceCoords,rotationMatrix);
    }


    public float[] getOrientation() {
        return orientation.clone();
    }
}
