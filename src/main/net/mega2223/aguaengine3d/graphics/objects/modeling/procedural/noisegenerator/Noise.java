package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

import net.mega2223.aguaengine3d.misc.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public interface Noise {
    float get(float x, float z);

    static BufferedImage NoiseToImage(Noise noise, int width, int height, float scale){
        BufferedImage output = new BufferedImage(width, height,BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = output.createGraphics(); graphics.setColor(Color.black);
        graphics.drawRect(0,0,width,height);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float x = ((float)i)*scale, z = ((float) j)*scale;
                float v = noise.get(x,z);
                int col = Math.min(Math.max((int)(v*(255)),-255),255);
                col = col > 0 ? col : (-col) <<8;
                output.setRGB(i,j,col);
            }
        }
        return output;
    }
}
