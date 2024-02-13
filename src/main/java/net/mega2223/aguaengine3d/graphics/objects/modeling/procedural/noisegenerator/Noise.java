package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.awt.*;
import java.awt.image.BufferedImage;
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

    static Model NoiseToModel(Noise noise, int width, int depth, float samplingScale, float modelScale, ShaderProgram program){
        float[] vertices = new float[4*width*depth];
        List<Integer> indiceList = new ArrayList<>();
        for (int i = 0; i < vertices.length; i+=4) {
            int ii = i/4;
            int x = ii%width, z = ii/depth;
            vertices[i] = x * modelScale;
            vertices[i+1] = noise.get(x*samplingScale,z*samplingScale)  * modelScale;
            vertices[i+2] = z  * modelScale;
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

    static Model NoiseToModel(Noise noise, float minX, float minZ, float maxX, float maxZ, float step, float xzScale, float yScale, ShaderProgram program){
        int samples = (int) Math.ceil((maxX - minX) * (maxZ - minZ) / (step * step));
        List<Float> verticeList = new ArrayList<>(samples*4);
        List<Integer> indexList = new ArrayList<>(samples*4);

        int i = 0, rowSize = (int) Math.ceil(((maxX - minX)/step)+.001F);
        for (float x = minX; x < maxX; x+=step) {
            for (float z = minZ; z < maxZ; z+=step) {
                verticeList.add(x*xzScale); verticeList.add(noise.get(x,z)*yScale);
                verticeList.add(z*xzScale); verticeList.add(0F);
                if(x + step < maxX && z + step < maxZ){
                    indexList.add(i); indexList.add(i+1); indexList.add(i+rowSize);
                }
                if (x > minX && z > minZ){
                    indexList.add(i); indexList.add(i-1); indexList.add(i-rowSize);
                }
                i++;
            }
        }
        float[] vertices = new float[verticeList.size()];
        System.out.println("V: " + vertices.length / 4);
        for (int j = 0; j < vertices.length; j++) {vertices[j] = verticeList.get(j);}
        int[] indices = new int[indexList.size()];
        for (i = 0; i < indices.length; i++) {indices[i] = indexList.get(i);}
        for (int j = 0; j < indices.length; j++) {
            if(indices[j]>=vertices.length / 4){throw new RuntimeException(j + ": " + indices[j]);}
        }
        return new Model(vertices,indices,program);
    }
}
