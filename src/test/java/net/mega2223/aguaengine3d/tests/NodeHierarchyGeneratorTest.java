package net.mega2223.aguaengine3d.tests;

import net.mega2223.aguaengine3d.physics.collisiondetection.BoundHierarchyManager;
import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;
import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.RectHitbox;

public class NodeHierarchyGeneratorTest {

    public static void main(String[] args) {
        Hitbox h1 = new RectHitbox(-1,-1,-1,1,1,1);
        Hitbox h2 = new RectHitbox(-2,-2,-2,2,2,3);
        Hitbox h3 = new RectHitbox(2,3,4,1,2,3);
        Hitbox h4 = new RectHitbox(3,3,4,1.2F,2,3);
        Hitbox h5 = new RectHitbox(2,3,1,2,3,1);
        Hitbox h6 = new RectHitbox(11,11,11,12,12,12);
        BoundHierarchyManager manager = new BoundHierarchyManager();
        manager.addHitbox(h1);
        manager.addHitbox(h2);
        manager.addHitbox(h3);
        manager.addHitbox(h4);
        manager.addHitbox(h5);
        manager.addHitbox(h6);
        manager.generate();
        BoundHierarchyManager.displayHierarchy(manager);
    }
}
