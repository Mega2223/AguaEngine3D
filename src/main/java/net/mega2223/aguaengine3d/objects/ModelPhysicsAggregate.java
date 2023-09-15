package net.mega2223.aguaengine3d.objects;

import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

public class ModelPhysicsAggregate {

    protected final Model model;
    protected final PhysicsSystem physicsHandler;

    public ModelPhysicsAggregate(Model assossiatedMesh, PhysicsSystem physicsHandler){
        this.model = assossiatedMesh;
        this.physicsHandler = physicsHandler;
    }

    public void doLogic(int interation, float timeElapsed, float drag, float[] gravity){
        model.doLogic(interation);
        model.setCoords(physicsHandler.getCoords());
        physicsHandler.doLogic(timeElapsed,drag,gravity);
    }

    public void draw(){
        model.draw();
    }

    public Model model() {
        return model;
    }

    public PhysicsSystem physicsHandler(){
        return physicsHandler;
    }
}
