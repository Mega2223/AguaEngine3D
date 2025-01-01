package net.mega2223.aguaengine3d.mathematics;

import net.mega2223.aguaengine3d.misc.annotations.Modified;

import java.util.Arrays;
import java.util.Locale;

public class VectorTranslator {

    static final float[] buffer1 = new float[4];
    static final float[] buffer2 = new float[4];
    static final float[] buffer3 = new float[4];
    private static final float PI2 = (float) (2 * Math.PI);

    public static void scaleVector(@Modified float[] vector, float factor, int start){
        for (int i = 0; i < 3; i++) {
            vector[i+start]*=factor;
        }
    }

    public static void scaleVector(@Modified float[] vector, float factor){
        scaleVector(vector,factor,0);
    }

    public static void divideVector(@Modified float[] vector, float factor){
        for (int i = 0; i < 3; i++) {
            vector[i]/=factor;
        }
    }

    public static float getDistance(float[] v1, float[] v2){
        float sum = 0;
        for (int i = 0; i < 3; i++) {
            float v = v2[i] - v1[i];
            sum+= v * v;
        }
        return (float) Math.sqrt(sum);
    }

    public static float getDistance(float x1, float y1, float z1, float x2, float y2, float z2){
        float x = x2 - x1, y = y2 - y1, z = z2 - z1;
        return (float) Math.sqrt(x*x+y*y+z*z);
    }

    public static void addToVector(@Modified float[] vector, float[] vector2){
        for (int i = 0; i < 3; i++) {
            vector[i]+=vector2[i];
        }
    }

    public static void addToVector(float x, float y, float z, @Modified float[] vector){
        vector[0]+=x;
        vector[1]+=y;
        vector[2]+=z;
    }

    public static void subtractFromVector(@Modified float[] v1, float[] v2){
        subtractFromVector(v1,v2,v1);
    }

    public static void subtractFromVector(float[] v1, float[] v2, @Modified float[] result){
        subtractFromVector(v1[0],v1[1],v1[2],v2[0],v2[1],v2[2],result);
    }

    public static void subtractFromVector(float x1, float y1, float z1, float x2, float y2, float z2, @Modified float[] result){
        result[0] = x1 - x2;
        result[1] = y1 - y2;
        result[2] = z1 - z2;
    }

    public static void flipVector(@Modified float[] vector){
        vector[0]=-vector[0];
        vector[1]=-vector[1];
        vector[2]=-vector[2];
    }

    public static void getCrossProduct(@Modified float[] vector, float[] vector2){
        getCrossProduct(vector[0], vector[1], vector[2], vector2[0], vector2[1], vector2[2], buffer1);
        System.arraycopy(buffer1,0,vector,0,3);
    }

    public static void getCrossProduct(float[] vector, float[] vector2, @Modified float[] result){
        getCrossProduct(vector[0], vector[1], vector[2], vector2[0], vector2[1], vector2[2], result);
    }

    public static void getCrossProduct(float x1, float y1, float z1, float x2, float y2, float z2, @Modified float[] result){
        result[0] = y1*z2-z1*y2;
        result[1] = z1*x2-x1*z2;
        result[2] = x1*y2-y1*x2;
    }

    public static float getMagnitude(float[] vec3){
        return (float) Math.sqrt(vec3[0]*vec3[0]+vec3[1]*vec3[1]+vec3[2]*vec3[2]);
    }

    public static float getMagnitude(float x, float y, float z){
        return (float) Math.sqrt(x*x+y*y+z*z);
    }
    
    public static float get2DDotProduct(float[] vec, float[] vec2){
        return vec[0]*vec2[0] + vec[1]*vec2[1];
    }
    
    public static float dotProduct(float[] vec3, float[] vec32){
        return vec3[0]*vec32[0]+vec3[1]*vec32[1]+vec3[2]*vec32[2];
    }
    
    public static float dotProduct(float x1, float y1, float z1, float x2, float y2, float z2){
        return x1*x2+y1*y2+z1*z2;
    }

