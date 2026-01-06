package net.mega2223.aguaengine3d.physics.advanced;

import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.PhysicsObject;

public interface Rotatable extends PhysicsObject {

    void getRotationMatrix(@Modified float[] m4);
    void getRotationQuaternion(@Modified float[] q4);

    /**
     * Returns the velocity of the point with respect to the object, if the object is moving at a certain
     * direction then so is the point. Rotating objects may have different velocities for different points
     * in the same instant.
     * @param point point in world coordinates
     * @param dest returns the velocity in world coordinates
     * */
    void getLocalPointVelocity(float[] point, @Modified float[] dest);

    /**
     * Applies a force at the specified point,
     * where f is a world oriented force and
     * p is a point in world coordinates
     * */
    void applyForce(float fx, float fy, float fz, float px, float py, float pz);

    /**
     * Applies an impulse at the specified point,
     * where i is a world oriented force and
     * p is a point in world coordinates
     * */
    void applyImpulse(float ix, float iy, float iz, float px, float py, float pz);

    void applyRotationalTranslation(float rx, float ry, float rz, float px, float py, float pz);

    /**
     * Applies an angular torque to the object (a change of rotation with respect to the object's inertia)
     * The torque applied is in world coordinates
     * */
    void applyTorque(float tx, float ty, float tz);

    void applyAngularImpulse(float rvx, float rvy, float rvz);

    void applyRotation(float rx, float ry, float rz);

//    void setRotation(float rx, float ry, float rz); TODO

    void setAngularVelocity(float vx, float vy, float vz);

    void getInertialTensor(@Modified float[] dest);

    void getInverseInertialTensor(@Modified float[] dest);

    /**
     * Applies an angular torque to the object (a change of rotation with respect to the object's inertia)
     * The torque applied is in world coordinates
     * */
    default void applyTorque(float[] torque){
        applyTorque(torque[0],torque[1],torque[2]);
    }

    default void applyRotationalTranslation(float[] amount, float[] point){
        applyRotationalTranslation(amount[0],amount[1],amount[2],point[0],point[1],point[2]);
    }

    /**
     * Applies an impulse at the specified point (in world coordinates)
     * */
    default void applyImpulse(float[] impulse, float[] point){
        applyImpulse(impulse[0],impulse[1],impulse[2],point[0],point[1],point[2]);
    }

    default void applyAngularImpulse(float[] angularImpulse){
        applyAngularImpulse(angularImpulse[0],angularImpulse[1],angularImpulse[2]);
    }
}
