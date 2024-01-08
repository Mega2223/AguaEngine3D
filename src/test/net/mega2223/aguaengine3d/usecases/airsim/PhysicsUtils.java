package net.mega2223.aguaengine3d.usecases.airsim;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

public class PhysicsUtils {
    private PhysicsUtils(){}

    public static float[] generatePredictionVector(float speed, float rX, float rY, float rZ){
        float[] transVec = {0,0,1};
        MatrixTranslator.rotateVector3(transVec,rX,rY,rZ);
        VectorTranslator.scaleVec3(transVec,speed,0);
        return transVec;
    }

    public static void predictNextStep(float[] vector, float speed, float rX, float rY, float rZ){
        float[] transVec = {0,1,0};
        MatrixTranslator.rotateVector3(transVec,rX,rY,rZ);
        VectorTranslator.scaleVec3(transVec,speed,0);
        for (int i = 0; i < vector.length; i++) {
            vector[i] += transVec[i];
        }
    }

    public static float calculateDistanceVec3 (float[] vec1, float[] vec2){
        float ret = 0;
        for (int i = 0; i < vec1.length; i++) {
            float sqr = vec2[i] - vec1[i];
            sqr *= sqr;
            ret += sqr;
        }
        return (float) Math.sqrt(ret);
    }

}
