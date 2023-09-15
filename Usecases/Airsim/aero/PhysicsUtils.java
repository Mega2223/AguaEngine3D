package net.mega2223.aguaengine3d.usecases.aero;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;

public class PhysicsUtils {
    private PhysicsUtils(){}

    public static float[] generatePredictionVector(float magnitude, float rx, float ry, float rz){
        float[] ret = {0,0,1,0};
        MatrixTranslator.rotateVector3(ret,rx,ry,rz);
        for (int i = 0; i < ret.length; i++) {ret[i]*=magnitude;}
        return ret;
    }

}
