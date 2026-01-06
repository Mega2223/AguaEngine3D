package net.mega2223.aguaengine3d.physics;

import net.mega2223.aguaengine3d.misc.annotations.Modified;
import net.mega2223.aguaengine3d.physics.collisions.CollisionMath;

public interface PhysicsObject {
    void applyAcceleration(float ax, float ay, float az);
    void applyVelocity(float vx, float vy, float vz);
    void applyTranslation(float x, float y, float z);

    void setVelocity(float vx, float vy, float vz);
    void setCoordinates(float x, float y, float z);

    float getMass();
    float getInverseMass();

    void update(float deltaT);

    float x(); float y(); float z();
    float vx(); float vy(); float vz();

    /** Converts a global coordinate to a coordinate from the object's internal coordinate system*/
    void toLocalCoordinateSystem(@Modified float[] vec3);

    /** Converts a coordinate from the object's internal coordinate system to a global coordinate*/
    void toGlobalCoordinateSystem(@Modified float[] vec3);

    /** Gets the relative velocity of the point in relation to the object
     * @param point point in world coordinates
     * @param pointVelocity velocity in world coordinates
     * @param dest returns the velocity of the point in relation to the object in world coordinates
     * */
    void toLocalVelocity(float[] point, float[] pointVelocity, @Modified float[] dest);

    void toGlobalVelocity(float[] point, float[] pointVelocity, @Modified float[] dest);

    Material getMaterial(); // para aggregates vamos ter que mudar algumas coisas

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

    default void applyTranslation(float[] translation){
        applyTranslation(translation[0], translation[1], translation[2]);
    }
    default void getCoords(@Modified float[] dest){
        dest[0] = x(); dest[1] = y(); dest[2] = z();
    }

    default void getVelocity(@Modified float[] dest){
        dest[0] = vx(); dest[1] = vy(); dest[2] = vz();
    }

    default float getClosingVelocity(float[] coord, float[] velocity){
        return getClosingVelocity(coord[0],coord[1],coord[2],velocity[0],velocity[1],velocity[2]);
    }

    default float getClosingVelocity(float x, float y, float z, float vx, float vy, float vz){
        return CollisionMath.closingVelocity(x(),y(),z(),vx(),vy(),vz(),x,y,z,vx,vy,vz);
    }

    default PhysicsObject getActor(){
        return this;
        // TODO o getMaterial pode ter uma lógica semelhante?
        //  O foda é ter os agregados, mas acho que dá pra
        //  resolver todas as colisões em formas de primitivas
    }

    default void applyVelocity(float[] velocity){
        applyVelocity(velocity[0],velocity[1],velocity[2]);
    }
}
