package net.mega2223.aguaengine3d.demos.particlesim;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;
import net.mega2223.aguaengine3d.graphics.objects.RenderingContext;
import net.mega2223.aguaengine3d.graphics.objects.modeling.Model;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class Particle extends Model {
    private static final float[] v = {-1,-1,0,0, 1,-1,0,0, -1,1,0,0, 1,1,0,0};
    private static final int[] i = {0,1,2, 1,2,3};
    public static final int C = 1
            ;
    public ParticleShaderProgram shader;
    public float x = 0, y = 0, vX = 0, vY = 0, mass, e;

    public Particle(float m) {
        this(m,Math.random() <= .25 ? 0 : Math.random() >= .5 ? -1 : 1);
    }

    public Particle(float m, float e){
        super(new float[]{-C,-C,0,0, C,-C,0,0, -C,C,0,0, C,C,0,0}, i, new ParticleShaderProgram());
        this.shader = (ParticleShaderProgram) this.getShader(); this.mass = m;
        GL30.glUseProgram(this.shader.id);
        GL30.glUniform1f(this.shader.radius_loc,1.0F);
//        GL30.glUniform1f(this.shader.radius_loc,mass);
        this.e = e;
        this.shader.color[0] = this.e < 0 ? 1 : this.e > 0 ? 0 : 1F;
        this.shader.color[1] = this.e < 0 ? 1 : this.e > 0 ? 0 : 1F;
        this.shader.color[2] = this.e < 0 ? 0 : this.e > 0 ? 1 : 1F;
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