package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

public class UniformNoise extends StandardNoise implements Noise {
    final float height;
    public UniformNoise(float height){this.height = height;}
    @Override
    public float get(float x, float z) {
        return super.applyTransformations(height);
    }
}
