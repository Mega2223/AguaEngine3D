package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;

import java.util.ArrayList;
import java.util.List;

public class StructureUtils {

    public static TexturedModel generateWallModel(float sx, float sz, float ex, float ez, float top, float base, int texture){
        float[] coords = {sx,base,sz,0 , sx,top,sz,0 , ex,base,ez,0 , ex,top,ez,0};
        int[] indexes = {0,1,2, 2,3,1};

        float xD = ex - sx;
        float zD = ez - sz;
        float r = (float) Math.sqrt((xD*xD)+(zD*zD));
        float dif = top-base;

        float[] textureShift = {0,0 , 0,-dif , r,0 , r,-dif};
        TexturedModel ret = new TexturedModel(
                coords,indexes,textureShift,texture
        );
        return ret;
    }


    /**Generates a set of triangles corresponding to a building given the instructions*/
    public static Model genBuildingModel(float bottom, float top, float begX, float endX, float begZ, float endZ, ShaderProgram shaderP){

        float[] retVetices = {
                begX,bottom,begZ,0,
                begX,bottom,endZ,0,
                endX,bottom,endZ,0,
                endX,bottom,begZ,0,
                begX,top,begZ,0,
                begX,top,endZ,0,
                endX,top,endZ,0,
                endX,top,begZ,0
        };
        int[] retIndicies = {
                0,1,2,3,2,0, //floor
                4,5,6,7,6,4, //ceiling
                0,1,4,4,5,1,
                1,2,5,5,6,2,
                3,2,6,6,7,3,
                0,3,7,7,4,0
        };

        return new Model(retVetices,retIndicies,shaderP);
    }

    public static List<Model> elongateSurface(Model model, float bas){
        ArrayList<Model> ret = new ArrayList();
        float[] ver = model.getRelativeVertices();
        int[] indices = model.getIndexes();

        model.setVertices(ver);

        int tex = TextureManager.loadTexture(Utils.TEXTURES_DIR+"\\TijoloSuave.png");
        for (int i = 0; i < indices.length; i+=3) {
            float[][] tAct = new float[3][];
            for (int j = 0; j < 3; j++) {
                int act = indices[i+j]*4;
                float[] vAct = {ver[act],ver[act+1],ver[act+2],ver[act+3]};
                tAct[j] = vAct;
            }
            ret.add(StructureUtils.generateWallModel(tAct[0][0],tAct[0][2],tAct[1][0],tAct[1][2],0,bas,tex));
            ret.add(StructureUtils.generateWallModel(tAct[1][0],tAct[1][2],tAct[2][0],tAct[2][2],0,bas,tex));
            ret.add(StructureUtils.generateWallModel(tAct[2][0],tAct[2][2],tAct[0][0],tAct[0][2],0,bas,tex));
        }
        return ret;
    }

    public static float[] repeat (float[] vertices,int aX,int aY, int aZ, boolean mirror){

        float[] min = new float[3];
        float[] max = new float[3];
        float[] ran = new float[4];


        for (int i = 0; i < vertices.length; i+=4) {
            for (int j = 0; j < 3; j++) {
                if (min[j]>vertices[i+j]){min[j]=vertices[i+j];}
                if (max[j]<vertices[i+j]){max[j]=vertices[i+j];}
            }
        }
        for (int i = 0; i < 3; i++) {
            ran[i] = max[i]-min[i];
        }

        float[] start = {0,0,0,0};
        if(mirror){for (int i = 0; i < start.length; i++) {start[i]-=ran[i]/2;}}

        List<Float> newTriangles = new ArrayList<>();

        //nightmare fuel
        for (int xI = 0; xI < aX; xI++) {
            for (int yI = 0; yI < aY; yI++) {
                for (int zI = 0; zI < aZ; zI++) {
                    for (int i = 0; i < vertices.length; i+=4) {
                        for (int j = 0; j < 4; j++) {
                            newTriangles.add(vertices[i+j]+start[j]);
                        }

                    }
                    start[2]+=ran[2];
                }
                start[1]+=ran[1];
            }
            start[0]+=ran[0];
        }

        float[] newVertices = new float[newTriangles.size()];
        for (int i = 0; i < newVertices.length; i++) {
            newVertices[i]=newTriangles.get(i);
        }
        return newVertices;
    }

}
