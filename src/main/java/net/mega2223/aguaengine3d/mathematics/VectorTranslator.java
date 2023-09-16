package net.mega2223.aguaengine3d.mathematics;

import java.util.Arrays;

public class VectorTranslator {

    private static final float[] bufferVector = new float[4];

    public static void scaleVector(float[] vector, float factor,int start){
        for (int i = 0; i < 3; i++) {
            vector[i+start]*=factor;
        }
    }

    public static void scaleVector(float[] vector, float factor){
        scaleVector(vector,factor,0);
    }

    public static void scaleAllVectors(float[] vectors, float factor){
        for (int i = 0; i < vectors.length; i+=4) {
            for (int j = 0; j < 3; j++) {
                vectors[i+j]*=factor;
            }
        }
    }

    public static void scaleAllVectors(float[] vectors, float[] scaleVector){
        scaleAllVectors(vectors,scaleVector[0],scaleVector[1],scaleVector[2]);
    }

    public static void scaleAllVectors(float[] vectors, float x, float y, float z){
        for (int i = 0; i < vectors.length; i+=4) {
            vectors[i]*=x;
            vectors[i+1]*=y;
            vectors[i+2]*=z;
        }
    }

    public static void divideVector(float[] vector, float factor){
        for (int i = 0; i < vector.length; i++) {
            vector[i]/=factor;
        }
    }

    public static float getDistance(float[] v1, float[] v2){
        float sum = 0;
        for (int i = 0; i < v1.length; i++) {
            sum+=(v2[i]-v1[i])*(v2[i]-v1[i]);
        }
        return (float) Math.sqrt(sum);
    }

    public static void addToAllVectors(float[] vectors, float[] scaleVector){
        addToAllVectors(vectors,scaleVector[0],scaleVector[1],scaleVector[2]);
    }

    public static void addToAllVectors(float[] vectors, float x, float y, float z){
        for (int i = 0; i < vectors.length; i+=4) {
            vectors[i]+=x;
            vectors[i+1]+=y;
            vectors[i+2]+=z;
        }
    }

    public static void addToVector(float[] vector, float[] vector2){
        for (int i = 0; i < vector.length; i++) {
            vector[i]+=vector2[i];
        }
    }

    public static void addToVector(float[] vector, float x, float y, float z){
        vector[0]+=x;
        vector[1]+=y;
        vector[2]+=z;
    }

    public static void subtractFromVector(float[] v1, float[] v2){
        subtractFromVector(v1,v2,v1);
    }

    public static void subtractFromVector(float[] v1, float[] v2, float[] dest){
        subtractFromVector(v1[0],v1[1],v1[2],v2[0],v2[1],v2[2],dest);
    }

    public static void subtractFromVector(float x1, float y1, float z1, float x2, float y2, float z2, float[] dest){
        dest[0] = x1 - x2;
        dest[1] = y1 - y2;
        dest[2] = z1 - z2;
    }

    public static void flipVector(float[] vector){
        vector[0]=-vector[0];
        vector[1]=-vector[1];
        vector[2]=-vector[2];
    }

    public static void getCrossProduct(float[] vector, float[] vector2){
        getCrossProduct(bufferVector,vector[0],vector[1],vector[2],vector2[0],vector2[1],vector2[2]);
        System.arraycopy(bufferVector,0,vector,0,3);
    }

    public static void getCrossProduct(float[] vector, float[] vector2, float[] result){
        getCrossProduct(result,vector[0],vector[1],vector[2],vector2[0],vector2[1],vector2[2]);
    }

    public static void getCrossProduct(float[] vector, float x1,float y1,float z1,float x2,float y2,float z2){
        vector[0] = y1*z2-z1*y2;
        vector[1] = z1*x2-x1*z2;
        vector[2] = x1*y2-y1*x2;
    }

    public static float getMagnitude(float[] vec3){
        return (float) Math.sqrt(vec3[0]*vec3[0]+vec3[1]*vec3[1]+vec3[2]*vec3[2]);
    }

    public static float getMagnitude(float x, float y, float z){
        return (float) Math.sqrt(x*x+y*y+z*z);
    }
    
    public static float getScalarProduct(float[] vec3, float[] vec32){
        return vec3[0]*vec32[0]+vec3[1]*vec32[1]+vec3[2]*vec32[2];
    }
    public static float getScalarProduct(float x1,float y1,float z1,float x2,float y2,float z2){
        return x1*x2+y1*y2+z1*z2;
    }

    public static void normalizeVector(float[] vec3){
        float magnitude = getMagnitude(vec3);
        if(magnitude == 0){return;}
        divideVector(vec3, magnitude);
    }

    public static float getAngleBetweenVectors(float[] vec, float[] vec2){
        //TODO: test this lol
        float m1 = getMagnitude(vec), m2 = getMagnitude(vec2);
        float x1 = vec[0]/m1, y1 = vec[1]/m1, z1 = vec[2]/m1;
        float x2 = vec2[0]/m2, y2 = vec2[1]/m2, z2 = vec2[2]/m2;
        float scalar = getScalarProduct(x1,y1,z1,x2,y2,z2);
        return (float) Math.acos(scalar);
    }

    public static void debugVector (float[] vec){
        System.out.print("v: [");
        for (int i = 0; i < vec.length - 1; i++) {
            System.out.print(vec[i] + ",");
        }
        System.out.print(vec[vec.length-1]+"]\n");
    }
}
