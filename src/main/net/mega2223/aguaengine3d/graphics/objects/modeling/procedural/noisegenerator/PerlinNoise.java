package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

import java.util.Random;

public class PerlinNoise extends StandardNoise implements Noise{
    final float[][][] vectorSpace;
    private final Random r = new Random();
    public PerlinNoise(int x, int z) {
        vectorSpace = new float[x][z][2];
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
        x = xToLocal(x); z = zToLocal(z);
        int xF = (int) Math.floor(x); int xC = (int) Math.ceil(x);
        int zF = (int) Math.floor(z); int zC = (int) Math.ceil(z);
        float[] v1, v2, v3, v4;
        try{
            v1 = vectorSpace[xC][zC]; v2 = vectorSpace[xF][zC];
            v3 = vectorSpace[xC][zF]; v4 = vectorSpace[xF][zF];
        } catch (ArrayIndexOutOfBoundsException ignored){
            return -1;
        }

        float dx = x - xF, dz = z - zF;
        float dot1 = dx * v1[0] + dz * v1[1];
        float dot2 = dx * v2[0] + dz * v2[1];
        float dot3 = dx * v3[0] + dz * v3[1];
        float dot4 = dx * v4[0] + dz * v4[1];

        return (dot1 + dot2 + dot3 + dot4)/4;
    }
}
