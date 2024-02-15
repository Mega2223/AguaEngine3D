package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

public class RadialNoise extends TransformableNoise implements Noise {
    float x, z, radius;
    boolean goesDown;
    int algorithm;
    public static final int LINEAR = 0, SQUARE = 1, CUBIC = 2;

    public RadialNoise(float x, float z, float radius, int algorithm, boolean goesDown) {
        this.x = x; this.z = z; this.radius = radius; this.goesDown = goesDown; this.algorithm = algorithm;
    }

    @Override
    public float get(float x, float z) {
        x = super.xToLocal(x); z = super.zToLocal(z);
        x -= this.x; z -= this.z;
        float sqrt = (float) Math.sqrt(x * x + z * z);
        sqrt = (float) (Math.pow(sqrt,algorithm+1) - radius);
        sqrt = Math.max(sqrt,0);
        sqrt = goesDown ? -sqrt : sqrt;
        return applyTransformations(sqrt);
    }
}
