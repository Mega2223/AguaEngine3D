package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

public abstract class TransformableNoise implements Noise {
    float heightScale = 1;
    float dislocation = 0;
    float[] displacement = {0,0};
    float[] spatialScaling = {1,1};

    public float applyTransformations(float v) {
        return v * heightScale + dislocation;
    }

    protected float xToLocal(float x){
        return (x - displacement[0])/spatialScaling[0];
    }

    protected float zToLocal(float z){
        return (z - displacement[1])/spatialScaling[1];
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

    public void setTranslations(float displacementX, float displacementZ, float scaleX, float scaleZ){
        displacement[0] = displacementX; displacement[1] = displacementZ;
        spatialScaling[0] = scaleX; spatialScaling[1] = scaleZ;
    }
}
