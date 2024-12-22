import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
                //length of pentagon sides
                (pentaV[0] - pentaV[4]) * (pentaV[0] - pentaV[4]) +
                (pentaV[1] - pentaV[5]) * (pentaV[1] - pentaV[5]) +
                (pentaV[2] - pentaV[6]) * (pentaV[2] - pentaV[6])
        );
        for (int i = 0; i < pentaV.length; i++) {
            pentaV[i] /= pentaLen;
        }

//        models.add(new Model(pentaV,pentaI,new SolidColorShaderProgram(.5f,.7f,.5f)));

        models.add(new Model(
                ModelUtils.plotPoints(
                        Utils.toPrimitiveArray(sampleHexagonPlane(-6,-6,6,6))
                ,.025F
                ).getRelativeVertices(), new int[0],new SolidColorShaderProgram(.5F,.5F,.6F)
        ));

        Model[] ret = new Model[models.size()];
        ret = models.toArray(ret);
        return ModelUtils.mergeModels(ret);
    }

    static List<Float> sampleHexagonPlane(float minX, float minY, float maxX, float maxY){
        List<Float> ret = new ArrayList<>();
        boolean even = true;
//        for (float y = 0; y < maxY-minY; y+= 3F/2F) {
//            float start = even ? 0 : SQRT_3 * 0.5F;
//            for (float x = 0; x < maxX-minX; x+= SQRT_3) {
//                System.out.printf("P = (%.3f %.3f)", x,y);
//                float sAX = start + x - SQRT_3 * 0.5F, sAY = y - .5F,
//                        sBX = start + x, sBY = y - 1F,
//                        sCX = start + x - SQRT_3 * 0.5F, sCY = y + .5F;;
//                ret.add(sAX); ret.add(sAY); ret.add(0F); ret.add(0F);
//                ret.add(sBX); ret.add(sBY); ret.add(0F); ret.add(0F);
//                ret.add(sCX); ret.add(sCY); ret.add(0F);ret.add(0F);
//                System.out.printf(" -> (%.3f %.3f) (%.3f %.3f) (%.3f %.3f)\n",sAX, sAY, sBX, sBY, sCX,sCY);
//            }
//            even = !even;
//        }
        for (int r = 0;; r++){
            float y = r * .5F + ((r + 1) >> 1) *.5F - .5F;
            if(y > maxY - minY){break;}
            for (int c = 0;; c++){
                boolean odd = (r % 4) / 2 == 1;
                float x = c * SQRT_3 + (odd ? 0 : SQRT_3 / 2);
                if(x > maxX - minX){break;}
                System.out.printf(Locale.US,"(%.3f, %.3f),", x,y);
                ret.add(x); ret.add(y); ret.add(0F); ret.add(0F);
            }
        }
        float tX = ret.get(0), tY = ret.get(1), cld = Float.MAX_VALUE;
        final float mx = (maxX-minX)/2, my = (maxY-minY)/2;
        for (int i = 0; i < ret.size(); i+=4) {
            float x = ret.get(i), y = ret.get(i + 1);
            float dx = x - mx, dy = y - my;
            float dist = (float) Math.sqrt(dx*dx + dy*dy);
            if(dist < cld){
                cld = dist; tX = x; tY = y;
            }
        }
        for (int i = 0; i < ret.size(); i+=4) {
            ret.set(i,ret.get(i)-tX);
            ret.set(i+1,ret.get(i+1)-tY);
        }
        return ret;
    }
}
