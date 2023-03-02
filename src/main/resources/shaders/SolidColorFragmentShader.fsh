#version 330 core


uniform vec3 color2;

uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);

out vec4 color;

void main(){
    color = vec4(color2.xyz,1);

    float range = (gl_FragCoord.z / gl_FragCoord.w);

    range-=fogStart;
    range/=fogDissolve;

    range = clamp(range,0,1);

    color = mix(color,fogColor,range);

}