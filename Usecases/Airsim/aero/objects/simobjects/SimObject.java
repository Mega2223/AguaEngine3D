package net.mega2223.aguaengine3d.usecases.aero.objects.simobjects;

import net.mega2223.aguaengine3d.graphics.objects.modeling.TexturedModel;

public abstract class SimObject extends TexturedModel {

    public SimObject(float[] vertices, int[] indices, float[] textureShift, String textureDir) {
        super(vertices, indices, textureShift, textureDir);
    }

    public SimObject(TexturedModel model) {
        super(model.getRelativeVertices(), model.getIndices(), model.getTextureShift(), model.getTexture());
    }

}
