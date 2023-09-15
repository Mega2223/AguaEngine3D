package net.mega2223.aguaengine3d.objects;

import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.physics.PhysicsContext;
import net.mega2223.aguaengine3d.physics.objects.PhysicsSystem;

public class PhysicsRenderContext {
    final RenderingContext renderingContext;
    final PhysicsContext physicsContext;

    public PhysicsRenderContext(){
        renderingContext = new RenderingContext();
        physicsContext = new PhysicsContext(0);
    }

    public void doLogic(){
        doLogic(1);
    }

    public void doLogic(float time){
        renderingContext.doLogic();
        physicsContext.doLogic(time);
    }

    public void doRender(float[] projectionMatrix){
        renderingContext.doRender(projectionMatrix);
    }

    public RenderingContext renderContext() {
        return renderingContext;
    }

    public PhysicsContext physContext() {
        return physicsContext;
    }

    public PhysicsRenderContext addObject(Model object){
        renderingContext.addObject(object);
        return this;
    }

    public PhysicsRenderContext addObject(PhysicsSystem object){
        physicsContext.addObject(object);
        return this;
    }

    public PhysicsRenderContext addObject(ModelPhysicsAggregate object){
        addObject(object.model);
        addObject(object.physicsHandler);
        return this;
    }

    public void setActive(boolean state){
        renderingContext.setActive(state);
        physicsContext.setActive(state);
    }

}
