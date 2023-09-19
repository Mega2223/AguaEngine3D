package net.mega2223.aguaengine3d.physics.objects;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.PhysicsUtils;
import org.lwjgl.system.CallbackI;

import java.util.Arrays;

public class RigidBodySystem extends PhysicsSystem {

    protected final float[] orientation = new float[4]; //quaterinon that stores the orientation and it's axis
    protected final float[] rotation = new float[3];
    protected final float[] accumulatedTorque = new float[3];

    private static final float[] w = new float[4]; //stores the rotation quaternion to be used in update operations

    protected final float[] inverseInnertialTensor = new float[9];

    public RigidBodySystem(float mass, float[] innertialTensor) {
        super(mass);
        MatrixTranslator.getInverseMatrix3(innertialTensor,this.inverseInnertialTensor);
    }

    @Override
    public void doLogic(float time) {
        super.doLogic(time);
        w[0] = 0;
        w[1] = rotation[0];
        w[2] = rotation[1];
        w[3] = rotation[2];
        PhysicsUtils.multiplyQuaternions(w,rotation);
        for (int i = 0; i < 4; i++) {
            w[i]*=time*.5F;
            rotation[i]+=w[i];
        }

        for (int i = 0; i < 3; i++) {
            rotation[i] += accumulatedTorque[i];
        }
        VectorTranslator.debugVector(orientation);
        Arrays.fill(accumulatedTorque,0);
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

    public float[] getOrientation() {
        return orientation.clone();
    }
}
