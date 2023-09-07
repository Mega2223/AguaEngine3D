package net.mega2223.lwjgltest.aguaengine3d.mathematics;

import java.util.Arrays;

@SuppressWarnings("unused") //"Aqui seu programador IDIOTA o método está INUTILIZADO, apague IMEDIATAMENTE"

/**Class that stores handmade math calculations for matrix translation and stuff
 * Seems like LWJGL has libraries that do exactly what i'm trying to do, but i'm just too deep to care now
 * Plus it's pretty cool doing your own stuff and seeing how it does not work the way you intended*/
public class MatrixTranslator {

    public static final int ISOMETRIC_PROJECTION = 0;
    public static final int WEAK_PERSPECTIVE_PROJECTION = 1;
    public static final int PSEUDO_PERSPERCTIVE_PROJECTION = 2;
    protected static final int TRUE_PERSPECTIVE_PROJECTION = 3;
    protected static final int WEAKER_PERSPECTIVE_PROJECTION = 4; //i will not tolerate having this, so [fixme]


    @Deprecated
    public static void projectVec3(float[] vec3, int startingPoint, float[] projectionPoint, final int projectionAlg) {
        float fieldOfView = 90;

        switch (projectionAlg) {

            case ISOMETRIC_PROJECTION:
                vec3[startingPoint] += projectionPoint[0];
                vec3[startingPoint + 1] += projectionPoint[1];
                vec3[startingPoint + 2] += projectionPoint[2];

                return;
            case WEAK_PERSPECTIVE_PROJECTION:

                vec3[startingPoint] += projectionPoint[0];
                vec3[startingPoint + 1] += projectionPoint[1];
                vec3[startingPoint + 2] += projectionPoint[2];

                System.out.println("");
                System.out.println(vec3[startingPoint + 2]);

                float mult = fieldOfView / (fieldOfView + vec3[startingPoint + 2]);
                System.out.println(fieldOfView + "/(" + fieldOfView + "+" + vec3[startingPoint + 2] + ") =" + mult);

                vec3[startingPoint] *= mult;
                vec3[startingPoint + 1] *= mult;
                return;
            case WEAKER_PERSPECTIVE_PROJECTION:
                vec3[startingPoint] += projectionPoint[0];
                vec3[startingPoint + 1] += projectionPoint[1];
                vec3[startingPoint + 2] += projectionPoint[2];

                vec3[startingPoint] /= -vec3[startingPoint + 2];
                vec3[startingPoint + 1] /= -vec3[startingPoint + 2];
                return;
            case TRUE_PERSPECTIVE_PROJECTION:
                float near = .01f;
                float far = 1000f;


                return;

        }
    }

    public static void translateVector(float[] vector, float[][] matrix) {
        if (matrix.length != 4 || vector.length != 4) {
            throw new UnsupportedOperationException();
        }
        //this is optimal I guess
        for (int i = 0; i < 4; i++) {
            vector[i] = (matrix[i][0] * vector[0]) + (matrix[i][1] * vector[1]) + (matrix[i][2] * vector[2]) + (matrix[i][3] * vector[3]);
        }
    }

    public static void translateVector(float[] vector, float[] matrix) {
        if (matrix.length != 16) {
            throw new UnsupportedOperationException();
        }
        //this is optimal I guess
        for (int i = 0; i < 4; i++) {
            int i1 = i * 4;
            vector[i] = (matrix[i1] * vector[0]) + (matrix[i1 + 1] * vector[1]) + (matrix[i1 + 2] * vector[2]) + (matrix[i1 + 3] * vector[3]);
        }
    }

    public static void translateVectors(float[] vectors, float[] matrix) {
        if (matrix.length != 16) {
            throw new UnsupportedOperationException();
        }

        for (int g = 0; g < vectors.length; g += 4) {
            for (int i = 0; i < 4; i++) {
                int i1 = i * 4;
                vectors[i + g] = (matrix[i1] * vectors[g]) + (matrix[i1 + 1] * vectors[g + 1]) + (matrix[i1 + 2] * vectors[g + 2]) + (matrix[i1 + 3] * vectors[g + 3]);
            }
        }
        return;
    }