    public static void normalize(@Modified float[] vector){
        float magnitude = getMagnitude(vector);
        if(magnitude == 0){return;}
        divideVector(vector,magnitude);
    }

    public static float getAngleBetweenVectors(float[] vecA, float[] vecB){
        float m1 = getMagnitude(vecA), m2 = getMagnitude(vecB);
        float x1 = vecA[0], y1 = vecA[1], z1 = vecA[2];
        float x2 = vecB[0], y2 = vecB[1], z2 = vecB[2];
        float dot = dotProduct(x1,y1,z1,x2,y2,z2);

        double angle = Math.acos(dot / (m1 * m2));
//        System.out.printf(Locale.US,
//                "ANGLE BETWEEN (%.2f,%.2f,%.2f) AND (%.2f,%.2f,%.2f): %.2f\n",
//                vecA[0],vecA[1],vecA[2],vecB[0],vecB[1],vecB[2],Math.toDegrees(angle));
        return (float) angle;
    }

    /**Gets the axis angle rotation which represents the angle difference between these vectors
     * FIXME: incrivelmente impreciso*/
    public static void getAxisAngle(float[] v3a, float[] v3b, @Modified float[] dest){
        Arrays.fill(dest,0);
//        float magA = VectorTranslator.getMagnitude(v3a), magB = VectorTranslator.getMagnitude(v3b);
        float ang = getAngleBetweenVectors(v3a,v3b);
        getCrossProduct(v3a,v3b,dest);
        normalize(dest);
        scaleVector(dest, ang);
//        VectorTranslator.debugVector("AXIS ",dest); AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
    }

    //**Rotates a vector given an axis-angle*/
    public static void rotateAlongAxis(float[] vec3, float[] axis, @Modified float[] dest){
        Arrays.fill(dest,0);
        float ang = getMagnitude(axis) % PI2;
        if(ang == 0){ System.arraycopy(vec3,0,dest,0,3); return;}
        float s = (float) Math.sin(ang), C = 1F - (float) Math.cos(ang);

        buffer1[0] = axis[0] / ang; buffer1[1] = axis[1] / ang;buffer1[2] = axis[2] / ang; buffer1[3] = 0;
        //b1 = e    vec3 = v
        getCrossProduct(buffer1,vec3,buffer2); // b2 = (e X v)
        getCrossProduct(buffer1,buffer2,buffer3); // b3 = e X (e X v)
        scaleVector(buffer3,C); // b3 = ( 1 - cos(T) ) (e X (e X v))
        scaleVector(buffer2,s); // b2 = sin(T) (e X v)

        System.arraycopy(vec3,0,dest,0,3);
        addToVector(dest,buffer2);
        addToVector(dest,buffer3);
    }

    public static void getRotationRadians(float[] quaternion, @Modified float[] result){
        // FIXME eu não sei qual o sistema que essa rotação usa
        double sinr_cosp = 2 * (quaternion[0] * quaternion[1] + quaternion[2] * quaternion[3]);
        double cosr_cosp = 1 - 2 * (quaternion[1] * quaternion[1] + quaternion[2] * quaternion[2]);
        result[0] = (float) Math.atan2(sinr_cosp, cosr_cosp);

        double sinp = Math.sqrt(1 + 2 * (quaternion[0] * quaternion[2] - quaternion[1] * quaternion[3]));
        double cosp = Math.sqrt(1 - 2 * (quaternion[0] * quaternion[2] - quaternion[1] * quaternion[3]));
        result[1] = (float) (2 * Math.atan2(sinp, cosp) - Math.PI / 2);

        double siny_cosp = 2 * (quaternion[0] * quaternion[3] + quaternion[1] * quaternion[2]);
        double cosy_cosp = 1 - 2 * (quaternion[2] * quaternion[2] + quaternion[3] * quaternion[3]);
        result[2] = (float) Math.atan2(siny_cosp, cosy_cosp);
    }

