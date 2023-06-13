package net.mega2223.aguaengine3d.usecases.airsim.objects;

public abstract class SimObject {

    protected SimObject(float x, float z, float altitude) {

        this.location = new float[]{x,altitude,z};
        this.rotation = new float[]{0,0,0};
    }

    public abstract void update();
    //model data

    //plane data

    protected float[] location;//x z y, since the last coord represents height
    protected float[] rotation;//rX, ry, rZ
    //in order to ensure compatibility, yaw is stored on the first index
    //and pitch is stored on the third
    //(because it affects the proximity to the camera)

    //encapsulation stuff

    public double getAltitude() {
        return location[1];
    }

    public float getX(){
        return location[0];
    }
    public float getY(){
        return location[1];
    }
    public float getZ(){
        return location[2];
    }

    public float[] getRotation() {return rotation.clone();}
}
