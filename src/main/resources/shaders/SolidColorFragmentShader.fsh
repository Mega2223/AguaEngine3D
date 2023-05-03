#version 330 core

in vec4 worldCoord;
in vec3 normalAligment;

uniform vec3 color2;

uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);

out vec4 color;

const vec3 lightSource = vec3(0,1,0);
const vec4 lightColor = vec4(1,1,1,1);

void main(){

    color = vec4(color2.xyz,1);
    color = clamp(color,0,1);


    float range = (distance(worldCoord,vec4(0,0,0,0)));

    range-=fogStart;
    range/=fogDissolve;

    range = clamp(range,0,1);

    color = mix(color,lightColor,clamp(dot(lightSource,normalAligment),0,1));
    color = mix(color,fogColor,range);


}