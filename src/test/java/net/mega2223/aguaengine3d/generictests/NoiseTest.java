package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.Noise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.PerlinNoise;
import net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator.StackedNoises;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

//▒▓░▐▌
public class NoiseTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        StackedNoises noise = new StackedNoises();
        int SIZE = 512;
        float scale = 3;

        JFrame frame = new JFrame("OI :)");
        frame.setSize(SIZE,SIZE );
        JLabel comp = new JLabel();
        frame.add(comp); ImageIcon icon =  new ImageIcon();
        frame.setAlwaysOnTop(true); frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        comp.setIcon(icon); frame.setVisible(true);

        for (int i = 0; i < 7; i++) {
            PerlinNoise act = new PerlinNoise((int) scale, (int) scale);
            noise.add(act);
            //BufferedImage out = Noise.NoiseToImage(act, SIZE, SIZE,scale/SIZE);
            //ImageIO.write(out,"png",new File(Utils.USER_DIR+"\\src\\test\\out\\noise_"+i+".png"));
            //System.out.println("Gerei :)");
            BufferedImage out2 = Noise.NoiseToImage(noise, SIZE, SIZE,scale/SIZE);
            icon.setImage(out2);
            comp.invalidate();
            frame.invalidate();
            frame.repaint();
            frame.pack();
            //ImageIO.write(out2,"png",new File(Utils.USER_DIR+"\\src\\test\\out\\noise_final.png"));
            System.out.println("Gerei o maior :)");
            act.setHeightScale(3F/scale);
            scale*= (float) (Math.PI/3F) * 2F;
            List<Noise> noises = noise.getNoises();

            for(Noise actt : noises){
                PerlinNoise actt1 = (PerlinNoise) actt; actt1.setTranslations(0,0,scale,scale);
            }

            Thread.sleep(2000);

        }
        System.out.println("zzzz");

    }
}
