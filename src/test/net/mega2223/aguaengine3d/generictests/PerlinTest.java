package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.WaveNoise;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//▒▓░▐▌
public class PerlinTest {
    public static void main(String[] args) throws IOException {
        int X = 10, Z = 10;
        Noise noise = new PerlinNoise(X, Z);
        //noise = new WaveNoise(2,0,2,0);
        final int scale = 200;
        int width = X * scale; int height = Z * scale;
        BufferedImage output = new BufferedImage(width, height,BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = output.createGraphics(); graphics.setColor(Color.black);
        graphics.drawRect(0,0,width,height);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float x = ((float) i)/scale, z = ((float) j)/scale;
                float v = noise.get(x,z);
                int col = Math.min(Math.max((int)(v*(255)),-255),255);
                col = col > 0 ? col : (-col)<<8;
                output.setRGB(i,j,col);
            }
        }
        ImageIO.write(output,"png",new File(Utils.USER_DIR+"\\src\\test\\out\\img.png"));
    }
}
