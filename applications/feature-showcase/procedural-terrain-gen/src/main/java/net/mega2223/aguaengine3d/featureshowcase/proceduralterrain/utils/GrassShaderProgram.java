package net.mega2223.aguaengine3d.featureshowcase.proceduralterrain.utils;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgramTemplate;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class GrassShaderProgram extends ShaderProgramTemplate implements ShaderProgram {

    static final float R = .2F, G = .7F, B = .2F;
    int itneration_uni_loc = -1;
    public GrassShaderProgram(){
        id = ShaderManager.loadShaderFromFiles(new String[]{
                Utils.USER_DIR+"\\feature showcase\\procedural-terrain-gen\\src\\main\\resources\\GrassFragment.fsh",
                Utils.USER_DIR+"\\feature showcase\\procedural-terrain-gen\\src\\main\\resources\\Vertex.vsh",
                Utils.USER_DIR+"\\feature showcase\\procedural-terrain-gen\\src\\main\\resources\\NormalCalculator.geom"
        }
        );
        initUniforms();
    }

    @Override
    public void initUniforms() {
        super.initUniforms();
        itneration_uni_loc = GL30.glGetUniformLocation(id,"iteration");
    }

    @Override
    public void setUniforms(int iteration, float[] translationMatrix, float[] projectionMatrix) {
        super.setUniforms(iteration, translationMatrix, projectionMatrix);
        GL30.glUniform1i(itneration_uni_loc,iteration);
    }
}
