package net.mega2223.aguaengine3d.physics;

public interface Material {
    float getFriction();
    float getRestitution();

    Material DEFAULT = new Material() {
        @Override
        public float getFriction() {
            return .25F;
        }

        @Override
        public float getRestitution() {
            return .25F;
        }
    };
}
