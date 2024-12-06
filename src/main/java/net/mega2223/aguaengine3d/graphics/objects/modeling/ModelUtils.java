package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModelUtils {

    private ModelUtils(){}
    private static final float[] bufferVec4 = new float[4];

    public static TexturedModel mergeModels(TexturedModel[] models, int texture){
        if(models.length == 0){return null;}
        return mergeModels(models,texture,models[0].getShader());
    }
    public static TexturedModel mergeModels(TexturedModel[] models, int texture, ShaderProgram program){
        if(models.length == 0){return null;}
        List<Float> verticeList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        List<Float> textureCoordList = new ArrayList<>();

        for (int m = 0; m < models.length; m++) {
            float[] modelVertices = models[m].vertices;
            int[] modelIndexes = models[m].indices;
            float[] modelTextureCoords = models[m].textureShift;
            int indexStartingPoint = verticeList.size()/4;
            translateVertices(modelVertices,models[m].coords);

            for (float modelVertex : modelVertices) {verticeList.add(modelVertex);}
            for (int modelIndex : modelIndexes){indexList.add(modelIndex+indexStartingPoint);}
            for (float textureCoord : modelTextureCoords){textureCoordList.add(textureCoord);}

        }
        //primitives and whatnot
        float[] vertices = new float[verticeList.size()];
        int[] indices = new int[indexList.size()];
        float[] textureCoords = new float[textureCoordList.size()];

        for(int i = 0; i < vertices.length;i++){vertices[i]=verticeList.get(i);}
        for(int i = 0; i < indices.length;i++){indices[i]=indexList.get(i);}
        for(int i = 0; i < textureCoords.length;i++){textureCoords[i]=textureCoordList.get(i);}

        return new TexturedModel(vertices,indices,textureCoords,program,texture);
    }

    public static Model mergeModels(Model[] models){
        return mergeModels(models,models[0].shader);
    }

    public static Model mergeModels(Model[] models, ShaderProgram program){
        if(models.length == 0){return null;}
        List<Float> verticeList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();

        for (int m = 0; m < models.length; m++) {
            float[] modelVertices = models[m].vertices;
            int[] modelIndexes = models[m].indices;
            int indexStartingPoint = verticeList.size()/4;
            translateVertices(modelVertices,models[m].coords);

            for (float modelVertex : modelVertices) {verticeList.add(modelVertex);}
            for (int modelIndex : modelIndexes){indexList.add(modelIndex+indexStartingPoint);}
        }
        float[] vertices = new float[verticeList.size()];
        int[] indices = new int[indexList.size()];

        for(int i = 0; i < vertices.length;i++){vertices[i]=verticeList.get(i);}
        for(int i = 0; i < indices.length;i++){indices[i]=indexList.get(i);}

        return new Model(vertices,indices,program);
    }

    public static TextureInterfaceComponent mergeComponents(TextureInterfaceComponent[] components, int texture){
        List<Float> verticeList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        List<Float> textureCoordList = new ArrayList<>();

        for (int m = 0; m < components.length; m++) {
            float[] modelVertices = components[m].vertices;
            int[] modelIndexes = components[m].indices;
            float[] modelTextureCoords = components[m].textureCoords;
            int indexStartingPoint = verticeList.size()/4;
            translateVertices(modelVertices,components[m].coords);

            for (float modelVertex : modelVertices) {verticeList.add(modelVertex);}
            for (int modelIndex : modelIndexes){indexList.add(modelIndex+indexStartingPoint);}
            for (float textureCoord : modelTextureCoords){textureCoordList.add(textureCoord);}

        }
        float[] vertices = new float[verticeList.size()];
        int[] indices = new int[indexList.size()];
        float[] textureCoords = new float[textureCoordList.size()];

        for(int i = 0; i < vertices.length;i++){vertices[i]=verticeList.get(i);}
        for(int i = 0; i < indices.length;i++){indices[i]=indexList.get(i);}
        for(int i = 0; i < textureCoords.length;i++){textureCoords[i]=textureCoordList.get(i);}
        //should I make an Interface that abranges InterfaceComponent AND TexturedModel?

        return new TextureInterfaceComponent(vertices,indices,textureCoords,texture,components[0].getAspectRatio());
    }

    public static void translateModel(Model model, float x, float y, float z){
        model.setVertices(translateVertices(model,x,y,z));
    }
    public static float[] translateVertices(Model model, float x, float y, float z){
        return translateVertices(model.vertices,x,y,z);
    }
    @SuppressWarnings("UnusedReturnValue") //why??????
    public static float[] translateVertices (float[] vertices, float[] vector){
        return translateVertices(vertices,vector[0],vector[1], vector[2]);
    }
    public static float[] translateVertices (float[] vertices, float x, float y, float z){
        vertices = vertices.clone();
        //it was all A MERE FACADE
        VectorTranslator.addToAllVectors(vertices,x,y,z);
        return vertices;
    }
    public static void scaleModel(Model model, float factor){
        scaleModel(model,factor,factor,factor);
    }
    public static void scaleModel(Model  model, float sX, float sY, float sZ){
        float[] vertices = model.getRelativeVertices();
        for (int i = 0; i < vertices.length; i+=4) {
            vertices[i]*=sX; vertices[i+1]*=sY; vertices[i+2]*=sZ;
        }
        model.setVertices(vertices);
    }

    /**Saves the object in wavefront format, does not consider normals*/
    public static void saveModel(TexturedModel model, String dir, String nameAndExtension) throws IOException {
        FileWriter stream = new FileWriter(dir + "\\" + nameAndExtension,false);
        float[] vertices = model.getRelativeVertices();
        float[] textureCoords = model.getTextureShift();
        int[] indices = model.getIndices();

        stream.write(" # Written by AguaEngine :) \n # Code and license available at https://github.com/Mega2223/AguaEngine3D\n\n # Vertices:\n");

        for (int i = 0; i < vertices.length; i+=4) {
            stream.write("v " + vertices[i] + " " + vertices[i+1] + " "+ vertices[i+2] + "\n");
        }
        stream.write("\n# Texture Coordinates:\n");
        for (int i = 0; i < textureCoords.length; i+=2) {
            stream.write("vt " + textureCoords[i] + " " + (1-textureCoords[i+1]) + "\n");
        }
        stream.write("\n# Faces:\n");
        for (int i = 0; i < indices.length; i+=3) {
            stream.write("f ");
            for (int j = 0; j < 3; j++) {
                int k = indices[i+j];
                stream.write((1+(k)) + "/" + (1+(k)));
                if(j!=2){stream.write(" ");}
            }
            stream.write("\n");
        }
        stream.close();
    }

    public static void translateAllVertices(float[] vertices, float x, float y, float z){
        for (int i = 0; i < vertices.length; i+=4) {
            vertices[i] += x; vertices[i+1] += y; vertices[i+2] += z;
        }
    }

    public static void scaleAllVertices(float[] vertices, float factor){
        for (int i = 0; i < vertices.length; i+=4) {
            vertices[i] *= factor; vertices[i+1] *= factor; vertices[i+2] *= factor;
        }
    }

    public static float[] generateNormals(Model model){
        float[] vertices = model.getRelativeVertices(); //TODO normals should have 4 elements
        float[] normals = generateNormals(vertices, model.getIndices());
        if(VectorTranslator.dotProduct(normals[0],normals[1],normals[2], vertices[0],vertices[1],vertices[2]) < 0){
            VectorTranslator.flipAllVectors(normals);
        }
        return normals;
    }

    public static float[] generateNormals(float[] vertices, int[] indices){
        float[] ret =  new float[vertices.length];
        //int[] connectedPrimitives = new int[vertices.length/4];
        float[][] primitiveNormals = new float[indices.length/3][3];

        //Figures out the normals for each triangle
        for (int i = 0; i < indices.length; i+= 3) {
            int v0loc = 4 * indices[i]; int v1loc = 4 * indices[i + 1]; int v2loc = 4 * indices[i + 2];
            if(v0loc > vertices.length || v1loc >= vertices.length || v2loc >= vertices.length){continue;}
            float v0x = vertices[v0loc]; float v0y = vertices[v0loc +1]; float v0z = vertices[v0loc +2];
            float v1x = vertices[v1loc]; float v1y = vertices[v1loc +1]; float v1z = vertices[v1loc +2];
            float v2x = vertices[v2loc]; float v2y = vertices[v2loc +1]; float v2z = vertices[v2loc +2];

            float vAx = v1x - v0x; float vAy = v1y - v0y; float vAz = v1z - v0z;
            float vBx = v2x - v0x; float vBy = v2y - v0y; float vBz = v2z - v0z;

            VectorTranslator.getCrossProduct(vAx, vAy, vAz, vBx, vBy, vBz, bufferVec4);
            VectorTranslator.normalize(bufferVec4);

            primitiveNormals[i/3][0] = bufferVec4[0];
            primitiveNormals[i/3][1] = bufferVec4[1];
            primitiveNormals[i/3][2] = bufferVec4[2];

            /*connectedPrimitives[indices[i]]++;
            connectedPrimitives[indices[i+1]]++;
            connectedPrimitives[indices[i+2]]++;*/
        }

        //Calculates the average vector between the normal of each connected primitive
        float[][] normalBuffer = new float[vertices.length/4][4];
        for (int j = 0; j < indices.length; j+=3) {
            int id = j/3;
            float[] normal = primitiveNormals[id];
            int loc1 = indices[j]; int loc2 = indices[j+1]; int loc3 = indices[j+2];
            if (loc3 >= normalBuffer.length){continue;}
            for (int i = 0; i < 3; i++) {
                normalBuffer[loc1][i] += primitiveNormals[id][i];
                normalBuffer[loc2][i] += primitiveNormals[id][i];
                normalBuffer[loc3][i] += primitiveNormals[id][i];
            }
        }

        for (int i = 0; i < vertices.length; i+=4) {
            int id = i/4;
            float xC = normalBuffer[id][0], yC =  normalBuffer[id][1], zC =  normalBuffer[id][2];
            float mag = (float) Math.sqrt(xC*xC+yC*yC+zC*zC);
            ret[i] = xC/mag; ret[i+1] = yC/mag; ret[i+2] = zC/mag;
        }

        /*for (int i = 0; i < connectedPrimitives.length; i++) {
            if(connectedPrimitives[i]!=0){
                System.out.println("There was a miscount at " + i + " while generating a model normal");
            }
        }*/
        return ret;
    }
    public static void figureOutLargestTriangle(Model mod){
        float[] vertices = mod.getRelativeVertices();
        int[] ind = mod.getIndices();
        int largestIndex = 0; float largestDist = 0;
        for (int i = 0; i < ind.length; i+=3) {
            int i0 = ind[i];int i1 = ind[i+1];int i2 = ind[i+2];
            float x0 = vertices[i0*4], y0 = vertices[i0*4+1], z0 = vertices[i0*4+2];
            float x1 = vertices[i1*4], y1 = vertices[i1*4+1], z1 = vertices[i1*4+2];
            float x2 = vertices[i2*4], y2 = vertices[i2*4+1], z2 = vertices[i2*4+2];
            float dist = (float) Math.abs(Math.sqrt((x1-x0)*(x1-x0)+(y1-y0)*(y1-y0)+(z1-z0)*(z1-z0)));
            boolean isLarger = dist > largestDist;
            largestIndex = isLarger ? i : largestIndex;
            largestDist = isLarger ? dist : largestDist;
            dist = (float) Math.abs(Math.sqrt((x2-x0)*(x2-x0)+(y2-y0)*(y2-y0)+(z2-z0)*(z2-z0)));
            isLarger = dist > largestDist;
            largestIndex = isLarger ? i : largestIndex;
            largestDist = isLarger ? dist : largestDist;
            dist = (float) Math.abs(Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)+(z1-z2)*(z1-z2)));
            isLarger = dist > largestDist;
            largestIndex = isLarger ? i : largestIndex;
            largestDist = isLarger ? dist : largestDist;
        }
        System.out.println("LARGEST TRIANGLE: ");
        int v1 = ind[largestIndex], v2 = ind[largestIndex + 1], v3 = ind[largestIndex + 2];
        int[] all = {v1,v2,v3};
        System.out.println(largestIndex/3 + ": " + v1 + ", " + v2 + ", " + v3);
        for (int i = 0; i < all.length; i++) {
            System.out.println("V"+i+": " + vertices[all[i]*4] + ", " + vertices[all[i]*4+1] + ", " + vertices[all[i]*4+2]);
        }
        System.out.println("LEN: " + largestDist);
        System.out.println("\n\n");
        //for (int i = 0; i < vertices.length; i+=4) {
        //    System.out.println(i/4 + ": " + vertices[i] + ", " + vertices[i+1] + ", " + vertices[i+2]);
        //}
    }

    public static void debugIndices(Model model){
        int[] indices = model.indices;
        float[] vertices = model.vertices;
        debugIndices(indices,vertices);
    }
    public static void debugIndices(int[] indices, float[] vertices){
        for (int i = 0; i < indices.length; i+=3) {
            System.out.println("Triangle " + i/3 + ": ");
            for (int j = 0; j < 3; j++) {
                int ind = indices[i + j]*4;
                System.out.print("Vertice " + ind/4 + ": ");
                System.out.println("x: " + vertices[ind] + " y: " + vertices[ind+1] + " z: " + vertices[ind + 2]);
            }
            System.out.println();
        }
    }

    public static void debugIndices(TextureInterfaceComponent model){
        debugIndices(model.indices,model.vertices,model.textureCoords);
    }

    public static void debugIndices(int[] indices, float[] vertices, float[] textureCoords){
        for (int i = 0; i < indices.length; i+=3) {
            System.out.println("Triangle " + i/3 + ": ");
            for (int j = 0; j < 3; j++) {
                int ind = indices[i + j]*4;
                System.out.print("Vertice " + ind/4 + ": ");
                System.out.println("x: " + vertices[ind] + " y: " + vertices[ind+1] + " z: " + vertices[ind + 2]);
                System.out.println("tx: " + textureCoords[ind/2] + " ty: " + textureCoords[ind/2+1]);

            }
            System.out.println();
        }
    }

    public static void rescaleModel(Model model, float scale){
        float[] verts = model.getRelativeVertices();
        for (int i = 0; i < verts.length; i++) {
            verts[i] *= scale;
        }
        model.setVertices(verts);
    }

    public static void rescaleModel(TextureInterfaceComponent model, float scale){
        float[] verts = model.getRelativeVertices();
        for (int i = 0; i < verts.length; i++) {
            verts[i] *= scale;
        }
        model.setVertices(verts);
    }

    private static final float[] cubeV = {
            -1F, 1F, 1F, 0F, -1F, -1F, 1F, 0F,
            -1F, 1F, -1F,0F, -1F, -1F, -1F, 0F,
            1F, 1F, 1F, 0F, 1F, -1F, 1F, 0F,
            1F, 1F, -1F, 0F, 1F, -1F, -1F, 0F
    };
    private static final int[] cubeI = {
            5, 3, 1, 3, 8, 4, 7, 6, 8, 2, 8, 6, 1, 4, 2, 5, 2, 6, 5, 7, 3, 3, 7, 8, 7, 5, 6, 2, 4, 8, 1, 3, 4, 5, 1, 2
    };

    public static Model plotPoints(float[] vertices, float radius){
        final int n = vertices.length / 4;
        float[] nVertices = new float[n * 8 * 4];
        int[] nIndices = new int[n * 12 * 3];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 32; j++) {
                nVertices[i * 32 + j] = cubeV[j] * radius + vertices[i * 4 + (j % 4)];
            }
        }
        for (int i = 0; i < nIndices.length ; i++) {
            nIndices[i] = cubeI[i%cubeI.length] + (cubeV.length / 4) * (i / cubeI.length);
        }
        VectorTranslator.debugVector(nVertices);
        VectorTranslator.debugVector(nIndices);
        Model model = new Model(nVertices, nIndices, null);
        ModelUtils.debugIndices(model);
        return model;
    }

    static {
        for (int i = 0; i < cubeI.length; i++) {cubeI[i]--;}
    }
}
