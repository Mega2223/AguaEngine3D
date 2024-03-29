#version 330 core

//--@maxLightsConstant

in vec2 texturePosition;
in vec4 gl_FragCoord;
in vec4 worldCoord;
in vec4 objectiveCoord;
in vec4[MAX_LIGHTS] lightSpacePos;
in vec4 fragmentNormal;

uniform sampler2D samplerTexture;
uniform int iteration;
uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);
uniform int useAlphaForFog = 0;

uniform vec4[MAX_LIGHTS] lights; //4th location is brightness
uniform vec4[MAX_LIGHTS] lightColors; //4th location is color influence

//lightspace calculations for shadow rendering, off by default
uniform sampler2D[MAX_LIGHTS] shadowmaps;//fixme OpenGL does not like sampler arrays JUST IN SOME MACHINES FOR SOME REASON >:(
uniform int[MAX_LIGHTS] doShadowMapping;

out vec4 color;

float calculateShadowAt(int index){
    vec4 pos = lightSpacePos[index];
    vec3 tr = lightSpacePos[index].xyz/lightSpacePos[index].w;
    if(tr.x > 1 || tr.y > 1 || tr.z > 1 || tr.x < -1 || tr.y < -1 || tr.z < -1 ){return 1;}
    vec3 br = tr.xyz;
    br = cos(clamp(br*3.14,-3.14,3.14))+1;
    tr/=2;tr+=.5;
    float depth = texture(shadowmaps[index],tr.xy)[0];
    return tr.z-.00005 > depth ? 1:0;
}

float calculateLightInfluence(vec4 light,vec4 coord){
    float influence = distance(light.xyz,coord.xyz);
    float brightness = light.w;
    brightness = max(brightness,0);
    return clamp((1/influence)*brightness,0,1);
}

//--@stepToVarFunction

//--@mixFogFunction

void main(){
    vec4 textureColor = texture(samplerTexture,texturePosition);
    for(int i = 0; i < MAX_LIGHTS; i++){
        float lightInfluence = calculateLightInfluence(lights[i],objectiveCoord);
        vec4 mixedColor = mix(textureColor, lightColors[i], lightColors[i].a);
        lightInfluence = doShadowMapping[i]==0?(lightInfluence):(lightInfluence-calculateShadowAt(i));
        lightInfluence = clamp(lightInfluence,0,1);
        color = mix(color,mixedColor,lightInfluence);
    }
    color.a = textureColor.a;
    mixFog();

}

