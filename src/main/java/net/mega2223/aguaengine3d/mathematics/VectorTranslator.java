package net.mega2223.aguaengine3d.mathematics;

public class VectorTranslator {

    public static void scaleVector(float[] vector, float factor,int start){
        for (int i = 0; i < 3; i++) {
            vector[i+start]*=factor;
        }
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

    public static void debugVector (float[] vec){
        System.out.print("v: [");
        for (int i = 0; i < vec.length - 1; i++) {
            System.out.print(vec[i] + ",");
        }
        System.out.print(vec[vec.length-1]+"]\n");
    }
}
