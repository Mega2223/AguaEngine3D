package net.mega2223.aguaengine3d.graphics.objects.modeling;

import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModelUtils {

    private ModelUtils(){}

    public static TexturedModel mergeModels(TexturedModel[] models, int texture){
        return mergeModels(models,texture,models[0].getShader());
    }
    public static TexturedModel mergeModels(TexturedModel[] models, int texture, ShaderProgram program){
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

    public static InterfaceComponent mergeComponents(InterfaceComponent[] components, int texture){
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

        return new InterfaceComponent(vertices,indices,textureCoords,texture,components[0].getAspectRatio());
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

    public static void debugIndices(InterfaceComponent model){
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

}