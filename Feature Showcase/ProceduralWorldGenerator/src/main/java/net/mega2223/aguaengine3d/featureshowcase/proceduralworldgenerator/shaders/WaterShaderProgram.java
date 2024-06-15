package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.shaders;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class WaterShaderProgram extends DemoShader implements ShaderProgram {
    int light_dir_loc = -1;
    int iteration_loc = -1;

    public WaterShaderProgram(){
        id = ShaderManager.loadShaderFromFiles(new String[]{
                        Utils.USER_DIR+"\\feature showcase\\ProceduralWorldGenerator\\src\\main\\resources\\WaterVertex.vsh",
                        Utils.USER_DIR+"\\feature showcase\\ProceduralWorldGenerator\\src\\main\\resources\\WaterFragment.fsh"
                }
        );
        initUniforms();
    }

    @Override
    public void initUniforms() {
        super.initUniforms();
        light_dir_loc = GL30.glGetUniformLocation(id,"lightDir");
        iteration_loc = GL30.glGetUniformLocation(id,"iteration");
    }

    @Override
    public void setLightDirection(float x, float y, float z){
        float mag = (float) Math.sqrt(x*x + y*y + z*z);
        x/=mag; y/=mag; z/=mag;
        GL30.glUseProgram(id);
        GL30.glUniform3f(light_dir_loc,x,y,z);
    }

    @Override
    public void setUniforms(int iteration, float[] translationMatrix, float[] projectionMatrix) {
        super.setUniforms(iteration, translationMatrix, projectionMatrix);
        GL30.glUniform1i(iteration_loc,iteration);
    }
}
