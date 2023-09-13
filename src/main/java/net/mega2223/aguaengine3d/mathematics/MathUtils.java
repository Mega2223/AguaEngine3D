package net.mega2223.aguaengine3d.mathematics;

import java.util.List;
import java.util.Random;

public class MathUtils {
    public static Object doWeightedSelection(List objectList, float[] weights){
        return doWeightedSelection(objectList.toArray(),weights);
    }
    public static Object doWeightedSelection(Object[] objects, float[] weights){
        if(objects.length!= weights.length){throw new UnsupportedOperationException("Objects or weights not set up correctly");}
        if(objects.length == 1){return objects[0];}
        float totalWeightLenght = 0;
        for(float act : weights){totalWeightLenght+=act;}
        float randomValue = new Random().nextFloat() * totalWeightLenght;
        float currentPos = 0;
        for(int i = 0; i < objects.length; i++){
            if(randomValue >= currentPos && randomValue < currentPos+weights[i]){return objects[i];}
            currentPos+=weights[i];
        }
        return null;
    }

    public static float[][] cloneFloatArrayArray(float[][] base){
        float[][] ret = new float[base.length][];
        for(int i = 0; i < base.length; i++){
            ret[i] = base[i].clone();
        }
        return ret;
    }
    public static int[][] cloneIntArrayArray(int[][] base){
        int[][] ret = new int[base.length][];
        for(int i = 0; i < base.length; i++){
            ret[i] = base[i].clone();
        }
        return ret;
    }

    public static void predictNextPos(float[] initialVector, float vectorSpeed, float directionRadians){
        predictNextPos(initialVector,0,1,vectorSpeed,directionRadians);
    }

    public static void predictNextPos(float[] initialVector, int xLocationInArray, int zLocationInArray, float vectorSpeed, float directionRadians){
        float x = initialVector[xLocationInArray] , z = initialVector[zLocationInArray];
        float s = (float) Math.sin(directionRadians);
        float c = (float) Math.cos(directionRadians);
        initialVector[xLocationInArray] += s * vectorSpeed;
        initialVector[zLocationInArray] += c * vectorSpeed;
    }

    public static double stepTowardsVar(double var, double varTo, double unit){
        double difference = var - varTo;
        if(difference > unit){return - unit;}
        if(difference < -unit){return unit;}
        return -difference;
    }


    public static float angleDifference(float a1, float a2){
        final float pi2 = (float) (Math.PI * 2);
        a1 = Math.abs(a1%pi2);
        a2 = Math.abs(a2%pi2);

        float smallestDis = Math.abs(a1-a2);
        for (float i = -1; i < 2; i+=1) {
            float dis = Math.abs(a1-(a2+(i*pi2)));
            smallestDis = Math.min(dis,smallestDis);
        }

        return smallestDis;

    }


}
