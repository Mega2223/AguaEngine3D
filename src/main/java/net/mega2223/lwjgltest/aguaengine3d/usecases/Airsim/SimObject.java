package net.mega2223.lwjgltest.aguaengine3d.usecases.Airsim;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;

public abstract class SimObject extends TexturedModel {

    public SimObject(float[] triangles, int[] indices, float[] textureShift, String textureDir) {
        super(triangles, indices, textureShift, textureDir);
    }

    public SimObject(TexturedModel model) {
        super(model.getRelativeVertices(), model.getIndexes(), model.getTextureShift(), model.getTexture());
    }

}
