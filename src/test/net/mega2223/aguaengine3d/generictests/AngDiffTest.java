package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.mathematics.MathUtils;

public class AngDiffTest {
    public static void main(String[] args) {
        for(float d = (float) - Math.PI * 4; d < Math.PI * 4; d+= Math.PI/36 ){
            System.out.println(d + ": " +MathUtils.angleDifference(d,0));
        }
    }
}
