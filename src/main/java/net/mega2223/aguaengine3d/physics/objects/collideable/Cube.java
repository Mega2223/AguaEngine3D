package net.mega2223.aguaengine3d.physics.objects.collideable;

import net.mega2223.aguaengine3d.graphics.objects.modeling.Mesh;
import net.mega2223.aguaengine3d.mathematics.VectorTranslator;
import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.RigidBody;
import net.mega2223.aguaengine3d.physics.collisions.Collideable;

public class Cube extends RigidBody implements Collideable {

    private static final float[] buffer = new float[4];
    float[] vertices = Mesh.CUBE.getVertices();

    public Cube(float mass) {
        super(mass);
    }

    public boolean collides(float x, float y, float z) {
        buffer[0] = x; buffer[1] = y; buffer[2] = z;
        toLocalCoordinateSystem(buffer);
        return buffer[0] <= 1 && buffer[0] >= -1 &&
                buffer[1] <= 1 && buffer[1] >= -1 &&
                buffer[2] <= 1 && buffer[2] >= -1;
    }

    public float maxRadius() {
        return 2; //sqrt 2 mas fds
    }

    public void getContactNormal(float[] coord, @Modified float[] result) {
        VectorTranslator.copy(coord,buffer);
        toLocalCoordinateSystem(buffer);
        // f_mx(p) = max(0,min(-p.x+1,p.x+1))
        //f(p) = min(f_mx(p),f_my(p),f_mz(p))
        float colX = Math.max(0,Math.min(-buffer[0] + 1, buffer[0] + 1));
        float colY = Math.max(0,Math.min(-buffer[1] + 1, buffer[1] + 1));
        float colZ = Math.max(0,Math.min(-buffer[2] + 1, buffer[2] + 1));

        int contactAxis = colX > colY ? colX > colZ ? 0 : 2 : colY > colZ ? 1 : 2;
        //float contactDepth = contactAxis == 0 ? colX : contactAxis == 1 ? colY : colZ;
        result[0] = contactAxis == 0 ? colX : 0;
        result[1] = contactAxis == 1 ? colY : 0;
        result[2] = contactAxis == 2 ? colZ : 0;
        result[3] = 0;
        toGlobalCoordinateSystem(result);
    }

    public float getCollision(Collideable c, float[] contactNormalDest) {
        //TODO
        if(c instanceof Sphere){

        } else if (c instanceof Cube) {

        } else if (c instanceof FixedPlane) {
            FixedPlane f = ((FixedPlane) c);

        }
        return 0F;
    }
}