    public void rotatePolygons(float[][] polygons, float[] rotationRadians) {
        for (int i = 0; i < polygons.length; i++) {
            rotatePolygon(polygons[i], rotationRadians);
        }
        return;
    }

    public void rotatePolygon(float[] polygon, float rotationRadians[]) {
        for (int j = 0; j < polygon.length; j += 4) {
            float rot[] = polygon.clone();
            MatrixTranslator.rotateVector3(polygon, rotationRadians[0], rotationRadians[1], rotationRadians[2], j);
        }
    }

    public static void rotateVector3(float[] vec3, double rX, double rY, double rZ) {
        rotateVector3(vec3, rX, rY, rZ, 0, 0, 0);
    }

    public static void rotateVector3(float[] vec3, double rX, double rY, double rZ, int startIndex) {
        rotateVector3(vec3, rX, rY, rZ, 0, 0, 0, startIndex);
    }

    public static void rotateVector3(float[] vec3, double rX, double rY, double rZ, float aX, float aY, float aZ) {
        rotateVector3(vec3, rX, rY, rZ, aX, aY, aZ, 0);
    }

    public static void rotateVector3(float[] vec3, double rX, double rY, double rZ, float aX, float aY, float aZ, int startIndex) {
        float x = vec3[startIndex];
        float y = vec3[startIndex + 1];
        float z = vec3[startIndex + 2];
        rotateVector3(vec3, x, y, z, rX, rY, rZ, aX, aY, aZ, startIndex);
        //System.out.println("("+x+","+y+","+z+") -> ("+vec3[0]+","+vec3[1]+","+vec3[2]+")");
    }

    public static void rotateVector3(float[] vec3, float x, float y, float z, double rX, double rY, double rZ) {
        rotateVector3(vec3, x, y, z, rX, rY, rZ, 0, 0, 0, 0);
    }

    public static void rotateVector3(float[] vec3, float x, float y, float z, double rX, double rY, double rZ, float aX, float aY, float aZ, int startIndex) {

        if (rX == Float.POSITIVE_INFINITY || rX == Float.NEGATIVE_INFINITY) {
            rX = 0;
        }
        if (rY == Float.POSITIVE_INFINITY || rY == Float.NEGATIVE_INFINITY) {
            rY = 0;
        }
        if (rZ == Float.POSITIVE_INFINITY || rZ == Float.NEGATIVE_INFINITY) {
            rZ = 0;
        }

        //pain
        while (rX < 0) {
            rX += Math.PI * 2;
        }
        while (rY < 0) {
            rY += Math.PI * 2;
        }
        while (rZ < 0) {
            rZ += Math.PI * 2;
        }
        while (rX >= Math.PI * 2) {
            rX -= Math.PI * 2;
        }
        while (rY >= Math.PI * 2) {
            rY -= Math.PI * 2;
        }
        while (rZ >= Math.PI * 2) {
            rZ -= Math.PI * 2;
        }

        double cosX = Math.cos(rX);
        double cosY = Math.cos(rY);
        double cosZ = Math.cos(rZ);
        double sinX = Math.sin(rX);
        double sinY = Math.sin(rY);
        double sinZ = Math.sin(rZ);

        double Axx = cosX * cosY;
        double Axy = cosX * sinY * sinZ - sinX * cosZ;
        double Axz = cosX * sinY * cosZ + sinX * sinZ;

        double Ayx = sinX * cosY;
        double Ayy = sinX * sinY * sinZ + cosX * cosZ;
        double Ayz = sinX * sinY * cosZ - cosX * sinZ;

        double Azx = -sinY;
        double Azy = cosY * sinZ;
        double Azz = cosY * cosZ;

        float nX = x - aX;
        float nY = y - aY;
        float nZ = z - aZ;

        vec3[startIndex] = (float) (Axx * nX + Axy * nY + Axz * nZ);
        vec3[startIndex + 1] = (float) (Ayx * nX + Ayy * nY + Ayz * nZ);
        vec3[startIndex + 2] = (float) (Azx * nX + Azy * nY + Azz * nZ);
    }


