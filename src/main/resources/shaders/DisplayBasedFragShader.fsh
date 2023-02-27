#version 330 core

in vec2 texturePosition;
in vec4 gl_FragCoord;
uniform sampler2D samplerTexture;

out vec4 color;

void main(){
    vec2 coord = vec2(gl_FragCoord.x/1400,1-gl_FragCoord.y/800);
    color = texture(samplerTexture,coord);

}