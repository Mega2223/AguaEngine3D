package net.mega2223.aguaengine3d.physics;

public interface Material {
    float getFriction();
    float getRestitution();

    Material STANDARD = new Material() {
        @Override
        public float getFriction() {
            return .5F;
        }

        @Override
        public float getRestitution() {
            return .5F;
        }
    };
}
