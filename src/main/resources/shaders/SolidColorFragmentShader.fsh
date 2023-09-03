#version 330 core

in vec4 worldCoord;
in vec4 objectiveCoord;

//--@maxLightsConstant

uniform vec4 color2; // uniform color

uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);
uniform vec4[MAX_LIGHTS] lights;
uniform vec4[MAX_LIGHTS] lightColors;

out vec4 color;

//--@calculateLightInfluenceFunction

//--@stepToVarFunction

void main(){
    color = fogColor;

    for(int i = 0; i < MAX_LIGHTS; i++){
        float lightInfluence = calculateLightInfluence(lights[i],objectiveCoord);
        vec4 mixedColor = mix(color2, lightColors[i], lightColors[i].a);
        color = mix(color,mixedColor,lightInfluence);
    }


    float fogInfluence = (distance(worldCoord,vec4(0,0,0,0)));
    fogInfluence -= fogStart;
    fogInfluence /= fogDissolve;
    fogInfluence = clamp(fogInfluence,0,1);
    color = mix(color,fogColor,fogInfluence);

}