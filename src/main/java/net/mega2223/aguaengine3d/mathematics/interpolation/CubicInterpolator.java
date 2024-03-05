package net.mega2223.aguaengine3d.mathematics.interpolation;

public class CubicInterpolator implements Interpolator{

    @Override
    public float interpolate(float v1, float v2, float p) {
        return (v2 - v1) * (3F - p * 2F) * p * p + v1;
    }

    public static final CubicInterpolator INSTANCE = new CubicInterpolator();
}
