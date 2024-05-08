#version 330 core

in vec4 worldCoord;
in vec4 objectiveCoord;
in vec4 fragmentNormal;

//--@maxLightsConstant

uniform vec4 color2; // uniform color

uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);
uniform vec4[MAX_LIGHTS] lights;
uniform vec4[MAX_LIGHTS] lightColors;

uniform int useAlphaForFog = 0;

out vec4 color;

//--@calculateLightInfluenceFunction

//--@stepToVarFunction

//--@mixFogFunction

void main(){
    color = fogColor;

    for(int i = 0; i < MAX_LIGHTS; i++){
        float lightInfluence = calculateLightInfluence(lights[i],objectiveCoord);
        vec4 mixedColor = mix(color2, lightColors[i], lightColors[i].a);
        color = mix(color,mixedColor,lightInfluence);
    }
    float v = dot(fragmentNormal.xyz,normalize(vec3(2,1,0)));
    color = mix(color,vec4(0.1,0.1,0.1,1),clamp(v,0,1));
    mixFog();
}