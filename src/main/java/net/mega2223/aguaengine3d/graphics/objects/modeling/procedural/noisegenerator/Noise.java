package net.mega2223.aguaengine3d.graphics.objects.modeling.procedural.noisegenerator;

import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.mathematics.MathUtils;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public interface Noise {

    StringBuilder EXCP = new StringBuilder();

    float get(float x, float z);

    static BufferedImage NoiseToImage(Noise noise, int width, int height, float scale){
        return NoiseToImage(noise,width,height,0,0,scale);
    }
    static BufferedImage NoiseToImage(Noise noise, int width, int height, float displacementX, float displacementZ, float scale){
        return NoiseToImage(noise,width,height,displacementX,displacementZ,scale,false);
    }
    static BufferedImage NoiseToImage(Noise noise, int width, int height, float displacementX, float displacementZ, float scale, boolean normalize){
        BufferedImage output = new BufferedImage(width, height,BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = output.createGraphics(); graphics.setColor(Color.black);
        graphics.drawRect(0,0,width,height);
        float n = 1;
        if(normalize){
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    float x = ((float)i)*scale, z = ((float) j)*scale;
                    n = Math.max(Math.abs(noise.get(x+displacementX,z+displacementZ)),n);
                }
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float x = ((float)i)*scale, z = ((float) j)*scale;
                float v = noise.get(x+displacementX,z+displacementZ)/n;
                int col = Math.min(Math.max((int)(v*(255)),-255),255);
                col = col > 0 ? col << 8 : (-col);
                output.setRGB(i,j,col);
            }
        }
        return output;
    }

    static BufferedImage NoiseToImage(Noise noise, int width, int height, float displacementX, float displacementZ, float scale, float[] topColor, float[] bottomColor, boolean normalize){
        BufferedImage output = new BufferedImage(width, height,BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = output.createGraphics(); graphics.setColor(Color.black);
        graphics.drawRect(0,0,width,height);
        float n = 1; boolean alpha = topColor.length >= 4 && bottomColor.length >=4;
        if(normalize){
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    float x = ((float)i)*scale, z = ((float) j)*scale;
                    n = Math.max(Math.abs(noise.get(x+displacementX,z+displacementZ)),n);
                }
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float x = ((float)i)*scale, z = ((float) j)*scale;
                float v = noise.get(x+displacementX,z+displacementZ)/n;
                v = Math.max(Math.min(v,1),-1) * .5F + .5F;
                int col = 0X0;
                for (int c = 0; c < 3; c++) {
                    int act = (int) (MathUtils.linearInterpolation(bottomColor[2-c],topColor[2-c],v)*255);
                    col |= act << (c * 8);
                }
                col = alpha ? col | ((int) (MathUtils.linearInterpolation(bottomColor[3],topColor[3],v) * 255F) << 24) : col | 0XFF000000;
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
        if(maxX < minX){maxX += minX; minX = maxX - minX; maxX = maxX - minX;}// i am very clever
        if(maxZ < minZ){maxZ += minZ; minZ = maxZ - minZ; maxZ = maxZ - minZ;}
        int i = 0, rowSize = (int) Math.ceil(((maxX - minX)/step)+.001F);

        for (float x = minX; x < maxX; x+=step) {
            for (float z = minZ; z < maxZ; z+=step) {
                verticeList.add(x*xzScale); verticeList.add(noise.get(x,z)*yScale);
                verticeList.add(z*xzScale); verticeList.add(0F);

                if(z + step < maxZ && x + step < maxX){
                    indexList.add(i); indexList.add(i+1); indexList.add(i+rowSize);
                }
                if(z > minZ && x > minX){
                    indexList.add(i); indexList.add(i-1); indexList.add(i-rowSize);
                }
                i++;
            }
        }

        List<Integer> toClean = new ArrayList<>();

        for (int j = 0; j < indexList.size(); j+=3) {
            int i1 = indexList.get(j) * 4, i2 = indexList.get(j+1) * 4, i3 = indexList.get(j+2) * 4;
            if(Math.max(Math.max(i1,i2),i3) >= verticeList.size() || Math.min(i1,Math.min(i2,i3)) < 0) {
                toClean.add(j); toClean.add(j+1); toClean.add(j+2);
            }
        }
        toClean.sort((o1, o2) -> o2-o1);

        for (int j = 0; j < toClean.size(); j++) {
            indexList.remove((int)toClean.get(j));
        }


        float[] vertices = new float[verticeList.size()]; for (int j = 0; j < vertices.length; j++) {vertices[j] = verticeList.get(j);}
        int[] indices = new int[indexList.size()]; for (i = 0; i < indices.length; i++) {indices[i] = indexList.get(i);}EXCP.delete(0, Math.max(EXCP.length(), 0));
        Model model = new Model(vertices, indices, program);
        for (int j = 0; j < indices.length; j++) {
            if(indices[j]>=vertices.length / 4){throw new RuntimeException(j + ": " + indices[j]);}
        }
        float[] normals = model.getNormals();
        if(VectorTranslator.dotProduct(normals[0],normals[1],normals[2],0,1,0) < 0){
            VectorTranslator.flipArray(normals);
            model.setNormals(normals);
        }
        return model;
    }
}
