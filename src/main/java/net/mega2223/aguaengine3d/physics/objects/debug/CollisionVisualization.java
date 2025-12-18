package net.mega2223.aguaengine3d.physics.objects.debug;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.misc.Line;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.util.ArrayList;
import java.util.List;

public class CollisionVisualization implements Renderable {
    float[] position = new float[4];
    float[] translation = new float[4];
    float[] force = new float[4];
    float timeToRemove = 5F;

    public static List<CollisionVisualization> COLLISION_OUTPUT_STREAM = new ArrayList<>();

    Line[] lines = {
            new Line(1,0,0),
            new Line(0,1,0),
            new Line(0,0,1),
    };

    public CollisionVisualization(float[] position, float[] translation, float[] force) {
        this.position = position.clone();
        this.translation = translation.clone();
        this.force = force.clone();

        for (int i = 0; i < 3; i++) {
            lines[i].setStart(position);
        }
        VectorTranslator.scaleVector(translation,50);
        VectorTranslator.scaleVector(force,300);

        lines[0].setDirection(translation);
        lines[1].setDirection(force);
        lines[2].setDirection(0,0,0);
    }

    @Override
    public void draw() {
        if(timeToRemove < 0){
            return; //FIXME Kkkkkkkkkkkkkkk
        }
        for (int i = 0; i < 2; i++) {
//            System.out.println("l"+i);
//            System.out.println(lines[i]);
            lines[i].draw();
        }
    }

    @Override
    public void drawForceShader(ShaderProgram shader) {
        for (int i = 0; i < 3; i++) {
            lines[i].drawForceShader(shader);
        }
    }

    @Override
    public void doLogic(int iteration) {
        for (int i = 0; i < 3; i++) {
            lines[i].doLogic(iteration);
        }
        timeToRemove -= .02F;
    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        for (int i = 0; i < 3; i++) {
            lines[i].setUniforms(iteration,projectionMatrix);
        }
    }

    @Override
    public ShaderProgram getShader() {
        return lines[0].getShader();
    }

    @Override
    public boolean isValid() {
        return timeToRemove >= 0;
    }
}
