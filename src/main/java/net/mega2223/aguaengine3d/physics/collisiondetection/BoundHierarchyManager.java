package net.mega2223.aguaengine3d.physics.collisiondetection;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.util.ArrayList;
import java.util.List;

public class BoundHierarchyManager {
    Node primeNode = null;
    List<HitboxInteractionArea> areas = new ArrayList<>();

    public BoundHierarchyManager(){

    }

    public void generate(List<Collideable> data){
        List<Collideable> unlinked = new ArrayList<>(data);
        while(unlinked.size() >= 2){
            unlinked.add(linkClosestObjects(unlinked,true));
        }
    }

    static Node linkClosestObjects(List<Collideable> collideables, boolean removeObjectsFromList){
        if(collideables.size() < 2){return null;}
        float minDist = VectorTranslator.getDistance(collideables.get(0).getCenter(),collideables.get(1).getCenter());
        int m1 = -1, m2 = -1;
        for (int i = 0; i < collideables.size(); i++) {
            Collideable act = collideables.get(i);
            for (int j = 0; j < collideables.size(); j++) {
                if(i==j){continue;}
                Collideable act2 = collideables.get(j);
                float d = VectorTranslator.getDistance(act.getCenter(),act2.getCenter());
                if(d < minDist){m1 = i; m2 = j; minDist = d;}
            }
        }
        Collideable col1 = collideables.get(m1);
        Collideable col2 = collideables.get(m2);
        if(removeObjectsFromList){collideables.remove(col1);collideables.remove(col2);}
        return new Node(col1, col2);
    }

    public boolean checkForCollision(float x, float y, float z){
        return primeNode.collides(x,y,z);
    }
}
