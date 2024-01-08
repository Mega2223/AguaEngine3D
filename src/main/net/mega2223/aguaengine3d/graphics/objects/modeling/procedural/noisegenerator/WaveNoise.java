package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

public class WaveNoise extends StandardNoise{
    final float freqX, offsetX, freqZ, offsetZ;
    public WaveNoise(float freqX, float offsetX, float freqZ, float offsetZ){
        this.freqX = freqX; this.offsetX = offsetX; this.freqZ = freqZ; this.offsetZ = offsetZ;
    }

    @Override
    public float get(float x, float z) {
        //System.out.println(x + ":" + z);
        double xx = Math.sin((x*freqX) + offsetX);
        double zz = Math.sin((z*freqZ) + offsetZ);
        return super.applyTransformations((float)(xx+zz)/2);
    }
}
