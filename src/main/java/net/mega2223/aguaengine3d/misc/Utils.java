package net.mega2223.aguaengine3d.misc;

import javafx.print.Collation;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.List;

public class Utils {
    private Utils(){}

    public static final String USER_DIR = "";
    public static final String RESOURCES_DIR = "";
    public static final String SHADERS_DIR = RESOURCES_DIR + "shaders";
    public static final String MODELS_DIR = RESOURCES_DIR + "models";
    public static final String TEXTURES_DIR = RESOURCES_DIR + "textures";
    public static final String PROCEDURAL_BUILDINGS_DIR = RESOURCES_DIR + "procedural buildings";
    public static final String FONTS_DIR = RESOURCES_DIR + "fonts";

    public static Reader getFileStream(String path){
        if(new File(path).exists()){
            try {
                return new FileReader(path);}
            catch (FileNotFoundException ignored) {}
        } else {
            InputStream stream = Utils.class.getClassLoader().getResourceAsStream(path);
            if(stream != null){return new InputStreamReader(stream);}
        }
        return null;
    }

    public static String readFile (String path){
        File f = new File(path);

        try {
            InputStream stream = Utils.class.getClassLoader().getResourceAsStream(path);
            stream = stream != null ? stream :  Utils.class.getClassLoader().getResourceAsStream(path.replace("\\","/"));
            BufferedReader reader = f.exists() ?
                    new BufferedReader(new FileReader(f)):
                    new BufferedReader(new InputStreamReader(stream));
            return readStreamReader(reader);
        } catch (IOException | NullPointerException e) {
            RuntimeException runtimeException = new RuntimeException("There is no directory such as " + path);
            e.printStackTrace();
            throw runtimeException;
        }
    }

    protected static String readStreamReader(BufferedReader reader) throws IOException {
        StringBuilder ret = new StringBuilder();
        String line = reader.readLine();
        while(line != null){
            ret.append(line).append("\n");
            line = reader.readLine();
        }
        return ret.toString();
    }

    public static String resolvePath(String path) {
        File f = new File(path);
        try {
            return f.exists() ? path : URLDecoder.decode(Utils.class.getClassLoader().getResource(path).getPath(),"UTF-8");
        } catch (UnsupportedEncodingException ignored) { return null; }
    }

    public static BufferedImage readImage(String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                return ImageIO.read(f);
            } else {
                InputStream stream = Utils.class.getClassLoader().getResourceAsStream(path);
                stream = stream != null ? stream :  Utils.class.getClassLoader().getResourceAsStream(path.replace("\\","/"));
                if (stream != null) {
                    return ImageIO.read(stream);
                }
            }
        } catch (IOException ignored){}
        System.out.println("WARNING: Could not find file \""+ path + "\"");
        return null;
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

    public static int[] toPrimitiveIndexArray(List<Integer> i){
        int[] ret = new int[i.size()];
        for (int j = 0; j < ret.length; j++) { ret[j] = i.get(j);}
        return ret;
    }

    public static float[] toPrimitiveArray(List<Float> i){
        float[] ret = new float[i.size()];
        for (int j = 0; j < ret.length; j++) { ret[j] = i.get(j);}
        return ret;
    }
}
