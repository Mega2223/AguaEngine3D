package net.mega2223.aguaengine3d.mathematics.interpolation;
/**
 * Cubic interpolation with an additional inflection point at the medium point between v1 and v2
 * */
public class DoubleCubicInterpolator implements Interpolator{
    public static final DoubleCubicInterpolator INSTANCE = new DoubleCubicInterpolator();
    @Override
    public float interpolate(float v1, float v2, float p) {
        boolean aboveHalf = p > .5F;
        p = aboveHalf ? p * 2 - 1 : p * 2;
        v1 = aboveHalf ? v1 + (v2-v1)/2F : v1;
        v2 = aboveHalf ? v2 : v2 - (v2-v1)/2F;
        return CubicInterpolator.INSTANCE.interpolate(v1, v2, p);
    }
}