    public static void rotateVector2(float[] vec2, float[] anchorVec, double rotationRadians) {
        rotateVector2(vec2, vec2[0], vec2[1], anchorVec[0], anchorVec[1], rotationRadians);
    }

    public static void rotateVector2(float[] vec2, float cX, float cY, double rotationRadians) {
        rotateVector2(vec2, cX, cY, 0, 0, rotationRadians);
    }

    public static void rotateVector2(float vec2[], final float cX, final float cY, float anchorX, float anchorY, double rotationRadians) {
        final double sin = Math.sin(rotationRadians);
        final double cos = Math.cos(rotationRadians);
        final double nCX = (float) ((cX * cos) - (cY * sin));
        final double nCY = (float) ((cX * sin) + (cY * cos));
        vec2[0] = (float) (nCX) + anchorX;
        vec2[1] = (float) (nCY) + anchorY;
        //System.out.println("("+cX + ","+cY+") -> " + rotationRadians + " -> (" + ret[0] + "," + ret[1] + ")");
    }

    public static void scaleVector(float[] vector, float factor) {
        for (int i = 0; i < vector.length; i += 4) {
            vector[i] *= factor;
            vector[i + 1] *= factor;
            vector[i + 2] *= factor;
        }
    }

    public static void scaleVectors(float[][] vectors, float factor) {
        for (float[] vector : vectors) {
            scaleVector(vector, factor);
        }
    }

    //because otherwise, if I just use the .clone method it will still use the original pointers
    public static float[][] duplicateMatrix(float[][] matrix) {
        float[][] ret = new float[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            ret[i] = matrix[i].clone();
        }
        return ret;
    }

    public static float[] translateMatrix(float[] matrix, float[] matrix2) {
        if (matrix.length != matrix2.length) {
            throw new UnsupportedOperationException();
        }
        float[] translated = new float[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            translated[i] = matrix[i] * matrix2[i];
        }
        return translated;
    }

    public static void addMatrices(float[][] m1, float[][] m2) {
        for (int i = 0; i < m1.length; i++) {
            addArrays(m1[i], m2[i]);
        }
    }

