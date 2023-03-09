package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling;

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

}
