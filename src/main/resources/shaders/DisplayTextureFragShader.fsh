#version 330 core

in vec2 texturePosition;
in vec4 gl_FragCoord;
in vec4 worldCoord;
in vec4 objectiveCoord;

//--@maxLightsConstant

uniform sampler2D displayTexture;
uniform int iteration;
uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);
uniform vec4[MAX_LIGHTS] lights; //4th location is brightness
uniform vec4[MAX_LIGHTS] lightColors; //4th location is color influence
uniform bool doShadowMapping = false;
uniform int useAlphaForFog = 0;

out vec4 color;

//--@calculateLightInfluenceFunction

//--@stepToVarFunction

//--@mixFogFunction

void main(){

    vec4 textureColor = texture(displayTexture,texturePosition);

    color = fogColor;

    for(int i = 0; i < MAX_LIGHTS; i++){
        float lightInfluence = calculateLightInfluence(lights[i],objectiveCoord);
        vec4 mixedColor = mix(textureColor, lightColors[i], lightColors[i].a);
        color = mix(color,mixedColor,lightInfluence);
    }

    mixFog();
}

