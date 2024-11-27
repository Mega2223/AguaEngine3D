package net.mega2223.aguaengine3d.demos.particlesim;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;

import java.util.List;

public interface Force {
    Force GRAVITY = (context, p) -> {
        for (Renderable act : context) {
            if (!(act instanceof Particle) || act == p) {
                continue;
            }
            Particle p2 = (Particle) act;
            float dX = p.x - p2.x, dY = p.y - p2.y, dist = (float) Math.sqrt(dX * dX + dY * dY);
            if(!Float.isFinite(dist)){continue;}
            dist = Math.max(dist,1);
            dX /= dist; dY /= dist;
            float fMag = -(p.mass * p2.mass) / (dist * dist);
//            System.out.println("fGrav="+fMag);
            p.applyForce(dX * fMag, dY * fMag);
        }
    };
    Force REPULSION = (context, p) -> {
        float rK = 8F;
        for (Renderable act : context) {
            if (!(act instanceof Particle) || act == p) {
                continue;
            }
            Particle p2 = (Particle) act;
            float dX = p.x - p2.x, dY = p.y - p2.y, dist = (float) Math.sqrt(dX * dX + dY * dY);
            if(!Float.isFinite(dist)){continue;}
            dist = Math.max(dist,.7F);
            dX /= dist; dY /= dist;
            float fMag = (rK * p.mass * p2.mass) / (dist * dist * dist);
//            System.out.println("fRep="+fMag);
            p.applyForce(dX * fMag, dY * fMag);
        }
    };
    float k1 = .03F, k2 = .0F;
//float k1 = .001F, k2 = .001F;
    Force DRAG = (context, p) -> {
        float vX = p.vX, vY = p.vY, speed = (float) Math.sqrt(vX * vX + vY * vY);
        if(speed <= 0){return;}
        if(Float.isInfinite(speed)){speed = 10;}
        vX /= speed; vY /= speed;
        p.applyForce(-vX * speed * k1, -vY * speed *k1);
    p.applyForce(-vX * speed * speed * k2, -vY * speed * speed * k2);
    };

    Force STR = (context, p) -> {
        for (Renderable act : context) {
            if (!(act instanceof Particle)) {
                continue;
            }
            Particle p2 = (Particle) act;
            float x = p.x - p2.x, y = p.y - p2.y, r = (float) Math.sqrt(x*x + y*y);
            if(r <= 0){continue;} x /= r; y /= r;
            float mag = - 36 / (100*r*r*r*r+1);
            p.applyForce(x * mag,y * mag);
        }
    };

    void apply(List<Renderable> context, Particle p);
}
