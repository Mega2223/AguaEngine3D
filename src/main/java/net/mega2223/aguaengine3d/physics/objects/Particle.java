package net.mega2223.aguaengine3d.physics.objects;

public class Particle extends PhysicsSystem{

    public Particle(float mass){
        super(mass);
    }

    public void doLogic(float time, float drag, float[] gravity){
        super.doLogic(time,drag,gravity);
    }
}
