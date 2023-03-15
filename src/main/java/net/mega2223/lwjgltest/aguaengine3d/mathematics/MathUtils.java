package net.mega2223.lwjgltest.aguaengine3d.mathematics;

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
}
