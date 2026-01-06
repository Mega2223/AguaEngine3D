package net.mega2223.aguaengine3d.utils;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;

/**
 * Calcula a determinante de uma matriz quadrada de N^2 componentes conforme a fórmula de Leibniz
 * */
public class DeterminantAlgorithmComputation {

    static StringBuilder operationLog = new StringBuilder();

    public static void main(String[] args) {
        // Sim provavelmente tem um jeito mais rápido e não recursivo de fazer isso
        // mas isso funciona, tudo q eu preciso saber really são as ordens das operações
        // para uma mat4
        final int N = 4;
        float[] mat = new float[N * N];
        for (int i = 0; i < mat.length; i++) {
//            mat[i] = (float) Math.random();
            mat[i] = i;
        }
        if(N == 3){
            float d = MatrixTranslator.getDeterminantMatrix3(mat);
            System.out.println("exp = " +d);
        };
//        for (int i = 0; i < N; i++) { // identity
//            mat[MatrixTranslator.getIndexNSizedMatrix(i,i,N,N)] = 1;
//        }
        float determinant = computeDeterminant(mat);
        System.out.println(operationLog);
        System.out.println(debugMatrix(mat));
//        for (int i = 0; i < N; i++) {
//            System.out.println(debugMatrix(generateSubmatrix(mat,1,i)));
//        }

        System.out.println(determinant);
    }

    public static float computeDeterminant(float[] m){
        int n = (int) Math.sqrt(m.length);
        if(n == 1){
            operationLog.append("M["+(int)m[0]+"]");
            return m[0];
        }
        int sign = 1;
        float determinant = 0;
        for (int col = 0; col < n; col++) {
            float[] submatrix = generateSubmatrix(m, 0, col);
            int index = MatrixTranslator.getIndexNSizedMatrix(0,col,n,n);
            operationLog.append(sign > 0 ? "+" : "-");
            operationLog.append("M["+(int)m[index]+"]").append("*(");
            float subMatrixDeterminant = computeDeterminant(submatrix);
            determinant += sign * m[index] * subMatrixDeterminant;
            operationLog.append(")");
            sign = -sign;
        }
        return determinant;
    }

    public static float[] generateSubmatrix(float[] matrix, int rRow, int rCol){
        int n = (int) Math.sqrt(matrix.length);
        int n_ = n - 1;
        float[] ret = new float[n_ * n_];
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if(c == rCol || r == rRow){continue;}
                final int cc = c < rCol ? c : c-1, rr = r < rRow ? r : r-1;
                int ind_= MatrixTranslator.getIndexNSizedMatrix(rr,cc,n_,n_);
                int ind = MatrixTranslator.getIndexNSizedMatrix(r, c, n, n);
                float v = matrix[ind];
                ret[ind_] = v;
            }
        }
        return ret;
    }

    public static String debugMatrix(float[] matrix){
        int n = (int) Math.sqrt(matrix.length);
        StringBuilder s = new StringBuilder();
        for (int r = 0; r < n; r++) {
            s.append("[ ");
            for (int c = 0; c < n; c++) {
                s.append(String.format("%.4f ", matrix[MatrixTranslator.getIndexNSizedMatrix(r, c, n, n)]));
            }
            s.append("]\n");
        }
        return s.toString();
    }

    public static String from(int r, int c, int sign){
        return sign < 0 ? "-" : "+" + ("m" + (r+1)) + (c+1);
    }

    public static void scalarMultiplication(float[] m, float s){
        for (int i = 0; i < m.length; i++) {
            m[i]*=s;
        }
    }
}
