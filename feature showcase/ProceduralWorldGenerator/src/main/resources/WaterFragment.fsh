#version 330 core

in vec4 worldCoord;
in vec4 objectiveCoord;
in vec4 perVertexNormal;

//--@maxLightsConstant

uniform vec4 color2; // uniform color
uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 waterColor = vec4(.3,.3,.6,1);
uniform vec4 fogColor = vec4(.5,.5,.6,1);
uniform vec3 lightDir = normalize(vec3(0,1,0));

uniform int useAlphaForFog = 0;
uniform int iteration;

out vec4 color;

//--@mixFogFunction


void main(){
    float dp = dot(vec3(0,-1,0),-lightDir);
    dp = clamp(dp, 0, 1);
    color = mix(waterColor,mix(vec4(0,0,0,1),fogColor,0.25F),1-dp);
    color.a = 1;

    mixFog();
}