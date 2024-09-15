package net.mega2223.aguaengine3d.graphics.utils;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.misc.Line;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;

public abstract class WorldspaceDataVisualizer implements Renderable {
    Renderable object;
    private WorldspaceDataVisualizer(Renderable object){
        this.object = object;
    }

    @Override
    public void doLogic(int iteration) {
        object.doLogic(iteration);
    }
    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        object.setUniforms(iteration,projectionMatrix);
    }
    @Override
    public ShaderProgram getShader() {
        return object.getShader();
    }
    @Override
    public void draw() {
        object.draw();
    }
    @Override
    public void drawForceShader(ShaderProgram shader) {
        object.drawForceShader(shader);
    }

    public static void visualize3DVector(RenderingContext context, float[] memoryAccess, float x, float y, float z, float magnitudeDot){
        Line line = new Line(1,0,0);
        line.setStart(x,y,z);
        WorldspaceDataVisualizer visualizer = new WorldspaceDataVisualizer(line) {
            @Override
            public void doLogic(int iteration) {
                super.doLogic(iteration);
                line.setEnd(x + memoryAccess[0], y + memoryAccess[1], z + memoryAccess[2]);
            }
        };
        context.addObject(visualizer);
    }
    public static void visualize3DVector(RenderingContext context, float[] beginningMemoryAcess,float[] endMemoryAccess, float magnitudeDot){
        Line line = new Line(1,0,0);

        line.setStart(beginningMemoryAcess[0],beginningMemoryAcess[1],beginningMemoryAcess[2]);
        WorldspaceDataVisualizer visualizer = new WorldspaceDataVisualizer(line) {
            @Override
            public void doLogic(int iteration) {
                super.doLogic(iteration);
                line.setStart(beginningMemoryAcess[0],beginningMemoryAcess[1],beginningMemoryAcess[2]);
                line.setEnd(beginningMemoryAcess[0] + endMemoryAccess[0], beginningMemoryAcess[1] + endMemoryAccess[1], beginningMemoryAcess[2] + endMemoryAccess[2]);
            }
        };
        context.addObject(visualizer);
    }

}
