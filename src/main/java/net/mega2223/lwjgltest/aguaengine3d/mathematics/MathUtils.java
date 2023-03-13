package net.mega2223.lwjgltest.aguaengine3d.mathematics;

import java.util.List;
import java.util.Random;

public class MathUtils {
    public static Object doWeightedSelection(List objectList, float[] weights){
        return doWeightedSelection(objectList.toArray(),weights);
    }
    public static Object doWeightedSelection(Object[] objects, float[] weights){
        if(objects.length!= weights.length){throw new UnsupportedOperationException("Objects or weights not set up correctly");}
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
}
