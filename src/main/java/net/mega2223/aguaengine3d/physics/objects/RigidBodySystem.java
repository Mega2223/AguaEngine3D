package net.mega2223.aguaengine3d.physics.objects;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;

import java.util.Arrays;

public class RigidBodySystem extends PhysicsSystem {

    protected final float[] orientation = new float[4];
    protected final float[] rotation = new float[3];
    protected final float[] accumulatedAngularTorque = new float[3];

    protected final float[] inverseInnertialTensor = new float[9];

    public RigidBodySystem(float mass, float[] innertialTensor) {
        super(mass);
        MatrixTranslator.getInverseMatrix3(innertialTensor,this.inverseInnertialTensor);
        //System.arraycopy(innertialTensor,0,this.inverseInnertialTensor,0,this.inverseInnertialTensor.length);
    }

    @Override
    public void doLogic(float time) {
        super.doLogic(time);
        for (int i = 0; i < 3; i++) {
            orientation[i]+=rotation[i]*time;
            rotation[i] += accumulatedAngularTorque[i];
        }
        Arrays.fill(accumulatedAngularTorque,0);
    }

    public void setAngle(float w, float x, float y, float z, float angle){
        orientation[0] = (float) (Math.cos(angle)/2);
        orientation[1] = (float) (x*Math.sin(angle)/2);
        orientation[2] = (float) (y*Math.sin(angle)/2);
        orientation[3] = (float) (z*Math.sin(angle)/2);
    }

    public void applyTorque(float x, float y, float z){
        accumulatedAngularTorque[0]+=x;
        accumulatedAngularTorque[1]+=y;
        accumulatedAngularTorque[2]+=z;
    }

    public float dirX(){
        return orientation[0];
    }
    public float dirY(){
        return orientation[1];
    }
    public float dirZ(){
        return orientation[2];
    }
}
