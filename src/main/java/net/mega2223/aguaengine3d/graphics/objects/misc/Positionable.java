package net.mega2223.aguaengine3d.graphics.objects.misc;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.misc.annotations.Modified;

public interface Positionable extends Renderable {

    float x(); float y(); float z();
    void setCoords(float x, float y, float z);
    default void setRotationMatrix(float[] rotationM4){
        getShader().setRotationMatrix(rotationM4);
    }

    default void setCoords(float[] coords){setCoords(coords[0],coords[1],coords[2]);}
    default void getCoords(@Modified float[] result){result[0] = x(); result[1] = y(); result[2] = z();}
}
