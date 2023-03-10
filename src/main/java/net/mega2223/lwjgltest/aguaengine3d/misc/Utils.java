package net.mega2223.lwjgltest.aguaengine3d.misc;

import java.io.*;

public class Utils {
    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String RESOURCES_DIR = USER_DIR + "\\src\\main\\resources";
    public static final String SHADERS_DIR = RESOURCES_DIR + "\\shaders";
    public static final String MODELS_DIR = RESOURCES_DIR + "\\models";
    public static final String TEXTURES_DIR = RESOURCES_DIR + "\\textures";
    public static final String PROCEDURAL_BUILDINGS_DIR = RESOURCES_DIR + "\\procedural buildings";

    public static String readFile (String path){

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String ret = "";
            String line = reader.readLine();
            while(line != null){
                ret += line + "\n";
                line = reader.readLine();
            }
            return ret;
        } catch (IOException e) {
            RuntimeException runtimeException = new RuntimeException("There is no directory such as " + path);
            e.printStackTrace();
            throw runtimeException;
        }

    }
    public static void writeToFile(String path, String content){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(content);
            writer.close();
        } catch (IOException ex){}
    }

}
