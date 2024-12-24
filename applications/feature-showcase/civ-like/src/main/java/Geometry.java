import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import net.mega2223.aguaengine3d.graphics.objects.modeling.ModelUtils;
import net.mega2223.aguaengine3d.graphics.objects.shadering.SolidColorShaderProgram;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.Utils;

import java.util.ArrayList;
import java.util.List;

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
        SolidColorShaderProgram shader = new SolidColorShaderProgram(.5F, 1F, .6F,1F);

        List<Model> models = new ArrayList<>();
//        Model pentagon = genPolygon(5, 1);
//        Model hexagon = genPolygon(6, 1);
//        float[] pentaV = pentagon.getRelativeVertices(); int[] pentaI = pentagon.getIndices();
//        float[] hexaV = hexagon.getRelativeVertices(); int[] hexaI = hexagon.getIndices();

//        float pentaLen = (float) Math.sqrt(
                //length of pentagon sides
//                (pentaV[0] - pentaV[4]) * (pentaV[0] - pentaV[4]) +
//                (pentaV[1] - pentaV[5]) * (pentaV[1] - pentaV[5]) +
//                (pentaV[2] - pentaV[6]) * (pentaV[2] - pentaV[6])
//        );
//        for (int i = 0; i < pentaV.length; i++) {
//            pentaV[i] /= pentaLen;
//        }

//        models.add(new Model(pentaV,pentaI,new SolidColorShaderProgram(.5f,.7f,.5f)));
        int r = 2;
        List<Float> planeSample = sampleHexagonPlane(-r, -r, r, r);
        planeSample.removeAll(planeSample);

        for (int i = 0; i < 10; i++) {
            planeSample.add(i/10F - .5F);planeSample.add(0F);planeSample.add(0F);planeSample.add(0F);
        }
        for (int i = 0; i < 10; i++) {
            planeSample.add(0f);planeSample.add(i/10F - .5F);planeSample.add(0F);planeSample.add(0F);
        }

        List<Float> finalSample = new ArrayList<>(planeSample.size() * 20);

        Mesh icosahedron = Mesh.ICOSAHEDRON;
        icosahedron = Mesh.CUBE;
        //fixme a questão do ângulo tá ridiculamente imprecisa
        // em um CUBO ele não pega o theta correto
        // é bem possível que alguma fórmula no meio desse processo esteja errada
        // e se não for isso então dá uma olhada em aumentar a precisão
        // pq a tradução só funciona pra 1/4 dos triângulos

        int[] indices = icosahedron.getIndices();
        float[] vertices = icosahedron.getVertices();
        int t = indices.length; final float s = 1F;
        float[] normal = new float[4];
        float[] grid = {0,0,1,0};
        float[] rotAxis = new float[4];
        float[] currentSample = new float[4];
        float[] rotated = new float[4];
        float[] center = new float[4];

        Model icosaModel = icosahedron.toModel(shader);
        models.add(icosaModel);
        ModelUtils.debugIndices(icosaModel);

//        float maxDist = 0;
//        for (int v = 0; v < planeSample.size(); v+=4) {
//            float x = planeSample.get(v), y = planeSample.get(v+1), z = planeSample.get(v+2);
//            maxDist = Math.max(maxDist,VectorTranslator.getMagnitude(x,y,z));
//        }
//        for (int v = 0; v < planeSample.size(); v++) {
//            planeSample.set(v,planeSample.get(v) / maxDist);
//        }

        for (int i = 0; i < t; i+=3) {
            int a = indices[i], b = indices[i+1], c = indices[i+2];
            float aX = vertices[a*4], aY = vertices[a*4+1], aZ = vertices[a*4+2];
            float bX = vertices[b*4], bY = vertices[b*4+1], bZ = vertices[b*4+2];
            float cX = vertices[c*4], cY = vertices[c*4+1], cZ = vertices[c*4+2];

            //TODO era pra ser um ponto equidistante dos outros, mas
            // os triângulos são equiláteros então eu acho que não tem problema???
            // enfim, teste :)
            center[0] = (aX + bX + cX)/3; center[1] = (aY + bY + cY)/3; center[2] = (aZ + bZ + cZ)/3;

            VectorTranslator.getCrossProduct(aX-cX,aY-cY,aZ-cZ,bX-cX,bY-cY,bZ-cZ,normal);
            VectorTranslator.normalize(normal);
            VectorTranslator.getAxisAngle(normal,grid,rotAxis);

            for (int j = 0; j < planeSample.size(); j+=4) {
                currentSample[0] = planeSample.get(j); currentSample[1] = planeSample.get(j+1);
                currentSample[2] = planeSample.get(j+2); currentSample[3] = planeSample.get(j+3);
                VectorTranslator.rotateAlongAxis(currentSample,rotAxis,rotated);
                for (int k = 0; k < 4; k++) { finalSample.add(rotated[k] + center[k]); }
            }
//            System.out.println(i/3);
//            if(i > 4){break;};
        }

        Model points = ModelUtils.plotPoints(
                Utils.toPrimitiveArray(finalSample)
                , .025F
        );
        models.add(new Model(
                points.getRelativeVertices(), points.getIndices(), shader
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
//                System.out.printf(Locale.US,"(%.3f, %.3f),", x,y);
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
