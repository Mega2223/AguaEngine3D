package net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.procedural;

import net.mega2223.lwjgltest.aguaengine3d.graphics.objects.modeling.TexturedModel;

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

}
