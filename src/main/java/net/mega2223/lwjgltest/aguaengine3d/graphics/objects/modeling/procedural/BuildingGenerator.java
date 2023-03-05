package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildingGenerator {

    private BuildingGenerator(){}

    public static void generateBuilding (int templateTexture,float scale){



    }

    public static TexturedModel genBlock(float scale, float sX, float sZ, float h, int texture){
        List<Float> vertices = new ArrayList();
        List<Float> textureCoords = new ArrayList();
        List<Integer> indices = new ArrayList();

        float[] wallTextureCoords = {0,0 , 0,.5f , .5f,0 , .5f,.5f};

        //constructs the walls that run in paralell with the z axis
        for (int x = 0; x < sX; x++) {
            for (int y = 0; y < h; y++) {
                float yT = y*scale, xT = x*scale, zT = sZ*scale;

                float[] walls = {
                        xT,yT,0,0 , xT,yT+scale,0,0 , xT+scale,yT,0,0 , xT+scale,yT+scale,0,0,
                        xT,yT,zT,0 , xT,yT+scale,zT,0 , xT+scale,yT,zT,0 , xT+scale,yT+scale,zT,0
                };
                int[] wallsIndices = {0,1,2,3,2,1 , 4,5,6,7,6,5};

                int indiceStart = vertices.size()/4;
                for(int i = 0; i < wallsIndices.length; i++){wallsIndices[i]+=indiceStart;}

                for(float act : walls){vertices.add(act);}
                for(int act : wallsIndices){indices.add(act);}


                for (int i = 0; i < wallTextureCoords.length; i++) {textureCoords.add(wallTextureCoords[i]);}
                for (int i = 0; i < wallTextureCoords.length; i++) {textureCoords.add(wallTextureCoords[i]);}

            }
        }
        //x axis pararell walls
        for (int z = 0; z < sZ; z++) {
            for (int y = 0; y < h; y++) {
                float yT = y*scale, xT = sX*scale, zT = z*scale;

                float[] walls = {
                        0,yT,zT,0 , 0,yT+scale,zT,0 , 0,yT,zT+scale,0 , 0,yT+scale,zT+scale,0,
                        xT,yT,zT,0 , xT,yT+scale,zT,0 , xT,yT,zT+scale,0 , xT,yT+scale,zT+scale,0,
                };
                int[] wallsIndices = {0,1,2,3,2,1 , 4,5,6,7,6,5};

                int indiceStart = vertices.size()/4;
                for(int i = 0; i < wallsIndices.length; i++){wallsIndices[i]+=indiceStart;}

                for(float act : walls){vertices.add(act);}
                for(int act : wallsIndices){indices.add(act);}


                for (int i = 0; i < wallTextureCoords.length; i++) {textureCoords.add(wallTextureCoords[i]);}
                for (int i = 0; i < wallTextureCoords.length; i++) {textureCoords.add(wallTextureCoords[i]);}

            }
        }

        //can't use the toArray method to get the primitives :(
        float[] verticesArray = new float[vertices.size()];
        int[] indicesArray = new int[indices.size()];
        float[] textureCoordArray = new float[textureCoords.size()];

        for(int i = 0; i < verticesArray.length; i++){verticesArray[i] = vertices.get(i);}
        for(int i = 0; i < indicesArray.length; i++){indicesArray[i] = indices.get(i);}
        for(int i = 0; i < textureCoordArray.length; i++){textureCoordArray[i] = textureCoords.get(i);}

        TexturedModel result = new TexturedModel(
                verticesArray,
                indicesArray,
                textureCoordArray,
                texture
        );

        return result;
    };

}
