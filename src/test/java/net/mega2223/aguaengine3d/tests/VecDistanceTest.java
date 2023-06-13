package net.mega2223.aguaengine3d.tests;

import net.mega2223.aguaengine3d.usecases.airsim.PhysicsUtils;

public class VecDistanceTest {

    public static void main(String[] args) {
        float[] v1 = new float[3];
        float[] v2 = {1,1,1};
        System.out.println(PhysicsUtils.calculateDistanceVec3(v1,v2));

        PhysicsUtils.predictNextStep(v1,1, (float) Math.PI/2,0,0);
        System.out.println();

    }
}
