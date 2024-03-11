#version 330 core

in vec4 worldCoord;
in vec4 objectiveCoord;
in vec4 perVertexNormal;

//--@maxLightsConstant

uniform vec4 color2; // uniform color
uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);
uniform vec3 lightDir = normalize(vec3(0,-1,0));

uniform int useAlphaForFog = 0;
uniform int iteration;

out vec4 color;

//--@mixFogFunction

const vec4 green = vec4(.15,.35,.15,1);
const vec4 yellow = vec4(.7,.7,.4,1);
const vec4 brown = vec4(.49,.41,.3,1);
const vec4 test = vec4(0,0,0,1);


void main(){
    float greenBias = (objectiveCoord.y-1.5F)*.75F;
    greenBias = clamp(greenBias,0,1);
    color = mix(yellow,green,smoothstep(0,1,greenBias));
    float browBias = (-objectiveCoord.y-.85)*.5F;
    browBias = clamp(browBias,0,1);
    color = mix(color,brown,browBias);

    float dp = dot(perVertexNormal.xyz,lightDir);
    //dp = dp > 0 ? 1 : smoothstep(-1,1,dp * 2 + 1);
    //smoothstep(-1,1,clamp(dp*2,-1,1));
    dp = clamp(dp, 0, 1);
    //dp = (3F - dp * 2F) * dp * dp;
    color = mix(color,mix(vec4(0,0,0,1),fogColor,0.25F),1-dp);

    mixFog();
}