package net.mega2223.aguaengine3d.graphics.objects.shadering;

import net.mega2223.aguaengine3d.graphics.utils.ShaderDictionary;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class SolidColorShaderProgram extends ShaderProgramTemplate implements ShaderProgram {
    private static final String VERTEX_SH = Utils.SHADERS_DIR+"\\SolidColorVertexShader.vsh";
    private static final String FRAG_SH = Utils.SHADERS_DIR+"\\SolidColorFragmentShader.fsh";

    private final int program;
    private final float[] color;
    public SolidColorShaderProgram(float r, float g, float b){this(r,g,b,1);}
    public SolidColorShaderProgram(float r, float g, float b, float a){this(r,g,b,a,null);}
    public SolidColorShaderProgram(float r, float g, float b, float a, ShaderDictionary dict){
        color = new float[]{r,g,b,a};
        program = ShaderManager.loadShaderFromFiles(new String[]{
                VERTEX_SH,FRAG_SH
        },dict);
        initUniforms();
    }

    @Override
    public int getID() {
        return program;
    }

    private int uniformColorLocation = -1;

    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(getID());
        super.setUniforms(interation, translationMatrix, projectionMatrix);
        GL30.glUniform4f(uniformColorLocation,color[0],color[1],color[2],color[3]);

    }

    public void initUniforms(){
        uniformColorLocation = GL30.glGetUniformLocation(getID(),"color2");
        rotationMatrixLocation = GL30.glGetUniformLocation(getID(),"rotation");
        projectionMatrixLocation = GL30.glGetUniformLocation(getID(),"projection");
        translationMatrixLocation = GL30.glGetUniformLocation(getID(),"translation");
    }
}
