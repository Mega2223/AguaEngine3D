#version 330 core

in vec2 texturePosition;
in vec4 gl_FragCoord;
uniform sampler2D samplerTexture;

uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);

out vec4 color;

void main(){
    vec2 coord = vec2(gl_FragCoord.x/1400,1-gl_FragCoord.y/800);
    color = texture(samplerTexture,coord);

    float range = (distance(worldCoord,vec4(0,0,0,0)));

    range-=fogStart;
    range/=fogDissolve;

    range = clamp(range,0,1);

    color = mix(color,fogColor,range);

}