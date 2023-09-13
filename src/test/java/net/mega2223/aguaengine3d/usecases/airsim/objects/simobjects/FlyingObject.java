package net.mega2223.aguaengine3d.usecases.airsim.objects.simobjects;

import net.mega2223.aguaengine3d.usecases.airsim.PhysicsUtils;
import net.mega2223.aguaengine3d.usecases.airsim.objects.SimObject;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

@SuppressWarnings("unused")

public abstract class FlyingObject extends SimObject {

    //consts
    public static final boolean GEAR_DOWN = true;
    public static final boolean GEAR_UP = false;

    //local variables for physics and stuff, should be calculated by the physics handlers and NOT changed externally
    private float speed = 0;
    private float[] rotation = new float[3];
    private boolean gearState = true;
    //local variables that differ for waach model
    protected final float weight, drag;
    protected final float engineStrenght, maxYawPush, maxPitch;
    protected final float gravity;


    //controller data that should be updatable by subclasses
    private float throttle = 0; // current propulsion generated by the engines
    private int flapState = 0; // self-explanatory
    private float yawControl = 0, pitchControl = 0;

    public FlyingObject(float x, float z, float weight, float altitude, float initialDirection) {
        super(x,z,altitude);
        this.weight = weight;
        this.engineStrenght = 1f;
        this.maxYawPush = .1f;
        this.maxPitch = .1f;
        this.drag = .6f;
        this.gravity = .8f;
        this.rotation[2] = initialDirection;
    }

    public FlyingObject(float x, float z, float weight, float altitude, float initialDirection, float drag, float engineStrenght, float maxYawPush, float pitchControlInfluence, float[] flapStateSpeeds, float gravity) {
        super(x,z,altitude);
        this.weight = weight;
        this.drag = drag;
        this.engineStrenght = engineStrenght;
        this.maxYawPush = maxYawPush;
        this.maxPitch = pitchControlInfluence;
        this.rotation[2] = initialDirection;
        this.gravity = gravity;
    }
    //handles all the physics logic
    public void update(){
        clampVariables();
        //speed and drag calculations
        speed += getThrottle() * engineStrenght;
        speed -= speed*drag;
        if(speed == Float.NEGATIVE_INFINITY || speed == Float.POSITIVE_INFINITY || speed < 0){
            speed = 0;
        }

        //direction calculations
        float yawInfluence = yawControl * maxYawPush * speed;
        if(location[1] <= 0){yawInfluence*=15;}
        rotation[1] += yawInfluence;

        //todo ground checking and rudder simulations
        System.out.println(rotation[0]);

        rotation[0] += pitchControl * maxPitch * speed;
        rotation[0] = 1;

        if(getY() == 0){
            if(rotation[2] < 0){rotation[2] = 0;}
            if(rotation[2] > Math.PI/2){rotation[2] = (float) Math.PI/2;}
        }

        float[] predictionVec = PhysicsUtils.generatePredictionVector(speed,rotation[0],rotation[1],rotation[2]);
        //predictionVec[1]-=gravity/10;
        VectorTranslator.addToVector(location,predictionVec);

        if(location[1] <= 0){location[1] = 0;}
    }

    private void clampVariables(){
        if(yawControl < -1){yawControl = -1;}
        if(yawControl > 1){yawControl = 1;}
        if(pitchControl < -1){pitchControl = -1;}
        if(pitchControl > 1){pitchControl = 1;}
        if(throttle < 0){throttle = 0;}
        if(throttle > 1){throttle = 1;}
        if(location[1] <= 0){location[1] = 0;}
    }

    public void addToThrottle(float v){
        setThrottle(getThrottle() + v);
        clampVariables();
    }

    public void setThrottle(float v){
        throttle = v;
        clampVariables();
    }

    public void addToPitchControl(float v){
        pitchControl += v;
        clampVariables();
    }

    public void setPitchControl(float v) {
        this.pitchControl = v;
        clampVariables();
    }

    public void addToYawControl(float v){
        yawControl += v;
        clampVariables();
    }

    public void setYawControl(float v) {
        this.yawControl = v;
        clampVariables();
    }

    public boolean getGearState() {
        return gearState;
    }

    public float getWeight() {
        return weight;
    }

    public float getHeadingRadians(){
        return rotation[2];
    }

    public float getSpeed() {
        return speed;
    }

    public float getPitch() {
        return rotation[1];
    }

    public float getThrottle() {
        return throttle;
    }

    public boolean isOnGround(){
        return getAltitude() <= 0; //todo
    }

    public float[] getRotation(){
        return rotation.clone();
    }

    public static void debugFlyingObject(FlyingObject obj){
        System.out.println("Coords: [" + obj.getX() + "," + obj.getY() + "," + obj.getZ() + "]");
        System.out.println("Rotation: [" + obj.getRotation()[0] + "," + obj.getRotation()[1] + "," + obj.getRotation()[2] + "]");

        System.out.println("Speed: [" + obj.getSpeed() + "], Direction: [" + obj.rotation[0] + "," + obj.rotation[1] + "," + obj.rotation[2] + "]");
        System.out.println("Controls: [yaw=" + obj.yawControl + ",pitch=" + obj.pitchControl + ",throttle="+ obj.throttle +"]");
    }

}
