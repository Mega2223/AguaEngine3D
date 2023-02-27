package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class TextureShaderProgram implements ShaderProgram{

    final int programID;

    public TextureShaderProgram(){
        programID = ShaderManager.loadShaderFromFiles(
                new String[]{
                        Utils.SHADERS_DIR + "\\TextureFragShader.fsh",
                        Utils.SHADERS_DIR + "\\TextureVertexShader.vsh"
                }
        );
        initUniforms();
    }

    @Override
    public int getID() {
        return programID;
    }

    @Override
    public void initUniforms() {
        projectionMatrixLocation = GL30.glGetUniformLocation(programID,"projection");
        translationMatrixLocation = GL30.glGetUniformLocation(getID(),"translation");
    }

    int translationMatrixLocation = -1;
    int projectionMatrixLocation = -1;

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(getID());
        GL30.glUniformMatrix4fv(translationMatrixLocation,false,translationMatrix);
        GL30.glUniformMatrix4fv(projectionMatrixLocation,false,projectionMatrix);

    }
}
