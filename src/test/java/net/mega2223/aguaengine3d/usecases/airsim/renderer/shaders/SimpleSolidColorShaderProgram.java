package net.mega2223.aguaengine3d.usecases.airsim.renderer.shaders;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgramTemplate;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class SimpleSolidColorShaderProgram extends ShaderProgramTemplate implements ShaderProgram {
    float[] color;

    public SimpleSolidColorShaderProgram(float r, float g, float b){

        this.color = new float[]{r,g,b,1};
        id = ShaderManager.loadShaderFromFiles(
                new String[]{
                        Utils.USER_DIR+"\\src\\test\\resources\\airsim\\shaders\\SolidColorFragmentShader.fsh",
                        Utils.USER_DIR+"\\src\\test\\resources\\airsim\\shaders\\SolidColorVertexShader.vsh"
                }
        );
        initUniforms();
    }

    int colorUniformLocation = -1;
    @Override
    public void initUniforms() {
        super.initUniforms();
        colorUniformLocation = GL30.glGetUniformLocation(id,"fragColor");
    }

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        super.setUniforms(interation, translationMatrix, projectionMatrix);
        GL30.glUseProgram(this.id);
        GL30.glUniform4f(colorUniformLocation, color[0],color[1],color[2],color[3]);
    }
}
