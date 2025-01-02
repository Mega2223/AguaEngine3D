package net.mega2223.aguaengine3d.graphics.objects.shadering;

import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;

public class NormalDebugShaderProgram extends ShaderProgramTemplate implements ShaderProgram{

    private static final String VERTEX_SH = Utils.SHADERS_DIR+"\\NormalDebugVertexShader.vsh";
    private static final String FRAG_SH = Utils.SHADERS_DIR+"\\NormalDebugFragmentShader.fsh";

    public NormalDebugShaderProgram(){
        this.id = ShaderManager.loadShaderFromFiles(new String[]{
                VERTEX_SH,FRAG_SH
        });
        initUniforms();
    }
}
