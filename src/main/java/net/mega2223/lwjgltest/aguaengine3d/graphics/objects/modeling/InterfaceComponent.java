package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;

public class InterfaceComponent extends Model{
    public InterfaceComponent(float[] vertices, int[] indexes, ShaderProgram shader) {
        super(vertices, indexes, shader);
    }

    @Override
    public float[] getCoords() {
        return super.getCoords();
    }

    private final float[] fixedProjection = MatrixTranslator.createTranslationMatrix(0,0,0);
    private final float[] translation = MatrixTranslator.createTranslationMatrix(0,0,0);
    private int it = 0;

    @Override
    public void doLogic(int itneration) {
        it = itneration;
    }

    @Override
    public void draw() {
        MatrixTranslator.generateTranslationMatrix(translation,coords[0],coords[1],coords[2]);
        shader.setUniforms(it, translation, fixedProjection);
        super.draw();
    }
}
