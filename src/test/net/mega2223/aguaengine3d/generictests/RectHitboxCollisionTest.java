package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.utils.objects.hitboxes.RectHitbox;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;

public class RectHitboxCollisionTest {
    public static void main(String[] args) {
        PhysicsSystem sys = new RigidBodySystem(1,new float[9]);
        RectHitbox test = new RectHitbox(sys,-10,-10,-10,10,10,10);
        System.out.println("RECT: " + test);
        float[] dest = new float[4];
        for (float c = -12; c < 12; c+= 0.5F) {
            System.out.println("Testing for c = " + c);
            System.out.println("DEPTH: " + test.getDepth(0,c,0));
            test.getAxisDepthRelative(dest,0,c,0);
            System.out.println("DEPTH VECTOR: ");
            VectorTranslator.debugVector(dest);
            System.out.println("COLLIDES: " + test.collides(0,c,0)+"\n");
        }
    }
}
