import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Geometry {

    public static final float DEG_60 = (float) Math.toRadians(60);
    public static final float SQRT_3 = (float) Math.sqrt(3);

    private Geometry(){}
    private static final float[][] bufferMat = new float[4][16];

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
        return new Model(vertices,indices,null);
    }

    public static Model genPolyhedron(int m, int n){
        List<Model> models = new ArrayList<>();
        Model pentagon = genPolygon(5, 1);
        Model hexagon = genPolygon(6, 1);
        float[] pentaV = pentagon.getRelativeVertices(); int[] pentaI = pentagon.getIndices();
        float[] hexaV = hexagon.getRelativeVertices(); int[] hexaI = hexagon.getIndices();

        float pentaLen = (float) Math.sqrt(
                (pentaV[0] - pentaV[4]) * (pentaV[0] - pentaV[4]) +
                (pentaV[1] - pentaV[5]) * (pentaV[1] - pentaV[5]) +
                (pentaV[2] - pentaV[6]) * (pentaV[2] - pentaV[6])
        );
        for (int i = 0; i < pentaV.length; i++) {
            pentaV[i] /= pentaLen;
        }

        models.add(new Model(pentaV,pentaI,new SolidColorShaderProgram(.5f,.7f,.5f)));

        ArrayList<Float> vertices = new ArrayList<>();

        genVertices(1);

        Model[] ret = new Model[models.size()];
        ret = models.toArray(ret);
        return ModelUtils.mergeModels(ret);
    }

    static List<Float> genVertices(float h){
        List<Float> ret = new ArrayList<>();
//        float[] equilateralTriangle = {-.5F,0,0,0, 0, SQRT_3 / 2,0,0, .5F,0,0,0};
//        for (int i = 0; i < 16; i++) { equilateralTriangle[i] *= h / SQRT_3; }
        
        return ret;
    }
}
