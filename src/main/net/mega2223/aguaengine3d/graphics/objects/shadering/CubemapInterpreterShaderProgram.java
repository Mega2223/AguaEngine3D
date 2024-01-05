package net.mega2223.aguaengine3d.graphics.objects.shadering;

import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.ScriptedSequence;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class CubemapInterpreterShaderProgram implements ShaderProgram{

    final int id;
    final int texture_loc;
    final int direction_vector_loc;
    final int projection_matrix_loc;
    final int translation_matrix_loc;
    final int texture;
    float rX = 0, rY = 0, rZ = 0;

    public CubemapInterpreterShaderProgram(int texture){
        id = ShaderManager.loadShaderFromFiles(new String[]{
                Utils.SHADERS_DIR+"\\CubemapVertexShader.vsh",
                Utils.SHADERS_DIR+"\\CubemapFragmentShader.fsh"
        });
        this.texture = texture;
        texture_loc = GL30.glGetUniformLocation(id,"cubemap");
        direction_vector_loc = GL30.glGetUniformLocation(id,"direction");
        projection_matrix_loc = GL30.glGetUniformLocation(id,"projection");
        translation_matrix_loc = GL30.glGetUniformLocation(id,"translation");
        GL30.glUniform1i(texture_loc,0);//redundant but ok
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public int[] getLightspaceTextureLocs() {
        return new int[0];
    }

    @Override
    public void initUniforms() {

    }
    float[] trans = new float[16];
    float[] rot = new float[16];
    @Override
    public void setUniforms(int interation, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(id);
        MatrixTranslator.generateRotationMatrix(rot,rX,rY,rZ);
        GL30.glUniformMatrix4fv(projection_matrix_loc,false,rot);
        MatrixTranslator.generateTranslationMatrix(translationMatrix,0,0,0);
        GL30.glUniformMatrix4fv(translation_matrix_loc,false,translationMatrix);
    }
    public RotationUpdateRunnable genRotationUpdateRunnable(float[] camera){
        return new RotationUpdateRunnable(camera);
    }

    public class RotationUpdateRunnable extends ScriptedSequence {
        float[] camera;
        protected RotationUpdateRunnable(float[] camera) {
            super("RotationUpdateRunnable");
            this.camera = camera;
        }
        @Override
        protected void preLogic(int itneration, RenderingContext context) {
            super.preLogic(itneration, context); //TODO what???????
            rY = -camera[3];
            if(camera.length>4){rY = camera[4];}
            if(camera.length>5){rZ = camera[5];}
        }
    }

    @Override
    public void setRotationMatrix(float[] m4) {

    }

    @Override
    public void setRenderShadows(int index, boolean s){}

    @Override
    public void preRenderLogic() {
        GL30.glUseProgram(id);
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP,texture);
        GL30.glActiveTexture(GL30.GL_TEXTURE0);

    }
}
