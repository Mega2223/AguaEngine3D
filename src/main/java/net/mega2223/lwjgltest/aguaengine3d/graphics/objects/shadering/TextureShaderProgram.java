package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class TextureShaderProgram extends ShaderProgramTemplate implements ShaderProgram{

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
        super.initUniforms();
    }

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(getID());
        super.setUniforms(interation, translationMatrix, projectionMatrix);

    }
}
