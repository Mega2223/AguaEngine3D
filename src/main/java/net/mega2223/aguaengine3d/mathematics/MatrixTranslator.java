package net.mega2223.aguaengine3d.mathematics;

import net.mega2223.aguaengine3d.misc.annotations.Modified;

import java.util.Arrays;
import java.util.Locale;

@SuppressWarnings("unused") //"Aqui seu programador IDIOTA o método está INUTILIZADO, apague IMEDIATAMENTE"

//FIXME tem equações matriciais que mudam vetores os quais iterações seguintes ainda dependem
//ex translateAllVertices, dá uma olhada depois e verifica a translação de cada matriz

public class MatrixTranslator {

    /**
     * Class that stores handmade math calculations for matrix translation and stuff
     * Seems like LWJGL has libraries that do exactly what I'm trying to do, but I'm just too deep to care now
     * Plus it's pretty cool doing your own stuff and seeing how it does not work the way you intended
     * */

    public static final int ISOMETRIC_PROJECTION = 0;
    public static final int WEAK_PERSPECTIVE_PROJECTION = 1;
    public static final int PSEUDO_PERSPERCTIVE_PROJECTION = 2;
    protected static final int TRUE_PERSPECTIVE_PROJECTION = 3;
    protected static final int WEAKER_PERSPECTIVE_PROJECTION = 4;

    private static final float[] bufferMatrix4 = new float[16];

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

