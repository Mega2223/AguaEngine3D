package net.mega2223.aguaengine3d.demos.particlesim;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.utils.ShaderManager;
import net.mega2223.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;

public class ParticleShaderProgram implements ShaderProgram {
    int id, translation_loc = 0, rotation_loc = 0, projection_loc = 0, color_loc = 0, radius_loc = 0;
    public ParticleShaderProgram(){
        id = ShaderManager.loadShaderFromFiles(
                new String[]{
                        Utils.SHADERS_DIR + "\\fragment.fsh", Utils.SHADERS_DIR + "\\vertex.vsh"
                });
        initUniforms();
    }

    @Override
    public int getID() {
        return id;
    }

    public void initUniforms() {
        translation_loc = GL30.glGetUniformLocation(id,"translation");
        rotation_loc = GL30.glGetUniformLocation(id,"rotation");
        projection_loc = GL30.glGetUniformLocation(id,"projection");
        color_loc = GL30.glGetUniformLocation(id,"color2");
        radius_loc = GL30.glGetUniformLocation(id,"radius");
        GL30.glUseProgram(id);
        GL30.glUniform4f(color_loc,
                (float) Math.random(),
                (float) Math.random(),
                (float) Math.random(),
                1);
    }
    public void setUniforms(int iteration, float[] translationMatrix, float[] projectionMatrix) {
        GL30.glUseProgram(id);
        GL30.glUniformMatrix4fv(translation_loc,false,translationMatrix);
        GL30.glUniformMatrix4fv(projection_loc,false,projectionMatrix);
    }

    public int[] getLightspaceTextureLocs() {return null;}
    public void setRotationMatrix(float[] m4) {}
    public void setRenderShadows(int index, boolean s) {}
}
