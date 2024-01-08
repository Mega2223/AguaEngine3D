package net.mega2223.aguaengine3d.generictests;

import net.mega2223.aguaengine3d.physics.collisiondetection.BoundHierarchyManager;
import net.mega2223.aguaengine3d.physics.collisiondetection.CollisionEnviroment;
import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;
import net.mega2223.aguaengine3d.physics.utils.objects.hitboxes.RectHitbox;

public class NodeHierarchyGeneratorTest {

    public static void main(String[] args) {
        Hitbox h1 = new RectHitbox(null,-1,-1,-1,1,1,1);
        Hitbox h2 = new RectHitbox(null,-2,-2,-2,2,2,3);
        Hitbox h3 = new RectHitbox(null,2,3,4,1,2,3);
        Hitbox h4 = new RectHitbox(null,3,3,4,1.2F,2,3);
        Hitbox h5 = new RectHitbox(null,2,3,1,2,3,1);
        Hitbox h6 = new RectHitbox(null,11,11,11,12,12,12);
        CollisionEnviroment manager = new CollisionEnviroment();
        manager.addHitbox(h1);
        manager.addHitbox(h2);
        manager.addHitbox(h3);
        manager.addHitbox(h4);
        manager.addHitbox(h5);
        manager.addHitbox(h6);
        manager.updateTopografy();
        BoundHierarchyManager.displayHierarchy(manager.getHierarchyManager());
    }
}