    public static void getRotationRadians(float w, float x, float y , float z, @Modified float[] ret){
        // FIXME eu não sei qual o sistema que essa rotação usa
        double sinr_cosp = 2 * (w * x + y * z);
        double cosr_cosp = 1 - 2 * (x * x + y * y);
        ret[0] = (float) Math.atan2(sinr_cosp, cosr_cosp);

        float v = 2 * (w * y - x * z);
        double sinp = Math.sqrt(1 + v);
        sinp = Double.isNaN(sinp) ? 0 : sinp;
        double cosp = Math.sqrt(1 - v);
        cosp = Double.isNaN(cosp) ? 0 : cosp;
        ret[1] = (float) (2 * Math.atan2(sinp, cosp) - Math.PI / 2);

        double siny_cosp = 2 * (w * z + x * y);
        double cosy_cosp = 1 - 2 * (y * y + z * z);
        ret[2] = (float) Math.atan2(siny_cosp, cosy_cosp);
    }

    public static void scaleAllVectors(@Modified float[] vectors, float factor){
        for (int i = 0; i < vectors.length; i+=4) {
            for (int j = 0; j < 3; j++) {
                vectors[i+j]*=factor;
            }
        }
    }

    public static void scaleAllVectors(@Modified float[] vectors, float[] scaleFactor){
        scaleAllVectors(vectors,scaleFactor[0],scaleFactor[1],scaleFactor[2]);
    }

    public static void scaleAllVectors(@Modified float[] vectors, float x, float y, float z){
        for (int i = 0; i < vectors.length; i+=4) {
            vectors[i]*=x;
            vectors[i+1]*=y;
            vectors[i+2]*=z;
        }
    }

    public static void addToAllVectors(@Modified float[] vectors, float[] scaleVector){
        addToAllVectors(vectors,scaleVector[0],scaleVector[1],scaleVector[2]);
    }

    public static void addToAllVectors(@Modified float[] vectors, float x, float y, float z){
        for (int i = 0; i < vectors.length; i+=4) {
            vectors[i]+=x;
            vectors[i+1]+=y;
            vectors[i+2]+=z;
        }
    }

    public static void flipAllVectors(@Modified float[] vectors){
        for (int i = 0; i < vectors.length; i+=4) {
            vectors[i]=-vectors[i];
            vectors[i+1]=-vectors[i+1];
            vectors[i+2]=-vectors[i+2];
        }
    }

    public static void flipArray(@Modified float[] array){
        for (int i = 0; i < array.length; i++) {
            array[i]=-array[i];
        }
    }

    public static void debugVector (String prefix, float... vec){
        System.out.print(prefix + ": [");
        for (int i = 0; i < vec.length - 1; i++) {
            System.out.print(vec[i] + ",");
        }
        System.out.print(vec[vec.length-1]+"]\n");
    }

    public static void debugVector (float... vec){
        System.out.print("v: [");
        for (int i = 0; i < vec.length; i++) {
            System.out.printf(Locale.US,"%2.2f ",vec[i]);
//            if(i + 1 >= vec.length){System.out.println(",");}
        }
        System.out.print("]\n");
    }

    public static void debugV4(float... vec){
        System.out.print("[");
        for (int i = 0; i < vec.length; i+=4) {
            System.out.print("[");
            for (int j = 0; j < 4; j++) {
                System.out.printf(Locale.US,"%2.2f ",vec[i+j]);
            }
            System.out.print("] ");
            if(i + 1 >= vec.length){System.out.println(",");}
        }
        System.out.print("]\n");
    }

    public static void debugVector (int... vec){
        System.out.print("v: [");
        for (int i = 0; i < vec.length - 1; i++) {
            System.out.print(vec[i] + ",");
        }
        System.out.print(vec[vec.length-1]+"]\n");
    }

    public static void debugVector (String prefix, int... vec){
        System.out.print(prefix + ": [");
        for (int i = 0; i < vec.length - 1; i++) {
            System.out.print(vec[i] + ",");
        }
        System.out.print(vec[vec.length-1]+"]\n");
    }



}
