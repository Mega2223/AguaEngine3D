package net.mega2223.aguaengine3d.demos.particlesim;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class Particle extends Model {
    private static final float[] v = {-1,-1,0,0, 1,-1,0,0, -1,1,0,0, 1,1,0,0};
    private static final int[] i = {0,1,2, 1,2,3};
    public ParticleShaderProgram shader;
    public float x = 0, y = 0, vX = 0, vY = 0, mass, e = -1;

    public Particle(float m) {
        super(new float[]{-m,-m,0,0, m,-m,0,0, -m,m,0,0, m,m,0,0}, i, new ParticleShaderProgram());
        this.shader = (ParticleShaderProgram) this.getShader(); this.mass = m;
        GL30.glUseProgram(this.shader.id);
        GL30.glUniform1f(this.shader.radius_loc,mass);
    }

    public Particle(){
        this(1);
    }

    public void applyForce(float fX, float fY){
        this.vX += fX / this.mass;
        this.vY += fY / this.mass;
    }

    public void doLogic(int iteration, RenderingContext context, Iterable<Force> forces) {
        x += vX; y+= vY;
        List<Renderable> renderables = context.getObjects();
        for (Force act : forces) {
            act.apply(renderables,this);
        }
        this.coords[0] = x; this.coords[1] = y;
    }
}