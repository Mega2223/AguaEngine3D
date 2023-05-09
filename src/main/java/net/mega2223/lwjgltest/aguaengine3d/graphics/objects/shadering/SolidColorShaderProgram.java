package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering;

import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class SolidColorShaderProgram implements ShaderProgram{
    private static final String VERTEX_SH = Utils.SHADERS_DIR+"\\SolidColorVertexShader.vsh";
    private static final String FRAG_SH = Utils.SHADERS_DIR+"\\SolidColorFragmentShader.fsh";

    private int program = -1;
    private float[] color;
    public SolidColorShaderProgram(float r,float g,float b){
        color = new float[]{r,g,b};
        program = ShaderManager.loadShaderFromFiles(new String[]{
                VERTEX_SH,FRAG_SH
        });
        initUniforms();
    }

    @Override
    public int getID() {
        return program;
    }

    private int uniformColorLocation = -1;
    private int projectionMatrixLocation = -1;
    private int translationMatrixLocation = -1;
    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(getID());
        GL30.glUniform4f(uniformColorLocation,color[0],color[1],color[2],1);
        GL30.glUniformMatrix4fv(translationMatrixLocation,false,translationMatrix);
        GL30.glUniformMatrix4fv(projectionMatrixLocation,false,projectionMatrix);

    }

    public void initUniforms(){
        uniformColorLocation = GL30.glGetUniformLocation(getID(),"color2");
        projectionMatrixLocation = GL30.glGetUniformLocation(getID(),"projection");
        translationMatrixLocation = GL30.glGetUniformLocation(getID(),"translation");
    }
}
