#version 330 core

in vec2 texturePosition;
in vec4 gl_FragCoord;
uniform sampler2D samplerTexture;

out vec4 color;

void main(){

    color = texture(samplerTexture,texturePosition);

}