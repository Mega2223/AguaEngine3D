package net.mega2223.aguaengine3d.graphics.utils;

import org.lwjgl.opengl.GL30;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class TextureManager {
    private TextureManager(){}

    public static int loadTexture(String path) {
        int width, height;
        int[] pixels;
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(path));
            width = image.getWidth();
            height = image.getHeight();
            pixels = new int[width * height];
            image.getRGB(0, 0, width, height, pixels, 0, width);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        int[] data = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);

            data[i] = a << 24 | b << 16 | g << 8 | r;
        }

        int result = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, result);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);

        IntBuffer buffer = ByteBuffer.allocateDirect(data.length << 2)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        buffer.put(data).flip();

        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, width, height, 0, GL30.GL_RGBA,
                GL30.GL_UNSIGNED_BYTE, buffer);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        return result;
    }

    public static int loadTexture(BufferedImage image){

        int texture = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);

        IntBuffer data = genFromImage(image);

        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, image.getWidth(), image.getHeight(), 0, GL30.GL_RGBA,
                GL30.GL_UNSIGNED_BYTE, data);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        return texture;
    }

    public static int generateImageTexture(int w, int h){
        int texture = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGB, w, h, 0, GL30.GL_RGB, GL30.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);
        return texture;
    }

    public static int generateDepthTexture(int w, int h){
        int texture = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, texture);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_DEPTH_COMPONENT16, w, h, 0, GL30.GL_DEPTH_COMPONENT, GL30.GL_FLOAT, (ByteBuffer) null);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D,0);

        return texture;
    }

    public static int generateCubemapTexture(BufferedImage[] textures){
        int texture = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_CUBE_MAP,texture);
        for (int i = 0; i < 6; i++) {
            BufferedImage act = textures[i];
            IntBuffer data = genFromImage(act);
            int current = GL30.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i;
            GL30.glTexImage2D(
                    current,
                    0, GL30.GL_RGBA, act.getWidth(), act.getHeight(), 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, data
            );

        }
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_CUBE_MAP, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
        return texture;
    }

    public static int[] getRGBATextureData(int texture, int x, int y){
        return getTextureData(texture,x,y,GL30.GL_RGBA,GL30.GL_UNSIGNED_BYTE);
    }

    public static int[] getDepthTextureData(int texture, int x, int y){
        return getTextureData(texture,x,y,GL30.GL_RED,GL30.GL_FLOAT);
    }

    public static int[] getTextureData(int texture, int x, int y, int format, int type){
        int[] data = new int[x*y];
        GL30.glReadPixels(0,0,x,y,format,type,data);
        return data;
    }

    public static BufferedImage getRGBAAsImage(int[] data, int w, int h){
        BufferedImage ret = new BufferedImage(w,h,BufferedImage.TYPE_4BYTE_ABGR);
        int i = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                ret.setRGB(x,y,i);
                i++;
            }
        }
        return ret;
    }

    public static IntBuffer genFromImage(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        int[] data = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            int a = (pixels[i] & 0xff000000) >> 24;
            int r = (pixels[i] & 0xff0000) >> 16;
            int g = (pixels[i] & 0xff00) >> 8;
            int b = (pixels[i] & 0xff);
            data[i] = a << 24 | b << 16 | g << 8 | r;
        }
        IntBuffer buffer = ByteBuffer.allocateDirect(data.length << 2)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        buffer.put(data).flip();
        return buffer;
    }


}
