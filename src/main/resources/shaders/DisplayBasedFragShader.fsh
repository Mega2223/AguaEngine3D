#version 330 core

in vec2 texturePosition;
in vec4 gl_FragCoord;
in vec4 worldCoord;
in vec4 objectiveCoord;
in vec3 normalAligment;

const int MAX_LIGHTS = 10;

uniform sampler2D samplerTexture;

uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);
uniform vec4[MAX_LIGHTS] lights; //4th location is brightness
uniform vec4[MAX_LIGHTS] lightColors; //4th location is light influence

//1400 800
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
    vec4 textureColor = texture(samplerTexture,vec2(gl_FragCoord.x/1400,-gl_FragCoord.y/800));
    color = fogColor;

    for(int i = 0; i < MAX_LIGHTS; i++){
        float lightInfluence = calculateLightInfluence(lights[i],objectiveCoord);
        vec4 mixedColor = mix(textureColor, lightColors[i], lightColors[i].a);
        color = mix(color,mixedColor,lightInfluence);
    }

    //fog calculations
    float fogInfluence = (distance(worldCoord,vec4(0,0,0,0)));
    fogInfluence-=fogStart;
    fogInfluence/=fogDissolve;
    fogInfluence = clamp(fogInfluence,0,1);
    color = mix(color,fogColor,fogInfluence);

}