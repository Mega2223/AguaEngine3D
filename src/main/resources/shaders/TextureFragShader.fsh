#version 330 core

in vec2 texturePosition;
in vec4 gl_FragCoord;
in vec4 worldCoord;
in vec4 objectiveCoord;

//--@maxLightsConstant

uniform sampler2D samplerTexture;
uniform int itneration;
uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);

uniform vec4[MAX_LIGHTS] lights; //4th location is brightness
uniform vec4[MAX_LIGHTS] lightColors; //4th location is color influence

//lightspace calculations for shadow rendering, off by default
uniform sampler2D[MAX_LIGHTS] shadowmaps;
uniform mat4[MAX_LIGHTS] lightspace_positions;
uniform int[MAX_LIGHTS] doShadowMapping;

out vec4 color;

float calculateShadowAt(int index){
    vec4 fragLightspacePos = objectiveCoord * lightspace_positions[index];
    vec3 compressed = fragLightspacePos.xyz / fragLightspacePos.w;
    float textureDepth = texture(shadowmaps[index],compressed.xy)[0];
    float depth = compressed.z;
    return abs(textureDepth);
    return(-depth);
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
        if(doShadowMapping[i]!=0){
            lightInfluence -= calculateShadowAt(i);
        }
        color = mix(color,mixedColor,lightInfluence);
    }


    //fog calculations
    float fogInfluence = (distance(worldCoord,vec4(0,0,0,0)));
    fogInfluence-=fogStart;
    fogInfluence/=fogDissolve;
    fogInfluence = clamp(fogInfluence,0,1);
    color = mix(color,fogColor,fogInfluence);


}

