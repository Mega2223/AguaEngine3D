package net.mega2223.aguaengine3d.physics.debug;

import net.mega2223.aguaengine3d.graphics.objects.misc.Line;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.mathematics.MatrixTranslator;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.physics.objects.advanced.RigidBody;

public class AngularVelocityVisualizer extends Line {
    final int xi, yi, zi;
    final float[] vertices;
    final float[] bufferV4 = new float[4], bufferRot = new float[4];
    final float[] bufferM4 = new float[16];
    final Model model;
    final RigidBody rigidBody;

    public AngularVelocityVisualizer(Model m, RigidBody r, int beginIndex) {
        super(0, 0, 1);
        xi = beginIndex; yi = beginIndex + 1;zi = beginIndex + 2;
        this.model = m;
        this.rigidBody = r;
        vertices = model.getRelativeVertices();
    }

    @Override
    public void doLogic(int iteration) {
        super.doLogic(iteration);
        //setStart(vertices[xi],vertices[yi],vertices[zi]);
        VectorTranslator.copy(vertices[xi],vertices[yi],vertices[zi], bufferV4);
        model.getRotationMatrix(bufferM4);
        MatrixTranslator.multiplyVec4Mat4(bufferV4,bufferM4);
        VectorTranslator.addToVector(rigidBody.x(),rigidBody.y(),rigidBody.z(),bufferV4);
        setStart(bufferV4);
        rigidBody.getLocalPointVelocity(bufferV4,bufferRot);
        VectorTranslator.addToVector(bufferRot,bufferV4);
        setEnd(bufferRot);
    }
}
