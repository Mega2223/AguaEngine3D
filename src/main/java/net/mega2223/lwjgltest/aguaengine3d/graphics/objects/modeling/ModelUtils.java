package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling;

import net.mega2223.lwjgltest.aguaengine3d.mathematics.VectorTranslator;

import java.util.ArrayList;
import java.util.List;

public class ModelUtils {

    private ModelUtils(){}

    public static TexturedModel mergeModels(TexturedModel[] models, int texture){
        List<Float> verticeList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        List<Float> textureCoordList = new ArrayList<>();

        for (int m = 0; m < models.length; m++) {
            float[] modelVertices = models[m].vertices;
            int[] modelIndexes = models[m].indexes;
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

        return new TexturedModel(vertices,indices,textureCoords,texture);
    }

    public static void translateModel(Model model, float x, float y, float z){
        model.setVertices(translateVertices(model,x,y,z));
    }
    public static float[] translateVertices(Model model, float x, float y, float z){
        return translateVertices(model.vertices,x,y,z);
    }
    public static float[] translateVertices (float[] vertices, float[] vector){
        return translateVertices(vertices,vector[0],vector[1], vector[2]);
    }
    public static float[] translateVertices (float[] vertices, float x, float y, float z){
        vertices = vertices.clone();
        //it was all A MERE FACADE
        VectorTranslator.addToAllVectors(vertices,x,y,z);
        return vertices;
    }
}
