package net.mega2223.aguaengine3d.physics.collisiondetection;

import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollisionEnviroment {
    BoundHierarchyManager hierarchyManager = new BoundHierarchyManager();
    ArrayList<Hitbox> hitboxes = new ArrayList<>();

    CollisionEnviroment(){

    }

    public void update(float time){
        for (int i = 0; i < hitboxes.size(); i++) {
            hitboxes.get(i).update(time);
        }
    }

    public void updateTopografy(){
        hierarchyManager.generate(getFiniteHitboxes());
    }

    public void addHitbox(net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox area){
        hitboxes.add(area);
    }

    public void removeHitbox(net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox area){
        hitboxes.remove(area);
    }

    public List<Hitbox> getHitboxes() {
        return Collections.unmodifiableList(hitboxes);
    }

    public List<Hitbox> getFiniteHitboxes(){
        ArrayList<Hitbox> ret = new ArrayList<>(hitboxes.size());
        for (Hitbox act : hitboxes) {
            if (Float.isFinite(act.getEffectiveInteractionRadius())) {
                ret.add(act);
            }
        }
        return Collections.unmodifiableList(ret);
    }

    public BoundHierarchyManager getHierarchyManager() {
        return hierarchyManager;
    }
}
