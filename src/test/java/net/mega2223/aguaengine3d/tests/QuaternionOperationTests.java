package net.mega2223.aguaengine3d.tests;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.PhysicsUtils;

import java.util.Arrays;

public class QuaternionOperationTests {
    public static void main(String[] args) {
        float[] q1 = new float[4];
        float[] e1 = new float[3];
        PhysicsUtils.genQuaternionFromAxis(.5F,0,1,0,q1);
        System.out.println("Initial quaternion:");
        VectorTranslator.debugVector(q1);
        System.out.println("Rotation in radians:");
        VectorTranslator.getRotationRadians(q1,e1);
        VectorTranslator.debugVector(e1);
        System.out.println("Quaternion from Euler angles:");
        PhysicsUtils.radiansToQuaternion(e1[0],e1[1],e1[2],q1);
        VectorTranslator.debugVector(q1);
        Arrays.fill(q1,0);
        float[] q2 = {1,0,1,0};
        float[] q3 = {2,2,0,0};
        System.out.println("Multiplying the following quaternions: ");
        VectorTranslator.debugVector(q2);
        VectorTranslator.debugVector(q3);
        System.out.println("Result:");
        PhysicsUtils.multiplyQuaternions(q2,q3,q1);
        VectorTranslator.debugVector(q1);
    }
}
