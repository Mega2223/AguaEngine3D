package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.misc.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
                col = col > 0 ? col : (-col) << 8;
                output.setRGB(i,j,col);
            }
        }
        return output;
    }

    static Model NoiseToModel(Noise noise, int width, int depth, float scale, ShaderProgram program){
        float[] vertices = new float[4*width*depth];
        List<Integer> indiceList = new ArrayList<>();
        for (int i = 0; i < vertices.length; i+=4) {
            int ii = i/4;
            int x = ii%width, z = ii/depth;
            vertices[i] = x;
            vertices[i+1] = noise.get(x*scale,z*scale);
            vertices[i+2] = z;
        }
        for (int i = 0; i < width*depth; i++) {
            if(!(i%width>=width-1||i/depth>=depth-1)){
                indiceList.add(i);
                indiceList.add(i+1);
                indiceList.add(i+width);
            }
            if(!(i%width==0||i/depth==0)){
                indiceList.add(i);
                indiceList.add(i-1);
                indiceList.add(i-width);
            }
        }
        int[] indices = new int[indiceList.size()];
        for (int i = 0; i < indices.length; i++) {indices[i] = indiceList.get(i);}
        return new Model(vertices,indices,program);
    }
}
