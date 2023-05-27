#version 330 core

in vec4 worldCoord;
in vec3 normalAligment;
in vec4 objectiveCoord;

const int MAX_LIGHTS = 10;

uniform vec4 color2; // uniform color

uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);
uniform vec4[MAX_LIGHTS] lights;
uniform vec4[MAX_LIGHTS] lightColors;

out vec4 color;

float calculateLightInfluence(vec4 light,vec4 coord){
    float influence = distance(light.xyz,coord.xyz);
    float brightness = light.w;
    brightness = max(brightness,0);
    return clamp((1/influence)*brightness,0,1);
}

//walks var towards varTo, at a maximum diference of unit
float stepToVar(float var, float varTo, float unit){
    float difference = var - varTo;
    if(difference > unit){return -unit;}
    if(difference < -unit){return unit;}
    return -difference;
}

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