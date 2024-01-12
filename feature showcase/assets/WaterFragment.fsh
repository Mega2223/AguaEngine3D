#version 330 core

in vec4 worldCoord;
in vec4 objectiveCoord;

//--@maxLightsConstant

uniform vec4 color2; // uniform color
uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);

uniform int useAlphaForFog = 0;

out vec4 color;

//--@mixFogFunction

void main(){
    color = vec4(.4,.4,1,1);
    mixFog();
}