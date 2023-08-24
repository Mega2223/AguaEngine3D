package net.mega2223.lwjgltest.aguaengine3d.graphics.utils;

import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public class ShaderManager {

    private ShaderManager(){}

    //public static int loadShaderFromFiles(String[] shaderPaths){
    //    return loadShaderFromFiles(shaderPaths,null);
    //}

    public static int loadShaderFromFiles(String[] shaderPaths){

        String[] shaderContents = new String[shaderPaths.length];
        int[] shaderTypes = new int[shaderContents.length];
        for (int i = 0; i < shaderContents.length; i++) {
            shaderContents[i] = Utils.readFile(shaderPaths[i]);
            String shaderType = shaderPaths[i].substring(shaderPaths[i].length()-4);
            
            if(shaderType.equalsIgnoreCase(".vsh")){ shaderTypes[i] = GL30.GL_VERTEX_SHADER;
            } else if (shaderType.equalsIgnoreCase(".fsh")){ shaderTypes[i] = GL30.GL_FRAGMENT_SHADER;
            } else if (shaderType.equalsIgnoreCase(".gsh")){ shaderTypes[i] = GL32.GL_GEOMETRY_SHADER; }
            else {throw new IllegalStateException("\""+shaderType+"\" is not a valid shader type OwO");}
        }

        int program = genProgramFromContent(shaderContents,shaderTypes);
        GL30.glLinkProgram(program);
        return program;
    }

    public static int loadShaderFromContent(String content, int shaderType){
        int shader = GL30.glCreateShader(shaderType);
        GL30.glShaderSource(shader,content);
        GL30.glCompileShader(shader);
        if(GL30.glGetShaderi(shader,GL30.GL_COMPILE_STATUS) == GL30.GL_FALSE){
            System.out.println("Failed to compile shader " + shader);
            return -1;
        }
        return shader;
    }

    public static int genProgramFromContent(String[] shaderContents, int[] respectiveTypes){
        if(shaderContents.length != respectiveTypes.length){return -1;}
        int[] shaders = new int[respectiveTypes.length];
        int program = GL30.glCreateProgram();

        for (int i = 0; i < respectiveTypes.length; i++) {
            shaders[i] = loadShaderFromContent(shaderContents[i], respectiveTypes[i]);
            GL30.glAttachShader(program,shaders[i]);
        }
        GL30.glLinkProgram(program);
        return program;
    }

}
