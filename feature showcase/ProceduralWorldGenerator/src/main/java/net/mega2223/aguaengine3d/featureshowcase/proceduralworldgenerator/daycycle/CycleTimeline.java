package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.daycycle;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.mathematics.interpolation.Interpolator;
import net.mega2223.aguaengine3d.mathematics.interpolation.LinearInterpolator;

import java.util.ArrayList;
import java.util.Comparator;

public class CycleTimeline {
    protected static final Comparator<CycleKeyframe> KEYFRAME_COMPARATOR = (o1, o2) -> (int) ((o1.cycleTime*1E10 - o2.cycleTime*1E10));

    ArrayList<CycleKeyframe> frames = new ArrayList<>();
    Interpolator interpolator = LinearInterpolator.INSTANCE;

    public CycleTimeline(){}
    public void setInterpolationMethod(Interpolator interpolator){
        this.interpolator = interpolator;
    }

    public CycleKeyframe getNext(float time){
        time = time % 1  > 0 ? time % 1 : 1-(-time % 1);
        for (int i = 0; i < frames.size(); i++) {
            CycleKeyframe act = frames.get(i);
            if(act.cycleTime > time){return act;}
        }
        return frames.get(0);
    }
    public CycleKeyframe getPrev(float time){
        time = time % 1 >= 0 ? time % 1 : 1-(-time % 1);
        for (int i = 0; i < frames.size(); i++) {
            CycleKeyframe act = frames.get(frames.size()-i-1);
            if(act.cycleTime <= time){return act;}
        }
        return frames.get(frames.size()-1);
    }

    public static float clampTime(float time){
        return time % 1 >= 0 ? time % 1 : 1-(-time % 1);
    }

    static float interpolate(float time, float varPrev, float varNext, float timePrev, float timeNext, Interpolator interpolator){
        time = clampTime(time); timePrev = clampTime(timePrev); timeNext = clampTime(timeNext);
        if(varNext == varPrev || timeNext == timePrev || time == timePrev){return varPrev;}
        if(time == timeNext){return varNext;}
        timeNext = timePrev > timeNext ? timeNext + 1 : timeNext;
        float ret = (time - timePrev) / (timeNext - timePrev);
        return interpolator.interpolate(varPrev, varNext, ret);
    }

    protected float interpolate(float time, float varPrev, float varNext, float timePrev, float timeNext){
        return interpolate(time,varPrev,varNext,timePrev,timeNext,this.interpolator);
    }

    public float getStarAlpha(float time){
        time = clampTime(time);
        CycleKeyframe next = getNext(time), prev = getPrev(time);
        return interpolate(time, prev.starAlpha, next.starAlpha, prev.cycleTime, next.cycleTime);
    }

    public void getSkyColor(float time, float[] dest){
        time = clampTime(time);
        CycleKeyframe next = getNext(time), prev = getPrev(time);
        for (int i = 0; i < 3; i++) {
            dest[i] = interpolate(time, prev.skyColor[i], next.skyColor[i], prev.cycleTime, next.cycleTime);
        }
    }
    void getCloudColor(float time, float[] dest){}
    void getFogData(float time, float [] dest){}

    public void add(CycleKeyframe obj){
        frames.add(obj);
        frames.sort(KEYFRAME_COMPARATOR);
    }

    public void remove(CycleKeyframe obj){
        frames.remove(obj);
    }

}
