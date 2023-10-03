package net.mega2223.aguaengine3d.physics.collisiondetection;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.CollisionResolver;
import net.mega2223.aguaengine3d.physics.collisiondetection.hierarchy.Collidiable;
import net.mega2223.aguaengine3d.physics.collisiondetection.hierarchy.Node;
import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoundHierarchyManager {
    protected static final String PREFIX = "| ";
    Node primeNode = null;

    public BoundHierarchyManager(){

    }

    public void generate(List<Hitbox> hitboxes){
        List<Collidiable> unlinked = new ArrayList<>(hitboxes);
        while(unlinked.size() > 2){
            unlinked.add(linkClosestObjects(unlinked,true));
        }
        primeNode = new Node(unlinked.get(0),unlinked.get(1));
    }
    /***updates the coordinates of the bouding ranges without changing the chain topografy*/
    public void update(){
        primeNode.updateCoords();
    }
    public void resolveForHitbox(Hitbox hitbox){
       primeNode.resolveForHitbox(hitbox);
    }

    static Node linkClosestObjects(List<Collidiable> collisionAreas, boolean removeObjectsFromList){
        if(collisionAreas.size() < 2){return null;}
        float minDist = VectorTranslator.getDistance(collisionAreas.get(0).getCenter(), collisionAreas.get(1).getCenter());
        int m1 = 0, m2 = 1;
        for (int i = 0; i < collisionAreas.size(); i++) {
            Collidiable act = collisionAreas.get(i);
            for (int j = 0; j < collisionAreas.size(); j++) {
                if(i==j){continue;}
                Collidiable act2 = collisionAreas.get(j);
                float d = VectorTranslator.getDistance(act.getCenter(),act2.getCenter());
                if(d < minDist){m1 = i; m2 = j; minDist = d;}
            }
        }

        Collidiable col1 = collisionAreas.get(m1);
        Collidiable col2 = collisionAreas.get(m2);
        if(removeObjectsFromList){
            collisionAreas.remove(col1);
            collisionAreas.remove(col2);}
        return new Node(col1, col2);
    }

    public boolean checkForCollision(float x, float y, float z){
        return primeNode.collides(x,y,z);
    }

    public static void displayHierarchy(BoundHierarchyManager man){
        StringBuilder out = new StringBuilder().append("BoundHierarchyManager: \n");
        appendObject(man.primeNode, PREFIX, out);
        System.out.println(out);
    }
    private static void appendObject(Collidiable object, String prefix, StringBuilder toAppend){
        if(object instanceof Node){
            Collidiable obj1 = ((Node) object).getN1();
            Collidiable obj2 = ((Node) object).getN2();
            toAppend.append(prefix).append(object).append("\n");
            appendObject(obj1,prefix+PREFIX,toAppend);
            appendObject(obj2,prefix+PREFIX,toAppend);
        } else {
            toAppend.append(prefix).append(object).append("\n");
        }
    }
}