                System.out.println();
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

        }
    }

    public static void multiplyVectorMatrix(@Modified float[] vector, float[][] mat4) {
        if (mat4.length != 4 || vector.length != 4) {
            throw new UnsupportedOperationException();
        }
        //this is optimal I guess
        for (int i = 0; i < 4; i++) {
            vector[i] = (mat4[i][0] * vector[0]) + (mat4[i][1] * vector[1]) + (mat4[i][2] * vector[2]) + (mat4[i][3] * vector[3]);
        }
    }

    public static void multiplyVectorMatrix(@Modified float[] vector, float[] mat4) {
        if (mat4.length != 16) {
            throw new UnsupportedOperationException();
        }
        //this is optimal I guess
        for (int i = 0; i < 4; i++) {
            int i1 = i * 4;
            vector[i] = (mat4[i1] * vector[0]) + (mat4[i1 + 1] * vector[1]) + (mat4[i1 + 2] * vector[2]) + (mat4[i1 + 3] * vector[3]);
        }
    }

    public static void transformVectorArray(@Modified float[] vectors, float[] mat4) {
        if (mat4.length < 16) {
            throw new UnsupportedOperationException("Provided argument is not a valid matrix object.");
        }
        float[] c = vectors.clone();
        for (int g = 0; g < vectors.length; g += 4) {
            for (int i = 0; i < 4; i++) {
                int i1 = i * 4;
                vectors[i + g] = (mat4[i1] * c[g]) + (mat4[i1 + 1] * c[g + 1]) + (mat4[i1 + 2] * c[g + 2]) + (mat4[i1 + 3] * c[g + 3]);
            }
        }
    }

    public void rotateAllVectors(@Modified float[][] vectors, float[] rotationRadians) {
        for (float[] polygon : vectors) {
            rotateAllVectors(polygon, rotationRadians);
        }
    }

    public void rotateAllVectors(@Modified float[] vectors, float[] rotationRadians) {
        for (int j = 0; j < vectors.length; j += 4) {
            float[] rot = vectors.clone();
            MatrixTranslator.rotateVector(rotationRadians[0], rotationRadians[1], rotationRadians[2], j, vectors);
        }
    }

    public static void rotateVector(double rX, double rY, double rZ, @Modified float[] result) {
        rotateVector(rX, rY, rZ, 0, 0, 0, result);
    }

    public static void rotateVector(double rX, double rY, double rZ, int startIndex, @Modified float[] result) {
        rotateVector(rX, rY, rZ, 0, 0, 0, startIndex, result);
    }

    public static void rotateVector(double rX, double rY, double rZ, float aX, float aY, float aZ, @Modified float[] result) {
        rotateVector(rX, rY, rZ, aX, aY, aZ, 0, result);
    }

    public static void rotateVector(double rX, double rY, double rZ, float aX, float aY, float aZ, int startIndex, @Modified float[] result) {
        float x = result[startIndex];
        float y = result[startIndex + 1];
        float z = result[startIndex + 2];
        rotateVector(x, y, z, rX, rY, rZ, aX, aY, aZ, startIndex, result);
    }

    public static void rotateVector(float x, float y, float z, double rX, double rY, double rZ, @Modified float[] result) {
        rotateVector(x, y, z, rX, rY, rZ, 0, 0, 0, 0, result);
    }

    public static void rotateVector(float x, float y, float z, double rX, double rY, double rZ, float aX, float aY, float aZ, int startIndex, @Modified float[] result) {

        if (!Double.isFinite(rX)) {rX = 0;}
        if (!Double.isFinite(rY)) {rY = 0;}
        if (!Double.isFinite(rZ)) {rZ = 0;}

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

        result[startIndex] = (float) (Axx * nX + Axy * nY + Axz * nZ);
        result[startIndex + 1] = (float) (Ayx * nX + Ayy * nY + Ayz * nZ);
        result[startIndex + 2] = (float) (Azx * nX + Azy * nY + Azz * nZ);
    }

    public static void rotateVector2(@Modified float[] vec2, float[] anchorVec, double rotationRadians) {
        rotateVector2(vec2[0], vec2[1], anchorVec[0], anchorVec[1], rotationRadians, vec2);
    }

    public static void rotateVector2(float cX, float cY, double rotationRadians, @Modified float[] result) {
        rotateVector2(cX, cY, 0, 0, rotationRadians, result);
    }

    public static void rotateVector2(final float cX, final float cY, float anchorX, float anchorY, double rotationRadians, @Modified float[] result) {
        final double sin = Math.sin(rotationRadians);
        final double cos = Math.cos(rotationRadians);
        final double nCX = (float) ((cX * cos) - (cY * sin));
        final double nCY = (float) ((cX * sin) + (cY * cos));
        result[0] = (float) (nCX) + anchorX;
        result[1] = (float) (nCY) + anchorY;
    }

    public static void scaleVector(@Modified float[] vector, float factor) {
        for (int i = 0; i < vector.length; i += 4) {
            vector[i] *= factor;
            vector[i + 1] *= factor;
            vector[i + 2] *= factor;
        }
    }

    public static void scaleVectors(@Modified float[][] vectors, float factor) {
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
    /** Multiplies each element of matrix with their respective counterpart on matrix2, this is NOT a matrix multiplication. */
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

    public static void addArrays(@Modified float[] arr, float[] arr2) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] += arr2[i];
        }
    }

    public static void addMatrices(float[] mat4a, float[] mat4b, @Modified float[] dest){
        for (int i = 0; i < 16; i++) {
            dest[i] = mat4a[i] + mat4b[i];
        }
    }

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

    public static void multiply4x4Matrices(@Modified float[] m1, float[] m2){
        multiply4x4Matrices(m1,m2,bufferMatrix4);
        System.arraycopy(bufferMatrix4,0,m1,0,16);
    }

    //very proud of that one
    public static void multiply4x4Matrices(float[] m1, float[] m2, @Modified float[] result){
        Arrays.fill(bufferMatrix4,0);
        for (int c = 0; c < 4; c++) {
            for (int r = 0; r <4; r++) {
                for (int i = 0; i < 4; i++) {
                    int m1Loc = r*4+i;
                    int m2Loc = c+i*4;//(c)*4+(3-r);
                    bufferMatrix4[c + r*4] += m1[m1Loc]*m2[m2Loc];
                }
            }
        }
        System.arraycopy(bufferMatrix4, 0, result, 0, 16);
    }

    public static void multiply3x3Matrices(float[] m1, float[] m2, @Modified float[] result){
        Arrays.fill(result,0);
        for (int c = 0; c < 3; c++) {
            for (int r = 0; r < 3; r++) {
                for (int i = 0; i < 3; i++) {
                    result[c + r*3] += m1[r*3+i]*m2[c+i*3];
                }
            }
        }
        System.arraycopy(result, 0, m1, 0, result.length);
    }

    public static void multiplyVec4Mat4(@Modified float[] vec4, float[] mat4){
        multiplyVec4Mat4(vec4,mat4,VectorTranslator.buffer1);
        System.arraycopy(VectorTranslator.buffer1,0,vec4,0,4);
    }

    public static void multiplyVec4Mat4(float[] vec4, float[] mat4,@Modified float[] result){
        Arrays.fill(result,0);
        for (int i = 0; i < 16; i++) {
            int c = i%4, r = i/4;
            result[r]+=vec4[c]*mat4[r*4+c];
        }
    }

    public static void multiplyVec3Mat3(@Modified float[] vec3, float[] mat3){
        multiplyVec3Mat3(vec3,mat3,VectorTranslator.buffer1);
        System.arraycopy(VectorTranslator.buffer1,0,vec3,0,3);
    }

    public static void multiplyVec3Mat3(float[] vec3, float[] mat3,@Modified float[] resultMat3){
        Arrays.fill(resultMat3,0);
        for (int i = 0; i < 9; i++) {
            int c = i%3, r = i/3;
            resultMat3[r]+=vec3[c]*mat3[r*3+c];
        }
    }

    /**Generates a rotation angle given an axis angle, pretty inaccurate*/
    public static void rotationMatrixFromAxisAngle(float[] axisAngle, @Modified float[] dest){
        Arrays.fill(dest,0);
        float ang = VectorTranslator.getMagnitude(axisAngle[0],axisAngle[1],axisAngle[2]);
        float c = (float) Math.cos(ang), s = (float) Math.sin(ang),  C = 1F - c;
        float x = axisAngle[0] / ang, y = axisAngle[1] / ang, z = axisAngle[2] / ang;
        dest[0] = x * x * C + c; dest[1] = x * y * C - (z * s); dest[2] = x * z * C + (y * s);
        dest[4] = y * x * C + (z * s); dest[5] = y * y * C + c; dest[6] = y * z * C - (x * s);
        dest[8] = z * x * C - (y * s); dest[9] = z * y * C + (x * s); dest[10] = z * z * C + c;
        dest[15] = 1;
    }

    /**
     * resets the current matrix and sets it to a translation
     */
    public static void generateTranslationMatrix(float[] vec, @Modified float[][] resultMat4) {
        generateTranslationMatrix(vec[0], vec[1], vec[2], resultMat4);
    }

    /**
     * resets the current matrix and sets it to a translation
     */
    public static void generateTranslationMatrix(float x, float y, float z, @Modified float[][] resultMat4) {
        if (resultMat4.length != 4) {
            return;
        }
        for (int i = 0; i < resultMat4.length; i++) {
            for (int j = 0; j < resultMat4.length; j++) {
                resultMat4[i][j] = 0;
            }
        }
        for (int i = 0; i < 4; i++) {
            resultMat4[i][i] = 1;
        }
        resultMat4[0][3] = x;
        resultMat4[1][3] = y;
        resultMat4[2][3] = z;

    }
    
    public static void generateTranslationMatrix(float[] vec, @Modified float[] resultMat4) {
        generateTranslationMatrix(vec[0], vec[1], vec[2], resultMat4);
    }

    public static void generateTranslationMatrix(float x, float y, float z, @Modified float[] resultMat4) {

        Arrays.fill(resultMat4, 0);
        for (int i = 0; i < resultMat4.length; i += 5) {
            resultMat4[i] = 1;
        }

        resultMat4[3] = x;
        resultMat4[7] = y;
        resultMat4[11] = z;

    }

    public static void generateIdentity(float[] resultMat4){
        generateTranslationMatrix(0, 0, 0, resultMat4);
    }

    public static void generateTranslationAndScaleMatrix(float tx, float ty, float tz, float sx, float sy, float sz, @Modified float[] resultMat4){
        Arrays.fill(resultMat4,0);
        resultMat4[0] = sx;
        resultMat4[5] = sy;
        resultMat4[10] = sz;
        resultMat4[3] = tx;
        resultMat4[7] = ty;
        resultMat4[11] = tz;
        resultMat4[15] = 1;
    }

    public static float[] createTranslationMatrix(float x, float y, float z) {
        float[] ret = new float[16];
        generateTranslationMatrix(x, y, z, ret);
        return ret;
    }

    public static void generateRotationMatrix(float rX, float rY, float rZ, @Modified float[] resultMat4) {
        float sX = (float) Math.sin(rX);
        float sY = (float) Math.sin(rY);
        float sZ = (float) Math.sin(rZ);
        float cX = (float) Math.cos(rX);
        float cY = (float) Math.cos(rY);
        float cZ = (float) Math.cos(rZ);

        Arrays.fill(resultMat4, 0);

        resultMat4[0] = cZ * cY;
        resultMat4[1] = cZ * sY * sX - sZ * cX;
        resultMat4[2] = cZ * sY * cX + sZ * sX;

        resultMat4[4] = sZ * cY;
        resultMat4[5] = sZ * sY * sX + cZ * cX;
        resultMat4[6] = sZ * sY * cX - cZ * sX;

        resultMat4[8] = -sY;
        resultMat4[9] = cY * sX;
        resultMat4[10] = cY * cX;

        resultMat4[15] = 1;

    }

    public static void generateStaticInterfaceProjectionMatrix(@Modified float[] resultMat4, float aspectRatio){
        generateStaticInterfaceProjectionMatrix(resultMat4,aspectRatio,0,0,0);
    }

    public static void generateStaticInterfaceProjectionMatrix(@Modified float[] resultMat4, float aspectRatio, float tX, float tY, float tZ){
        generateStaticInterfaceProjectionMatrix(resultMat4,aspectRatio,tX,tY,tZ,1,1,1);
    }

    public static void generateStaticInterfaceProjectionMatrix(@Modified float[] resultMat4, float aspectRatio, float tX, float tY, float tZ, float sX, float sY, float sZ){
        generateTranslationAndScaleMatrix(tX, tY, tZ, sX/aspectRatio, sY, sZ, resultMat4);//fixme

    }

    public static void generateIsometricProjectionMatrix(@Modified float[] resultMat4, float scale){
        Arrays.fill(resultMat4,0);
        resultMat4[0] = scale; resultMat4[5] = scale; resultMat4[10] = scale; resultMat4[15] = scale;
    }

    public static void generatePerspectiveProjectionMatrix(@Modified float[] resultMat4, float zNear, float zFar, float fov, float w, float h){
        float ar = w / h;
        generatePerspectiveProjectionMatrix(resultMat4,zNear,zFar,fov,ar);
    }
    public static void generatePerspectiveProjectionMatrix(@Modified float[] resultMat4, float zNear, float zFar, float fov, float aspR){
        Arrays.fill(resultMat4,0);
        float hh = (float) Math.tan(fov*.5f);
        resultMat4[0] = 1/(hh*aspR);
        resultMat4[5] = 1/hh;
        resultMat4[10] = (zFar + zNear) / (zNear - zFar);
        resultMat4[11] = -1f;
        resultMat4[14] = (zFar + zFar)*zNear/(zNear-zFar);
    }

    public static void applyLookTransformation(float cX, float cY, float cZ, float x, float y, float z, @Modified float[] resultMat4) {
        applyLookTransformation(cX, cY, cZ, x, y, z, 0, 1, 0, resultMat4);
    }

    public static void applyLookTransformation(float[] camera, float x, float y, float z, @Modified float[] resultMat4) {
        applyLookTransformation(camera[0], camera[1], camera[2], x, y, z, resultMat4);
    }

    public static void applyLookTransformation(float[] camera, float x, float y, float z, float upX, float upY, float upZ, @Modified float[] resultMat4) {
        applyLookTransformation(camera[0], camera[1], camera[2], x, y, z, upX, upY, upZ, resultMat4);
    }

    public static void applyLookTransformation(float cX, float cY, float cZ, float x, float y, float z, float upX, float upY, float upZ, @Modified float[] resultMat4){
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

        bufferMatrix4[0] = resultMat4[0] * m00;
        bufferMatrix4[4] = resultMat4[0] * m10;
        bufferMatrix4[8] = resultMat4[0] * m20;
        bufferMatrix4[12] = resultMat4[0] * m30;

        bufferMatrix4[1] = resultMat4[5] * m01;
        bufferMatrix4[5] = resultMat4[5] * m11;
        bufferMatrix4[9] = resultMat4[5] * m21;
        bufferMatrix4[13] = resultMat4[5] * m31;

        bufferMatrix4[2] = resultMat4[10] * m02;
        bufferMatrix4[6] = resultMat4[10] * m12;
        bufferMatrix4[10] = resultMat4[10] * m22;
        bufferMatrix4[14] = resultMat4[10] * m32 + resultMat4[14];

        bufferMatrix4[3] = resultMat4[11] * m02;
        bufferMatrix4[7] = resultMat4[11] * m12;
        bufferMatrix4[11] = resultMat4[11] * m22;
        bufferMatrix4[15] = resultMat4[11] * m32;

        System.arraycopy(bufferMatrix4, 0, resultMat4, 0, bufferMatrix4.length);
    }

    public static void getTransposeMatrix4(@Modified float[] resultMat4){
        getTransposeMatrix4(resultMat4,bufferMatrix4);
        System.arraycopy(bufferMatrix4,0,resultMat4,0,16);
    }

    public static void getTransposeMatrix4(float[] m4,@Modified float[] resultMat4){
        for (int i = 0; i < 16; i++) {
            int r = i/4;
            int c = i%4;
            resultMat4[r+c*4] = m4[r*4+c];
        }
    }

    public static void getInverseMatrix3(float[] m3,@Modified float[] resultMat3){
        float determinantMatrix3 = getDeterminantMatrix3(m3);
        if(determinantMatrix3 == 0){throw new UnsupportedOperationException("Matrix does not have a determinant.");}
        float v1 = m3[0]*m3[4];
        float v2 = m3[0]*m3[5];
        float v3 = m3[1]*m3[3];
        float v4 = m3[2]*m3[3];
        float v5 = m3[1]*m3[6];
        float v6 = m3[2]*m3[6];
        float inv = 1/determinantMatrix3;
        resultMat3[0] = (m3[4]*m3[8]-m3[5]*m3[7])*inv;
        resultMat3[1] = -(m3[1]*m3[8]-m3[2]*m3[7])*inv;
        resultMat3[2] = (m3[1]*m3[5]-m3[2]*m3[4])*inv;
        resultMat3[3] = -(m3[3]*m3[8]-m3[5]*m3[6])*inv;
        resultMat3[4] = (m3[0]*m3[8]-v6)*inv;
        resultMat3[5] = -(v2-v4)*inv;
        resultMat3[6] = (m3[3]*m3[7]-m3[4]*m3[6])*inv;
        resultMat3[7] = -(m3[0]*m3[7]-v5)*inv;
        resultMat3[8] = (v1-v3)*inv;
    }

    public static void getTransposeMatrix3(float[] m3, @Modified float[] resultMat3){
        for (int i = 0; i < 9; i++) {
            int r = i/3;
            int c = i%3;
            resultMat3[r+c*3] = m3[r*3+c];
        }
    }

    public static float getDeterminantMatrix3(float[] mat3){
        return (mat3[0]*mat3[4]*mat3[8] + mat3[1]*mat3[5]*mat3[6] + mat3[2]*mat3[3]*mat3[7])-
                (mat3[6]*mat3[4]*mat3[2] + mat3[7]*mat3[5]*mat3[0] + mat3[8]*mat3[3]*mat3[1]);
    }

    public static void getRotationMat4FromQuaternion(float w, float x, float y, float z, float[] resultMat4){
        Arrays.fill(resultMat4,0);
        float[] stolenVector = VectorTranslator.buffer1;
        VectorTranslator.getRotationRadians(w,x,y,z, stolenVector);
        MatrixTranslator.generateRotationMatrix(stolenVector[0], stolenVector[1], stolenVector[2], resultMat4);
        /*This doesn't work lol
        resultMat4[0] = 1-(2*y*y+2*z*z);
        resultMat4[1] = 2*x*y + 2*z*w;
        resultMat4[2] = 2*x*z - 2*y*w;
        resultMat4[4] = 2*x*y - 2*z*w;
        resultMat4[5] = 1-(2*x*x+2*z*z);
        resultMat4[6] = 2*y*z + 2*x*w;
        resultMat4[8] = 2*x*z + 2*y*w;
        resultMat4[9] = 2*y*z - 2*x*w;
        resultMat4[10] = 1-(2*x*x+2*y*y);
        resultMat4[15] = 1;*/
    }


    public static void debugMatrix4x4(float[] matrix4) {
        StringBuilder debug = new StringBuilder("[ ");
        for (int i = 0; i < matrix4.length; i += 4) {
            for (int j = 0; j < 4; j++) {
                debug.append(String.format(Locale.US,"%.2f",matrix4[i + j])).append(" ");
            }
            debug.append("]\n");
            if (i + 4 < matrix4.length) {
                debug.append("[ ");
            }
        }
        System.out.println(debug);
    }

    public static void debugMatrix3x3(float[] matrix3) {
        StringBuilder debug = new StringBuilder("[ ");
        for (int i = 0; i < matrix3.length; i += 3) {
            for (int j = 0; j < 3; j++) {
                debug.append(matrix3[i + j]).append(" ");
            }
            debug.append("]\n");
            if (i + 4 < matrix3.length) {
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

    public static void debugMatrix(int[][] mat){
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < mat.length; i++) {
            int[] act = mat[i];
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

    public static void debugPolygon(float[] poly, int debugN){
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
