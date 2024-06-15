package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.daycycle;

import java.util.Comparator;

public class CycleKeyframe {
    final float cycleTime,starAlpha;
    final float[] skyColor, cloudColor, fogData;


    public CycleKeyframe(float cycleTime, float[] skyColor, float[] cloudColor, float[] fogData, float starAlpha) {
        this.cycleTime = cycleTime;
        this.skyColor = skyColor;
        this.cloudColor = cloudColor;
        this.fogData = fogData;
        this.starAlpha = starAlpha;
    }

    float getCycleTime(){
        return cycleTime;
    }
    void getSkyColor(float[] dest){
        System.arraycopy(skyColor,0,dest,0,dest.length);
    }
    void getCloudColor(float[] dest){
        System.arraycopy(cloudColor,0,dest,0,dest.length);
    }
    void getFogData(float [] dest){
        System.arraycopy(fogData,0,dest,0,dest.length);
    }
    float getStarAlpha(){return starAlpha;}
}
