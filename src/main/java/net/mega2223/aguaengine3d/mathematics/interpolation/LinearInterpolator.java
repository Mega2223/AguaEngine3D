package net.mega2223.aguaengine3d.mathematics.interpolation;

public class LinearInterpolator implements Interpolator{
    @Override
    public float interpolate(float v1, float v2, float p) {
        return (v2-v1) * p + v1;
    }
    public static final LinearInterpolator INSTANCE = new LinearInterpolator();
}
