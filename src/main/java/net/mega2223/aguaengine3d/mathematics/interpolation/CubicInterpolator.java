package net.mega2223.aguaengine3d.mathematics.interpolation;

import net.mega2223.aguaengine3d.mathematics.Interpolator;

public class CubicInterpolator implements Interpolator {

    @Override
    public float interpolate(float v1, float v2, float p) {
        return (v2 - v1) * (3F - p * 2F) * p * p + v1;
    }

    public float interpolate(float v1, float v2, float p, int interpolationDegrees){
        for (int i = 0; i < interpolationDegrees; i++) {
            p = interpolate(0,1,p);
        }
        return interpolate(v1,v2,p);
    }

    public static final CubicInterpolator INSTANCE = new CubicInterpolator();
}
