package net.mega2223.aguaengine3d.mathematics;

public interface Transform {
    void transform(float x, float y, float z, float[] dest);
    void reverse(float x, float y, float z, float[] dest);

    default void transform(float[] vec3, float[] dest){
        transform(vec3[0],vec3[1],vec3[2],dest);
    }
    default void reverse(float[] vec3, float[] dest){
        reverse(vec3[0],vec3[1],vec3[2],dest);
    }
}
