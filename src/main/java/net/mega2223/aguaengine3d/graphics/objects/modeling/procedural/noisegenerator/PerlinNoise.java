package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

import net.mega2223.aguaengine3d.mathematics.interpolation.CubicInterpolator;
import net.mega2223.aguaengine3d.mathematics.Interpolator;

import java.util.Random;

public class PerlinNoise extends TransformableNoise implements Noise{
    final float[][][] vectorSpace;
    final int DX, DZ;
    private final Random r;

    private Interpolator interpolationMethod = CubicInterpolator.INSTANCE;
    public PerlinNoise(int x,int z){
        this(x,z,new Random().nextLong());
    }
    public PerlinNoise(int x, int z, long seed) {
        vectorSpace = new float[x][z][2];
        DX = x; DZ = z;
        r = new Random(seed);
        populateVectorSpace();
    }

    void populateVectorSpace(){
        for (int x = 0; x < vectorSpace.length; x++) {
            for (int z = 0; z < vectorSpace[x].length; z++) {
                double theta = r.nextFloat() * Math.PI * 2;
                double c = Math.cos(theta); double s = Math.sin(theta);
                vectorSpace[x][z][0] = (float) s; vectorSpace[x][z][1] = (float) c;
            }
        }
    }

    @Override
    public float get(float x, float z) {
        return get(x,z,false);
    }

    public float get(float x, float z, boolean normalize) {
        x = xToLocal(x);
        z = zToLocal(z);
        int xF = (int) Math.floor(x);
        int xC = (int) Math.ceil(x);
        int zF = (int) Math.floor(z);
        int zC = (int) Math.ceil(z);
        int xCI = xC < 0 ? DX - (-xC % DX) - 1 : xC % DX;
        int zCI = zC < 0 ? DZ - (-zC % DZ) - 1 : zC % DZ;
        int xFI = xF < 0 ? DX - (-xF % DX) - 1 : xF % DX;
        int zFI = zF < 0 ? DZ - (-zF % DZ) - 1 : zF % DZ;

        float[] v1, v2, v3, v4;
        v1 = vectorSpace[xCI][zCI];
        v2 = vectorSpace[xFI][zCI];
        v3 = vectorSpace[xCI][zFI];
        v4 = vectorSpace[xFI][zFI];

        //so much for perfomance
        float displXC = x - xC;
        float displZC = z - zC;
        float displXF = x - xF;
        float displZF = z - zF;
        float dCCX = displXC;
        float dCCZ = displZC;
        float dFCX = displXF;
        float dFCZ = displZC;
        float dCFX = displXC;
        float dCFZ = displZF;
        float dFFX = displXF;
        float dFFZ = displZF;

        if (normalize) {
            float sqrtDCC = (float) Math.sqrt(dCCX * dCCX + dCCZ * dCCZ);
            float sqrtDFC = (float) Math.sqrt(dFCX * dFCX + dFCZ * dFCZ);
            float sqrtDCF = (float) Math.sqrt(dCFX * dCFX + dCFZ * dCFZ);
            float sqrtDFF = (float) Math.sqrt(dFFX * dFFX + dFFZ * dFFZ);
            dCCX /= sqrtDCC;
            dCCZ /= sqrtDCC;
            dFCX /= sqrtDFC;
            dFCZ /= sqrtDFC;
            dCFX /= sqrtDCF;
            dCFZ /= sqrtDCF;
            dFFX /= sqrtDFF;
            dFFZ /= sqrtDFF;
        }

        float dCC = dCCX * v1[0] + dCCZ * v1[1];
        float dFC = dFCX * v2[0] + dFCZ * v2[1];
        float dCF = dCFX * v3[0] + dCFZ * v3[1];
        float dFF = dFFX * v4[0] + dFFZ * v4[1];

        float n1 = interpolationMethod.interpolate(dFF, dCF, displXF);
        float n2 = interpolationMethod.interpolate(dFC, dCC, displXF);
        float noise = interpolationMethod.interpolate(n1, n2, displZF);
        return super.applyTransformations(Math.max(-1, Math.min(noise, 1)));
    }

    public Interpolator getInterpolationMethod() {
        return interpolationMethod;
    }

    public void setInterpolationMethod(Interpolator interpolationMethod) {
        this.interpolationMethod = interpolationMethod;
    }
}
