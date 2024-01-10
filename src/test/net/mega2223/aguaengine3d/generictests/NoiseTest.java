package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.StackedNoises;
import net.mega2223.aguaengine3d.misc.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

//▒▓░▐▌
public class NoiseTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        StackedNoises noise = new StackedNoises();
        int SIZE = 1024;
        float scale = 1;
        for (int i = 0; i < 6; i++) {
            PerlinNoise act = new PerlinNoise((int) scale, (int) scale);
            noise.add(act);
            BufferedImage out = Noise.NoiseToImage(act, SIZE, SIZE,scale/SIZE);
            ImageIO.write(out,"png",new File(Utils.USER_DIR+"\\src\\test\\out\\noise_"+i+".png"));
            System.out.println("Gerei :)");
            BufferedImage out2 = Noise.NoiseToImage(noise, SIZE, SIZE,scale/SIZE);

            ImageIO.write(out2,"png",new File(Utils.USER_DIR+"\\src\\test\\out\\noise_final.png"));
            System.out.println("Gerei o maior :)");
            act.setHeightScale(1/(scale*scale*scale));
            scale*=2F;
            List<Noise> noises = noise.getNoises();

            for(Noise actt : noises){
                PerlinNoise actt1 = (PerlinNoise) actt; actt1.setTranslations(0,0,scale,scale);
            }
        }

    }
}
