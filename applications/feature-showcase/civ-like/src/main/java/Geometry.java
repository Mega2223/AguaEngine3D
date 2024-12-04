import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.util.ArrayList;
import java.util.List;

public class Geometry {
    private Geometry(){}

    public static Model genPolygon(int edges, float radius){
        float[] vertices = new float[4 + edges * 4];
        for (int i = 0; i < edges * 4; i+=4) {
            double angle = 0.5F * Math.PI * (((float) i) / edges);
            vertices[i] = (float) (radius * Math.sin(angle));
            vertices[i+1] = (float) (radius * Math.cos(angle));
        }
        int[] indices = new int[edges * 3];

        int c = 0;
        for (int i = 0; i < indices.length; i+=3) {
            indices[i] = (vertices.length / 4) - 1;
            indices[i + 1] = c % edges;
            indices[i + 2] = (c+1) % edges;
            c++;
        }

        VectorTranslator.debugVector(vertices);
        System.out.println(vertices.length / 4);
        for (int i = 0; i < vertices.length; i+=4) {
            VectorTranslator.debugVector(vertices[i],vertices[i+1],vertices[i+2],vertices[i+3]);
        }
        VectorTranslator.debugVector("i",indices);
        System.out.println(indices.length / 3);
        return new Model(vertices,indices,null);
    }
}
