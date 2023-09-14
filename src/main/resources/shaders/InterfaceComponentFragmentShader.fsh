#version 330 core

in vec2 texturePosition;

uniform sampler2D samplerTexture;

out vec4 color;

void main() {
    color = texture(samplerTexture,texturePosition);

}
