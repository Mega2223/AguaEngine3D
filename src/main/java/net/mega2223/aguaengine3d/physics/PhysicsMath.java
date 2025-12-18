package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;

import java.util.Arrays;

import static net.mega2223.aguaengine3d.mathematics.MatrixTranslator.M4.*;

public class PhysicsMath {
    private PhysicsMath(){}

    private static final float _1D12 = 1F / 12F;
    private static final float _1D6 = 1F / 6F;


    public static void getInertialTensorForRect(float dx, float dy, float dz, float mass, float side, @Modified float[] result){
        Arrays.fill(result,0);
        result[M_11.i] = mass * side * side * _1D6 * (dy*dy + dz*dz);
        result[M_22.i] = mass * side * side * _1D6 * (dx*dx + dz*dz);
        result[M_33.i] = mass * side * side * _1D6 * (dx*dx + dy*dy);
        result[M_44.i] = 1;
    }
}
