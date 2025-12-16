package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.misc.annotations.Modified;

import java.util.Arrays;

import static net.mega2223.aguaengine3d.mathematics.MatrixTranslator.M4.*;

public class PhysicsMath {
    private PhysicsMath(){}

    private static final float _1D12 = 1F / 12F;

    public static void getInertialTensorForRect(float dx, float dy, float dz, float mass, @Modified float[] result){
        Arrays.fill(result,0);
        result[M_11.i] = mass * _1D12 * (dy*dy + dz*dz);
        result[M_22.i] = mass * _1D12 * (dx*dx + dz*dz);
        result[M_33.i] = mass * _1D12 * (dx*dx + dy*dy);
        result[M_44.i] = 1;
    }
}
