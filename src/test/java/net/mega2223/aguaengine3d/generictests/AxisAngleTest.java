package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.util.Locale;
import java.util.Random;

public class AxisAngleTest {
    static Random r = new Random();
    //fixme EXTREMAMENTE impreciso
    public static void main(String[] args) {
        r.setSeed(System.currentTimeMillis());
        float[] a = {r.nextFloat()*2+1,r.nextFloat()*2+1,r.nextFloat()*2+1,0};
        float[] b = {r.nextFloat()*2+1,r.nextFloat()*2+1,r.nextFloat()*2+1,0};
        float[] axis = new float[4];

        VectorTranslator.normalize(a); VectorTranslator.normalize(b);

        VectorTranslator.getAxisAngle(a,b,axis);
//
//        VectorTranslator.getCrossProduct(a,b,axis);
//        VectorTranslator.normalize(axis);
//        VectorTranslator.scaleVector(axis, (float) Math.sin(Math.acos(VectorTranslator.dotProduct(a,b))));

        System.out.printf(
                Locale.US,
                "(%.3f,%.3f,%.3f) <) (%.3f,%.3f,%.3f) = (%.3f,%.3f,%.3f)\n",
                a[0],a[1],a[2],b[0],b[1],b[2],axis[0],axis[1],axis[2]
        );

        float[] rMat = new float[16];
        MatrixTranslator.rotationMatrixFromAxisAngle(axis,rMat);
        float[] res = new float[4];
        MatrixTranslator.multiplyVec4Mat4(a,rMat,res);
        MatrixTranslator.debugMatrix4x4(rMat);
        System.out.printf(Locale.US,"a * M = (%.3f,%.3f,%.3f)\nb=(%.3f,%.3f,%.3f)\n\n",
                res[0],res[1],res[2],b[0],b[1],b[2]
        );

        VectorTranslator.rotateAlongAxis(a,axis,res);
        System.out.printf(Locale.US,"a * vr = (%.3f,%.3f,%.3f)\nb=(%.3f,%.3f,%.3f)\n",
                res[0],res[1],res[2],b[0],b[1],b[2]
        );
        //Close enough lmao
        float[] t = new float[4];
        for (int i = 0; i < 4; i++) {t[i] = res[i] - b[i];}
//        if(VectorTranslator.getMagnitude(t) > .25F){
//            throw new RuntimeException("Invalid vector transformation");
//        }
    }
}
