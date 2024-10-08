package net.mega2223.aguaengine3d.featureshowcase.proceduralterrain.utils;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgramTemplate;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;
public class WaterShaderProgram extends ShaderProgramTemplate implements ShaderProgram {
    int itneration_uni_loc = -1;
    public WaterShaderProgram(){
        id = ShaderManager.loadShaderFromFiles(new String[]{
                        Utils.USER_DIR+"WaterFragment.fsh",
                        Utils.USER_DIR+"WaterVertex.vsh"
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
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        super.setUniforms(interation, translationMatrix, projectionMatrix);
        GL30.glUniform1i(itneration_uni_loc,interation);
    }
}
