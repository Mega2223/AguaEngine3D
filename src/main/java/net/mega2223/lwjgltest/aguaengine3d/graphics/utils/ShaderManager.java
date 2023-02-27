package net.mega2223.lwjgltest.aguaengine3d.graphics.utils;

import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class ShaderManager {

    private ShaderManager(){}

    public static int loadShaderFromFiles(String[] shaderPaths){

        int program = GL30.glCreateProgram();
        for (int i = 0; i < shaderPaths.length; i++) {
            String shaderDirectory = shaderPaths[i];
            String shaderType = shaderDirectory.substring(shaderDirectory.length()-4);
            int shader;
            if(shaderType.equalsIgnoreCase(".vsh")){
                shader = GL30.glCreateShader(GL30.GL_VERTEX_SHADER);
            } else if (shaderType.equalsIgnoreCase(".fsh")){
                shader = GL30.glCreateShader(GL30.GL_FRAGMENT_SHADER);
            } else if (shaderType.equalsIgnoreCase(".gsh")){
                shader = GL32.glCreateShader(GL32.GL_GEOMETRY_SHADER);
            }
            else {throw new IllegalStateException("\""+shaderType+"\" is not a valid shader type OwO");}

            String shaderContent = Utils.readFile(shaderDirectory);
            GL30.glShaderSource(shader, shaderContent);
            GL30.glCompileShader(shader);
            if(GL30.glGetShaderi(shader,GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE){
                System.out.println("Failed to compile shader from path " + shaderPaths[i]);
            }
            GL30.glAttachShader(program, shader);
        }
        GL30.glLinkProgram(program);
        return program;

    }

}
