#version 330 core

//--@maxLightsConstant

in vec2 texturePosition;
in vec4 gl_FragCoord;
in vec4 worldCoord;
in vec4 objectiveCoord;
in vec4[MAX_LIGHTS] lightSpacePos;

uniform sampler2D samplerTexture;
uniform int itneration;
uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);

uniform vec4[MAX_LIGHTS] lights; //4th location is brightness
uniform vec4[MAX_LIGHTS] lightColors; //4th location is color influence

//lightspace calculations for shadow rendering, off by default
uniform sampler2D[MAX_LIGHTS] shadowmaps;
uniform int[MAX_LIGHTS] doShadowMapping;

uniform mat4[MAX_LIGHTS] lightspace_projections;
uniform mat4[MAX_LIGHTS] lightspace_translations;

out vec4 color;
//todo não tem como escapar da projeção ortográfica :p
float calculateShadowAt(int index){
    vec4 pos = lightSpacePos[index];
    vec3 tr = lightSpacePos[index].xyz/lightSpacePos[index].w;

    if(tr.x > 1 || tr.y > 1 || tr.z > 1 || tr.x < -1 || tr.y < -1 || tr.z < -1 ){return 1;}
    return 0;
    //return tr.z > depth ? (tr.z):(depth);
}

float calculateLightInfluence(vec4 light,vec4 coord){
    float influence = distance(light.xyz,coord.xyz);
    float brightness = light.w;
    brightness = max(brightness,0);
    return clamp((1/influence)*brightness,0,1);
}

//--@stepToVarFunction

void main(){
    vec4 textureColor = texture(samplerTexture,texturePosition);
    for(int i = 0; i < MAX_LIGHTS; i++){
        float lightInfluence = calculateLightInfluence(lights[i],objectiveCoord);
        vec4 mixedColor = mix(textureColor, lightColors[i], lightColors[i].a);
        lightInfluence = doShadowMapping[i]==0?(lightInfluence):(lightInfluence-calculateShadowAt(i));
//        if(doShadowMapping[i]!=0){
//            color.rgb = lightSpacePos[i].xyz;
//            return;
//        }
        color = mix(color,mixedColor,lightInfluence);
    }

    //fog calculations
    float fogInfluence = (distance(worldCoord,vec4(0,0,0,0)));
    fogInfluence-=fogStart;
    fogInfluence/=fogDissolve;
    fogInfluence = clamp(fogInfluence,0,1);
    color = mix(color,fogColor,fogInfluence);


}

