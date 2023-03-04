package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;
import net.mega2223.lwjgltest.aguaengine3d.graphics.utils.TextureManager;
import net.mega2223.lwjgltest.aguaengine3d.logic.objects.RenderableObject;
import net.mega2223.lwjgltest.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.lwjgltest.aguaengine3d.misc.Utils;

import java.util.ArrayList;
import java.util.List;

public class StructureUtils {

    public static TexturedModel generateWall(float sx, float sz, float ex, float ez,float top,float base, int texture){
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
            ret.add(StructureUtils.generateWall(tAct[0][0],tAct[0][2],tAct[1][0],tAct[1][2],0,bas,tex));
            ret.add(StructureUtils.generateWall(tAct[1][0],tAct[1][2],tAct[2][0],tAct[2][2],0,bas,tex));
            ret.add(StructureUtils.generateWall(tAct[2][0],tAct[2][2],tAct[0][0],tAct[0][2],0,bas,tex));
        }
        return ret;
    }

    public static Model repeat (Model mod,int aX,int aY, int aZ, boolean mirror){
        
        float[] vertices = mod.getRelativeVertices();
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

        return null;//fixme


    }

}
