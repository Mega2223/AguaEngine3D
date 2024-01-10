package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.misc.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

//▒▓░▐▌
public class PerlinTest {
    public static void main(String[] args) throws IOException {
        PerlinNoise noise = new PerlinNoise(10, 10);
        //noise.setTranslations(0,0,20F,20F);
        BufferedImage out = Noise.NoiseToImage(noise,1000,1000,100);
        ImageIO.write(out,"png",new File(Utils.USER_DIR+"\\src\\test\\out\\img.png"));
    }
}
