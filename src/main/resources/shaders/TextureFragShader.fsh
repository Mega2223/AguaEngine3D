#version 330 core

in vec2 texturePosition;
in vec4 gl_FragCoord;
uniform sampler2D samplerTexture;

uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);

out vec4 color;

void main(){
    color = texture(samplerTexture,texturePosition);

    float range = (gl_FragCoord.z / gl_FragCoord.w);

    range-=fogStart;
    range/=fogDissolve;

    range = clamp(range,0,1);

    color = mix(color,fogColor,range);

}
