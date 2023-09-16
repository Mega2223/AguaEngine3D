package net.mega2223.aguaengine3d.logic;

import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.physics.objects.RigidBodySystem;

public class RigidBodyAggregate extends ModelPhysicsAggregate{

    private final RigidBodySystem phys;

    private final float[] rMatrix = new float[16];

    public RigidBodyAggregate(Model assossiatedMesh, RigidBodySystem physicsHandler) {
        super(assossiatedMesh, physicsHandler);
        phys = physicsHandler;
    }

    @Override
    public void doLogic() {
        super.doLogic();
        MatrixTranslator.generateRotationMatrix(rMatrix,phys.dirX(),phys.dirY(),phys.dirZ());
        model.getShader().setRotationMatrix(rMatrix);
    }
}
