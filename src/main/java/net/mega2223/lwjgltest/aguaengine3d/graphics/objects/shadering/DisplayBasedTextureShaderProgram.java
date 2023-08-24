package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class DisplayBasedTextureShaderProgram extends ShaderProgramTemplate implements ShaderProgram{
    final int programID;
    int texture;

    public DisplayBasedTextureShaderProgram(int texture){
        this(texture,null);
    }

    public DisplayBasedTextureShaderProgram(int texture, ShaderDictonary dict){
        programID = ShaderManager.loadShaderFromFiles(
                new String[]{
                        Utils.SHADERS_DIR + "\\DisplayBasedFragShader.fsh",
                        Utils.SHADERS_DIR + "\\TextureVertexShader.vsh"
                }
        ,dict);
        this.texture = texture;
        initUniforms();
    }

    @Override
    public int getID() {
        return programID;
    }

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(getID());
        super.setUniforms(interation, translationMatrix, projectionMatrix);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,texture);
        GL30.glActiveTexture(texture);
    }


}
