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
     * where both f and p are world-oriented coordinates
     * */
    void applyForce(float fx, float fy, float fz, float px, float py, float pz);

    void applyRotationalCorrection(float rx, float ry, float rz, float px, float py, float pz);

    void applyTorque(float tx, float ty, float tz);

    void applyRotation(float rx, float ry, float rz);

//    void setRotation(float rx, float ry, float rz); TODO

    void setAngularVelocity(float vx, float vy, float vz);

    void getInertialTensor(@Modified float[] dest);

    void getInverseInertialTensor(@Modified float[] dest);

    default void applyTorque(float[] torque){
        applyTorque(torque[0],torque[1],torque[2]);
    }

    default void applyRotationalCorrection(float[] amount, float[] point){
        applyRotationalCorrection(amount[0],amount[1],amount[2],point[0],point[1],point[2]);
    }
}
