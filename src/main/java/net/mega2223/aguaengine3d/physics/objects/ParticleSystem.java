package net.mega2223.aguaengine3d.physics.objects;

public class ParticleSystem extends PhysicsSystem {
    final float radius;

    public ParticleSystem(float mass){
        this(mass,1);
    }
    public ParticleSystem(float mass, float radius){
        super(mass);
        this.radius = radius;
    }

    @Override
    public float getInteractionRadius() {
        return radius;
    }

    public void doLogic(float time){
        super.doLogic(time);
    }
}
