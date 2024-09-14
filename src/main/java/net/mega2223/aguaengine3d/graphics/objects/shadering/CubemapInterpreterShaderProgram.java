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
    final int rotation_matrix_loc;
    final int texture;
    private float tX = 0, tY = 0, tZ = 0, rX = 0, rY = 0, rZ = 0;

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
        rotation_matrix_loc = GL30.glGetUniformLocation(id,"rotation");
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
        /* Some might say "isn't it more efficient to just not process local translations instead of"
         * making the cube constantly follow the camera?", to which I say yes, pretty much, but that would
         * require making another projection matrix, one which does not translate the object from the origin.
         * Since the shader interface is made to work without requiring a context getting variables like the
         * FOV right is currently impossible, this current solution is also barely a solution tho. So much for OOP.*/
        GL30.glUseProgram(id);
        MatrixTranslator.generateRotationMatrix(rot,rX,rY,rZ);
        //GL30.glUniformMatrix4fv(rotation_matrix_loc,false,rot);
        GL30.glUniformMatrix4fv(projection_matrix_loc,false,projectionMatrix);
        MatrixTranslator.generateTranslationMatrix(translationMatrix,tX,tY,tZ);
        GL30.glUniformMatrix4fv(translation_matrix_loc,false,translationMatrix);
    }
    /** Generates an object of the ScriptedSequence type intended to be put into a RenderingContext
     * said object updates the rotation and translation matrices with the camera view in mind before the
     * scene rendering*/
    public TransformationUpdateRunnable genRotationUpdateRunnable(float[] camera){
        return new TransformationUpdateRunnable(camera);
    }

    public class TransformationUpdateRunnable extends ScriptedSequence {
        float[] camera;
        protected TransformationUpdateRunnable(float[] camera) {
            super("RotationUpdateRunnable");
            this.camera = camera;
        }
        @Override
        protected void preLogic(int iteration, RenderingContext context) {
            super.preLogic(iteration, context);
            tX = camera[0]; tY = camera[1]; tZ = camera[2];
            rY = -camera[3];
            if(camera.length>4){rZ = camera[4]; //TODO: what
                if(camera.length>5){rX = camera[5];}
            }
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
        GL30.glDisable(GL30.GL_DEPTH_TEST);
    }

    @Override
    public void postRenderLogic() {
        GL30.glEnable(GL30.GL_DEPTH_TEST);
    }
}
