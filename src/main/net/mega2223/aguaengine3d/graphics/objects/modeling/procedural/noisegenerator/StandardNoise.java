package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

public abstract class StandardNoise implements Noise {
    float heightScale = 1;
    float dislocation = 0;
    float[] spatialTranslation = {0,0};

    public float applyTransformations(float v) {
        return v * heightScale + dislocation;
    }

    protected float xToLocal(float x){
        return x - spatialTranslation[0];
    }

    protected float zToLocal(float z){
        return z - spatialTranslation[1];
    }


    public float getHeightScale() {
        return heightScale;
    }

    public void setHeightScale(float heightScale) {
        this.heightScale = heightScale;
    }

    public float getDislocation() {
        return dislocation;
    }

    public void setDislocation(float dislocation) {
        this.dislocation = dislocation;
    }
}
