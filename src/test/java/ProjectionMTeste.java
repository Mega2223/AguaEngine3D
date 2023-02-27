import net.mega2223.lwjgltest.aguaengine3d.mathematics.MatrixTranslator;

public class ProjectionMTeste {

    public static void main(String[] args) {
        float[] testVec4 = {10,10,10,1};
        float[][] testMat = new float[4][4];

        MatrixTranslator.generateTranslationMatrix(testMat,testVec4);

        MatrixTranslator.debugMatrix(testMat);

    }


    private static float[][] genTransformationMatrixFromVec3(float[] vec3){
        float[][] ret = new float[4][4];
        for (int i = 0; i < vec3.length; i++) {
            ret[i][i] = vec3[i];
        }
        ret[3][3]=1;
        return ret;
    }
}