    public static void addArrays(float[] arr, float[] arr2) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] += arr2[i];
        }
    }

    /**
     * took me way too long
     */
    public static float[][] multiplyMatrices(float[][] m1, float[][] m2) {
        if (m1[0].length != m2.length) {
            throw new UnsupportedOperationException("Incompartible matrices");
        }
        float[][] ret = new float[m2.length][m2[0].length];
        for (int i = 0; i < m2.length; i++) {
            for (int j = 0; j < m2[i].length; j++) {
                for (int k = 0; k < m1[i].length; k++) {
                    ret[k][j] += m2[i][j] * m1[k][i];
                }
            }
        }
        return ret;
    }
    //very proud of that one
    public static void multiply4x4Matrices(float[] m1, float[] m2){
        Arrays.fill(bufferMatrix,0);
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r <4; r++) {
                for (int i = 0; i < 4; i++) {
                    int m1Loc = r*4+i;
                    int m2Loc = c+i*4;//(c)*4+(3-r);
                    int retLoc = i+r*4;
                    bufferMatrix[c + r*4] += m1[m1Loc]*m2[m2Loc];
                }
            }
        }
        System.arraycopy(bufferMatrix, 0, m1, 0, m1.length);
    }

    /**
     * resets the current matrix and sets it to a translation
     */
    public static void generateTranslationMatrix(float[][] mat, float[] vec) {
        generateTranslationMatrix(mat, vec[0], vec[1], vec[2]);
    }

    /**
     * resets the current matrix and sets it to a translation
     */
    public static void generateTranslationMatrix(float[][] mat, float x, float y, float z) {
        if (mat.length != 4) {
            return;
        }
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat.length; j++) {
                mat[i][j] = 0;
            }
        }
        for (int i = 0; i < 4; i++) {
            mat[i][i] = 1;
        }
        mat[0][3] = x;
        mat[1][3] = y;
        mat[2][3] = z;

    }

    public static void generateTranslationMatrix(float[] mat4, float[] vec) {
        generateTranslationMatrix(mat4, vec[0], vec[1], vec[2]);
    }

    public static void generateTranslationMatrix(float[] mat4, float x, float y, float z) {

        for (int i = 0; i < mat4.length; i++) {
            mat4[i] = 0;
        }
        for (int i = 0; i < mat4.length; i += 5) {
            mat4[i] = 1;
        }

        mat4[3] = x;
        mat4[7] = y;
        mat4[11] = z;

    }

    public static float[] createTranslationMatrix(float x, float y, float z) {
        float[] ret = new float[16];
        generateTranslationMatrix(ret, x, y, z);
        return ret;
    }

    private static final float[] bufferMatrix = new float[16];

    public static void generateRotationMatrix(float[] m4, float rX, float rY, float rZ) {
        float sX = (float) Math.sin(rX);
        float sY = (float) Math.sin(rY);
        float sZ = (float) Math.sin(rZ);
        float cX = (float) Math.cos(rX);
        float cY = (float) Math.cos(rY);
        float cZ = (float) Math.cos(rZ);

        Arrays.fill(m4, 0);

        m4[0] = cZ * cY;
        m4[1] = cZ * sY * sX - sZ * cX;
        m4[2] = cZ * sY * cX + sZ * sX;

        m4[4] = sZ * cY;
        m4[5] = sZ * sY * sX + cZ * cX;
        m4[6] = sZ * sY * cX - cZ * sX;

        m4[8] = -sY;
        m4[9] = cY * sX;
        m4[10] = cY * cX;

        m4[15] = 1;

    }


    public static void generateProjectionMatrix(float[] m4, float zNear, float zFar, float fov, float w, float h){
        float ar = w / h;
        generateProjectionMatrix(m4,zNear,zFar,fov,ar);
    }
    public static void generateProjectionMatrix(float[] m4, float zNear, float zFar, float fov, float aspR){
        Arrays.fill(m4,0);
        float hh = (float) Math.tan(fov*.5f);
        m4[0] = 1/(hh*aspR);
        m4[5] = 1/hh;
        m4[10] = (zFar + zNear) / (zNear - zFar);
        m4[11] = -1f;
        m4[14] = (zFar + zFar)*zNear/(zNear-zFar);
    }

    public static void applyLookTransformation(float[] m4, float cX, float cY, float cZ, float x, float y, float z) {
        applyLookTransformation(m4,cX,cY,cZ,x,y,z,0,1,0);
    }

    public static void applyLookTransformation(float[] m4, float[] camera, float x, float y, float z) {
        applyLookTransformation(m4,camera[0],camera[1],camera[2],x,y,z);
    }

    public static void applyLookTransformation(float[] m4, float[] camera, float x, float y, float z, float upX, float upY, float upZ) {
        applyLookTransformation(m4,camera[0],camera[1],camera[2],x,y,z,upX,upY,upZ);
    }

    public static void applyLookTransformation(float[] m4, float cX, float cY, float cZ, float x, float y, float z, float upX, float upY, float upZ){
        float dX, dY, dZ;
        dX = cX - x;
        dY = cY - y;
        dZ = cZ - z;
        float i = 1F/(float)(Math.sqrt(dX*dX + dY*dY + dZ*dZ));
        dX*=i; dY*=i; dZ*=i;
        float lX,lY,lZ;
        lX = upY*dZ-upZ*dY;
        lY = upZ*dX-upX*dZ;
        lZ = upX*dY-upY*dX;
        float iL = 1F/((float)Math.sqrt(lX*lX+lY*lY+lZ*lZ));
        lX*=iL; lY*=iL; lZ*=iL;
        float uX, uY, uZ;
        uX = dY * lZ - dZ * lY;
        uY = dZ * lX - dX * lZ;
        uZ = dX * lY - dY * lX;
        float   m00, m10, m20, m30, //likely wrong lol
                m01, m11, m21, m31,
                m02, m12, m22, m32,
                m03, m13, m23, m33;
        m00 = lX; m01 = uX; m02 = dX; m10 = lY;
        m11 = uY; m12 = dY; m20 = lZ; m21 = uZ;
        m22 = dZ;
        m30 = -(lX * cX + lY * cY + lZ * cZ);
        m31 = -(uX * cX + uY * cY + uZ * cZ);
        m32 = -(dX * cX + dY * cY + dZ * cZ);

        bufferMatrix[0] = m4[0] * m00;
        bufferMatrix[4] = m4[0] * m10;
        bufferMatrix[8] = m4[0] * m20;
        bufferMatrix[12] = m4[0] * m30;

        bufferMatrix[1] = m4[5] * m01;
        bufferMatrix[5] = m4[5] * m11;
        bufferMatrix[9] = m4[5] * m21;
        bufferMatrix[13] = m4[5] * m31;

        bufferMatrix[2] = m4[10] * m02;
        bufferMatrix[6] = m4[10] * m12;
        bufferMatrix[10] = m4[10] * m22;
        bufferMatrix[14] = m4[10] * m32 + m4[14];

        bufferMatrix[3] = m4[11] * m02;
        bufferMatrix[7] = m4[11] * m12;
        bufferMatrix[11] = m4[11] * m22;
        bufferMatrix[15] = m4[11] * m32;

        System.arraycopy(bufferMatrix, 0, m4, 0, bufferMatrix.length);
    }

    public static void debugMatrix4x4(float[] matrix4) {
        StringBuilder debug = new StringBuilder("[ ");
        for (int i = 0; i < matrix4.length; i += 4) {
            for (int j = 0; j < 4; j++) {
                debug.append(matrix4[i + j]).append(" ");
            }
            debug.append("]\n");
            if (i + 4 < matrix4.length) {
                debug.append("[ ");
            }
        }
        System.out.println(debug);
    }

    public static void compareMatrices(String m1N, String m2N, float[] m1, float[] m2){
        System.out.println("Comparing " + m1N + " with " + m2N + ": ");
        for (int i = 0; i < m1.length; i++) {
            String m = "m" + i/4 + "" + i%4;
            if(m1[i]!=m2[i]){System.out.println("Descrepancy at " + i + " (" + m + "): " + m1[i] + " != " + m2[i]);}
        }
        System.out.println("Comparison complete!");
    }

    public static void debugMatrix(float[][] mat){
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < mat.length; i++) {
            float[] act = mat[i];
            out.append("[");
            for (int j = 0; j < act.length; j++) {
                out.append(mat[i][j]);
                if(j != act.length - 1){
                    out.append(",");}
            }
            out.append("]\n");
        }
        System.out.println(out);
    }

    public static void debugPolygon(float poly[], int debugN){
        if(debugN >= 0){System.out.println("Debugging polygon " + debugN + ": ");}
        for (int i = 0; i < poly.length; i+=4) {
            System.out.println("| Vertice " + i/4 + ": [" + poly[i] + "," + poly[i+1] + "," + poly[i+2] + "]");
        }
    }

    public static void debugModel(float[][] model, int debugN){
        if(debugN >= 0){System.out.println("Debugging model " + debugN + ": ");}
        for (int i = 0; i < model.length; i++) {
            float[] poly = model[i];
            System.out.println("| Debugging polygon " + i + ": ");
            for (int j = 0; j < poly.length; j+=4) {
                System.out.println("|| Vertice " + j/4 + ": [" + poly[j] + "," + poly[j+1] + "," + poly[j+2] + "]");
            }
        }
        }

}
