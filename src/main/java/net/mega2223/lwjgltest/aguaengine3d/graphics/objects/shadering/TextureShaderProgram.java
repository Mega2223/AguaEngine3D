package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderDictonary;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class TextureShaderProgram extends ShaderProgramTemplate implements ShaderProgram{

    public TextureShaderProgram(){this(null);}
    public TextureShaderProgram(ShaderDictonary dict){
        id = ShaderManager.loadShaderFromFiles(
                new String[]{
                        Utils.SHADERS_DIR + "\\TextureFragShader.fsh",
                        Utils.SHADERS_DIR + "\\TextureVertexShader.vsh"
                }
        ,dict);
        initUniforms();
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public void initUniforms() {
        super.initUniforms();
    }

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        super.setUniforms(interation, translationMatrix, projectionMatrix);
    }
}
