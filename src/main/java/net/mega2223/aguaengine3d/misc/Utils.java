package net.mega2223.aguaengine3d.misc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class Utils {
    public static final String USER_DIR = "";
    public static final String RESOURCES_DIR = "";
    public static final String SHADERS_DIR = RESOURCES_DIR + "shaders";
    public static final String MODELS_DIR = RESOURCES_DIR + "models";
    public static final String TEXTURES_DIR = RESOURCES_DIR + "textures";
    public static final String PROCEDURAL_BUILDINGS_DIR = RESOURCES_DIR + "procedural buildings";
    public static final String FONTS_DIR = RESOURCES_DIR + "fonts";

    public static String resolvePath(String path){
        if(Files.exists(Paths.get(path))){
            return path;
        }
        return findResource(path);
    }

    public static String findResource(String path){
        path = Utils.class.getClassLoader().getResource(path).getFile();
        try {
            path = URLDecoder.decode(path,"UTF-8");
            return path;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readResource(String path) {
        return readFile(findResource(path),false);
    }

    public static String readFile(String path){
        return readFile(path, true);
    }

    public static String readFile (String path, boolean checkResources){
        if(checkResources){
            return readResource(path);
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            StringBuilder ret = new StringBuilder();
            String line = reader.readLine();
            while(line != null){
                ret.append(line).append("\n");
                line = reader.readLine();
            }
            return ret.toString();
        } catch (IOException e) {
            RuntimeException runtimeException = new RuntimeException("There is no directory such as " + path);
            e.printStackTrace();
            throw runtimeException;
        }
    }

    public static BufferedImage readImage(String path){
        path = resolvePath(path);
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(String path, String content){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path));
            writer.write(content);
            writer.close();
        } catch (IOException ignored){}
    }
    public static void writeToFile(String path, BufferedImage content){
        try {
            ImageIO.write(content,"png",new File(path));
        } catch (IOException ignored) {}
    }

}
