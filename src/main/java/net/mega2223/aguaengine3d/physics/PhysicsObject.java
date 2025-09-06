package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.misc.annotations.Modified;

public interface PhysicsObject {
    void applyAcceleration(float ax, float ay, float az);
    void applyVelocity(float vx, float vy, float vz);
    void applyTranslation(float x, float y, float z);

    void setVelocity(float vx, float vy, float vz);
    void setCoordinates(float x, float y, float z);

    float getMass();
    float getInverseMass();

    void update(float deltaT);

    default void applyForce(float fx, float fy, float fz){
        float invMass = getInverseMass();
        applyVelocity(fx*invMass,fy*invMass,fz*invMass);
    }

    default void applyForce(float[] force){
        applyForce(force[0],force[1],force[2]);
    }

    default void applyImpulse(float ix, float iy, float iz){
        float invMass = getInverseMass();
        applyVelocity(ix*invMass,iy*invMass,iz*invMass);
    }

    default void applyImpulse(float[] impulse){
        applyImpulse(impulse[0],impulse[1],impulse[2]);
    }

    float x(); float y(); float z();
    float vx(); float vy(); float vz();

    default void getCoords(@Modified float[] dest){
        dest[0] = x(); dest[1] = y(); dest[2] = z();
    }

    default void getVelocity(@Modified float[] dest){
        dest[0] = vx(); dest[1] = vy(); dest[2] = vz();
    }

    default PhysicsObject getActor(){
        return this;
    }
}
