package net.mega2223.aguaengine3d.featureshowcase.utils;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgramTemplate;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;

public class GrassShaderProgram extends ShaderProgramTemplate implements ShaderProgram {

    static final float R = .2F, G = .7F, B = .2F;

    public GrassShaderProgram(){
        id = ShaderManager.loadShaderFromFiles(new String[]{
                Utils.USER_DIR+"\\feature showcase\\src\\main\\resources\\GrassFragment.fsh",
                Utils.USER_DIR+"\\feature showcase\\src\\main\\resources\\Vertex.vsh",
                Utils.USER_DIR+"\\feature showcase\\src\\main\\resources\\NormalCalculator.geom"
        }
        );
        initUniforms();
    }

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        super.setUniforms(interation, translationMatrix, projectionMatrix);
    }
}
