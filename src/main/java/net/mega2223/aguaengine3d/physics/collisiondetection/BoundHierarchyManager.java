package net.mega2223.aguaengine3d.physics.collisiondetection;

import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.util.ArrayList;
import java.util.List;

public class BoundHierarchyManager {
    InteractionArea a1,a2 = null;
    List<InteractionArea> areas = new ArrayList<>();

    public BoundHierarchyManager(){

    }

    public void generate(){
        List<Collideable> unlinked = new ArrayList<>();
    }

    static Node linkClosestObjects(List<Collideable> collideables){
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
        return new Node(collideables.get(m1),collideables.get(m2));
    }


}
