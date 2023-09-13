package net.mega2223.aguaengine3d.usecases.Airsim;

import net.mega2223.aguaengine3d.mathematics.MathUtils;
import net.mega2223.aguaengine3d.usecases.Airsim.objects.simobjects.FlyingSimObject;

public class Aerodynamics {


    public static float[] generatePredictionVector(float[] currentRotation,float attackAngle,float velocity) {
        return generatePredictionVector(currentRotation, attackAngle, velocity,1);
    }
    public static float[] generatePredictionVector(float[] currentRotation,float attackAngle,float velocity,float attackAngleDecay){

        float pitch = currentRotation[FlyingSimObject.PITCH_LOC];
        float dif = (MathUtils.angleDifference(pitch,attackAngle))*attackAngleDecay;
        if(dif < 0){dif = -dif;}
        dif = (float) (dif/Math.PI/2);
        System.out.println(dif);

        float[] ret = PhysicsUtils.generatePredictionVector(velocity,currentRotation[0],currentRotation[1],currentRotation[2]);
        ret[1]-=dif;

        return ret;
    }

    public static float clampRotation(float r){
        while (r>=Math.PI*2){r-=Math.PI*2;}
        while (r<0){r+=Math.PI*2;}
        return r;
    }

}
