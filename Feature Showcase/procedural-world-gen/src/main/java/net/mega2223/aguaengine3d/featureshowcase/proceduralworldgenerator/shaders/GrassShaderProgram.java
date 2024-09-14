package net.mega2223.aguaengine3d.featureshowcase.proceduralworldgenerator.shaders;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class GrassShaderProgram extends DemoShader implements ShaderProgram {

    static final float R = .2F, G = .7F, B = .2F;
    int iteration_uni_loc = -1;
    int light_dir_loc = -1;
    public GrassShaderProgram(){
        id = ShaderManager.loadShaderFromFiles(new String[]{
                Utils.USER_DIR+"\\feature showcase\\procedural-world-gen\\src\\main\\resources\\GrassFragment.fsh",
                Utils.USER_DIR+"\\feature showcase\\procedural-world-gen\\src\\main\\resources\\GrassVertex.vsh"
        }
        );
        initUniforms();
    }

    @Override
    public void initUniforms() {
        super.initUniforms();
        iteration_uni_loc = GL30.glGetUniformLocation(id,"iteration");
        light_dir_loc = GL30.glGetUniformLocation(id,"lightDir");
    }

    @Override
    public void setUniforms(int iteration, float[] translationMatrix, float[] projectionMatrix) {
        super.setUniforms(iteration, translationMatrix, projectionMatrix);
        GL30.glUniform1i(iteration_uni_loc,iteration);
    }

    public void setLightDirection(float x, float y, float z){
        //System.out.println(x + "," + y + "," + z);
        float mag = (float) Math.sqrt(x*x + y*y + z*z);
        x/=mag; y/=mag; z/=mag;
        GL30.glUseProgram(id);
        GL30.glUniform3f(light_dir_loc,x,y,z);
    }
}
