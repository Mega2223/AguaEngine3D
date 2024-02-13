#version 330 core

in vec4 worldCoord;
in vec4 objectiveCoords;
in vec3 primitiveNormal;
in vec4 perVertexNormal;

//--@maxLightsConstant

uniform vec4 color2; // uniform color
uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);

uniform int useAlphaForFog = 0;
uniform int iteration;

out vec4 color;

//--@mixFogFunction

const vec4 green = vec4(.35,.55,.35,1);
const vec4 yellow = vec4(.7,.7,.4,1);
const vec4 brown = vec4(.49,.41,.3,1);
const vec4 test = vec4(0,0,0,1);


void main(){
    float greenBias = (objectiveCoords.y-1.5F)*.75F;
    greenBias = clamp(greenBias,0,1);
    color = mix(yellow,green,greenBias);
    float browBias = (-objectiveCoords.y-.85)*.5F;
    browBias = clamp(browBias,0,1);
    color = mix(color,brown,browBias);
    float s = sin(float(iteration)/2400F); float c = cos(float(iteration)/2400F);
    float dp = dot(perVertexNormal.xyz,normalize(vec3(s,-c,1)));
    float brightness = -c*2;
    dp += brightness * .5F;
    dp = clamp(dp, -0.6, 1);
    color = mix(color,fogColor,dp);

    mixFog();
}