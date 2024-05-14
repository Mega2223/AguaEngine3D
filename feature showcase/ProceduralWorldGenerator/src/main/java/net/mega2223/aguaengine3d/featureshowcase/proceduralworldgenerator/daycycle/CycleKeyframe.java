package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.daycycle;

public interface CycleKeyframe {
    float getCycleTime();
    void getSkyColor(float[] dest);
    void getCloudColor(float[] dest);
    void getFogData(float [] dest);
    float getStarAlpha();
}
