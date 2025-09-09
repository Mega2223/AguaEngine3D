package net.mega2223.aguaengine3d.graphics.objects.modeling.utils;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.shadering.ShaderProgram;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;

import java.util.ArrayList;
import java.util.List;

public class VertexTracker implements Renderable {
    List<Model> models  = new ArrayList<>();
    ShaderProgram s = new SolidColorShaderProgram(1,0,0);
    Model model;

    public VertexTracker(Model model){
        float[] v = model.getRelativeVertices();
        for (int i = 0; i < v.length; i+=4) {
            Model cube = Mesh.CUBE.toModel(s);
            ModelUtils.scaleModel(cube,.1F);
            ModelUtils.translateModel(cube,v[i],v[i+1],v[i+2]);
            models.add(cube);
        }
        this.model = model;
    }

    @Override
    public void draw() {
        models.forEach(Model::draw);
    }

    @Override
    public void drawForceShader(ShaderProgram shader) {
        models.forEach((m)->m.drawForceShader(shader));
    }

    float[] rotationBuffer = new float[16];
    float[] posBuffer = new float[4];
    @Override
    public void doLogic(int iteration) {
//        for (int i = 0; i < models.size(); i++) {
//            Model m = models.get(i);
//            model.getRotationMatrix(r);
//            m.setRotationMatrix(rotationBuffer);
//        }
        model.getRotationMatrix(rotationBuffer);
        s.setRotationMatrix(rotationBuffer);
        model.getCoords(posBuffer);
        models.forEach((m)->{m.setCoords(posBuffer);}); // FIXME o consumer é um objeto !!!
    }

    @Override
    public void setUniforms(int iteration, float[] projectionMatrix) {
        models.forEach((m)->m.setUniforms(iteration,projectionMatrix));
    }

    @Override
    public ShaderProgram getShader() {
        return s;
    }
}
