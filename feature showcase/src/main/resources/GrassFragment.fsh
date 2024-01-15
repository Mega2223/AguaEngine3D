#version 330 core

in vec4 worldCoord;
in vec4 objectiveCoords;
in vec3 normal;
//--@maxLightsConstant

uniform vec4 color2; // uniform color
uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);

uniform int useAlphaForFog = 0;

out vec4 color;

//--@mixFogFunction

const vec4 green = vec4(.05,.6,.05,1);
const vec4 yellow = vec4(.7,.7,.4,1);
const vec4 brown = vec4(.49,.41,.3,1);
const vec4 test = vec4(1,1,1,1);


void main(){
    float greenBias = (objectiveCoords.y-1.5F)*.75F;
    greenBias = clamp(greenBias,0,1);
    color = mix(yellow,green,greenBias);
    float browBias = (-objectiveCoords.y-.85)*.5F;
    browBias = clamp(browBias,0,1);
    color = mix(color,brown,browBias);
    color = mix(color,test,dot(normal,clamp(vec3(1,0,0),-1,1)));

    mixFog();
}