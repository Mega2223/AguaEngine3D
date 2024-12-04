package net.mega2223.aguaengine3d.demos.particlesim;

import net.mega2223.aguaengine3d.graphics.objects.Renderable;

import java.util.List;

public interface Force {
    float MAX_F = 1E-1F;

    class Gravity implements Force{
        final float forceFactor, maxForce;
        public Gravity(float forceFactor, float maxForce){
            this.forceFactor = forceFactor; this.maxForce = maxForce;
        }
        @Override
        public void apply(List<Renderable> context, Particle p) {
            for (Renderable act : context) {
                if (!(act instanceof Particle) || act == p) {
                    continue;
                }
                Particle p2 = (Particle) act;
                float dX = p.x - p2.x, dY = p.y - p2.y, dist = (float) Math.sqrt(dX * dX + dY * dY);
                if(!Float.isFinite(dist)){continue;}
                dX /= dist; dY /= dist;
                float fMag = -(p.mass * p2.mass) / (dist * dist);
                fMag = Math.signum(fMag) * Math.min(Math.abs(fMag),maxForce);
                p.applyForce(dX * fMag, dY * fMag);
            }
        }
    }

    class Electromag implements Force{
        final float forceFactor, maxForce;
        public Electromag(float forceFactor, float maxForce){
            this.forceFactor = forceFactor; this.maxForce = maxForce;
        }
        @Override
        public void apply(List<Renderable> context, Particle p) {
            for (Renderable act : context) {
                if (!(act instanceof Particle) || act == p) {
                    continue;
                }
                Particle p2 = (Particle) act;
                float dX = (p.x - p2.x) + 0*.15F*(p.y - p2.y), dY = p.y - p2.y - 0*.15F*(p.x- p2.x), dist = (float) Math.sqrt(dX * dX + dY * dY);
                if(!Float.isFinite(dist)){continue;}
                dX /= dist; dY /= dist;
                float fMag = (p2.e * p.e) / (dist * dist);
                fMag = Math.signum(fMag) * Math.min(Math.abs(fMag),maxForce);
                p.applyForce(dX * fMag, dY * fMag);
            }
        }
    }

    class Repulsion implements Force{
        final float forceFactor, maxForce;
        public Repulsion(float forceFactor, float maxForce){
            this.forceFactor = forceFactor; this.maxForce = maxForce;
        }
        @Override
        public void apply(List<Renderable> context, Particle p) {
            for (Renderable act : context) {
                if (!(act instanceof Particle) || act == p) {
                    continue;
                }
                Particle p2 = (Particle) act;
                float dX = p.x - p2.x, dY = p.y - p2.y, dist = (float) Math.sqrt(dX * dX + dY * dY);
                if(!Float.isFinite(dist)){continue;}
                dist = Math.max(dist,4F);
                dX /= dist; dY /= dist;
                float fMag = (forceFactor * p.mass * p2.mass) / (dist * dist * dist);
                fMag = Math.signum(fMag) * Math.min(Math.abs(fMag),maxForce);
                p.applyForce(dX * fMag, dY * fMag);
            }
        }
    }

    class Drag implements Force {
        final float k1 , k2;
        public Drag(){
            this(.008F, .0001F);
        }
        public Drag(float k1, float k2){
            this.k1 = k1; this.k2 = k2;
        }

        @Override
        public void apply(List<Renderable> context, Particle p) {
            {
                float vX = p.vX, vY = p.vY, speed = (float) Math.sqrt(vX * vX + vY * vY);
                if(speed <= 0){return;}
                if(Float.isInfinite(speed)){speed = 10;}
                vX /= speed; vY /= speed;
                float mag1 = speed * k1 * p.mass, mag2 = speed * speed * k2 * p.mass;
                p.applyForce(-vX * mag1, -vY * mag1);
                p.applyForce(-vX * mag2, -vY * mag2);
            }
        }
    }

    class Strong implements Force{
        final float maxForce, ringDist, ringRange, scale;

       public Strong(float maxForce, float ringDist, float ringRange, float scale) {
           this.maxForce = maxForce;
           this.ringDist = ringDist;
           this.ringRange = ringRange;
           this.scale = scale;
       }
       Drag apply = new Drag(0.1F,0.05F);
       @Override
       public void apply(List<Renderable> context, Particle p) {
           for (Renderable act : context) {
               if (!(act instanceof Particle) || act == p) {
                   continue;
               }
               Particle p2 = (Particle) act;
               float dX = (p.x - p2.x), dY = p.y - p2.y, dist = (float) Math.sqrt(dX * dX + dY * dY);
               if(!Float.isFinite(dist)){continue;}
               dX /= dist; dY /= dist;
               dist = -(dist - ringDist);
               boolean charge = p.e >= 0 && p2.e >= 0;
               charge = true;
               if(charge && Math.abs(dist) <= ringRange && Math.abs(p.mass / p2.mass) >= .9F && Math.abs(p.mass / p2.mass) <= 1.1F){
                   float mag = Math.signum(dist) * Math.min(Math.abs(dist*scale),maxForce);
                   mag *= Math.abs(p.mass * p2.mass);
                   p.applyForce(dX*mag,dY*mag);
                   apply.apply(context,p);
//                   System.out.println("yes" + dist + "   " + dX);
               }
           }
       }
   }

    void apply(List<Renderable> context, Particle p);
}
