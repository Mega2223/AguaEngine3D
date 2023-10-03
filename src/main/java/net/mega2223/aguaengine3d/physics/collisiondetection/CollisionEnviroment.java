package net.mega2223.aguaengine3d.physics.collisiondetection;

import net.mega2223.aguaengine3d.physics.collisiondetection.hitbox.Hitbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollisionEnviroment {
    BoundHierarchyManager hierarchyManager = new BoundHierarchyManager();
    ArrayList<Hitbox> hitboxes = new ArrayList<>();
    ArrayList<Hitbox> infiniteHitboxes = new ArrayList<>();

    public CollisionEnviroment(){

    }

    public void doLogic(float time){
        if(hierarchyManager.primeNode == null){
            hierarchyManager.generate(hitboxes);
        }
        for (int i = 0; i < hitboxes.size(); i++) {
            hitboxes.get(i).doLogic(time);
            hierarchyManager.resolveForHitbox(hitboxes.get(i));
        }
        hierarchyManager.update();
    }

    public void updateTopografy(){
        hierarchyManager.generate(getFiniteHitboxes());
    }

    public void addHitbox(Hitbox area){
        if(area!=null&&!hitboxes.contains(area)){
            hitboxes.add(area);
            if(Float.isInfinite(area.getEffectiveInteractionRadius())){
                infiniteHitboxes.add(area);
            }
        }
    }

    public void removeHitbox(Hitbox area){
        hitboxes.remove(area);
    }

    public List<Hitbox> getHitboxes() {
        return Collections.unmodifiableList(hitboxes);
    }

    public List<Hitbox> getFiniteHitboxes(){
        return Collections.unmodifiableList(infiniteHitboxes);
    }

    public BoundHierarchyManager getHierarchyManager() {
        return hierarchyManager;
    }
}
