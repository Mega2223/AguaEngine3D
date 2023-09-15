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

    public void doLogic(){
        model.setCoords(physicsHandler.getCoords());
        //does not need to call any doLogic methods, the assossiated RenderingContext and PhyscsContext will handle that
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
