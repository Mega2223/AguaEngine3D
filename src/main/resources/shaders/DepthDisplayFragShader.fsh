#version 330 core

in vec2 texturePosition;
in vec4 gl_FragCoord;
in vec4 worldCoord;
in vec4 objectiveCoord;

//--@maxLightsConstant

uniform sampler2D displayTexture;
uniform int itneration;
uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);
uniform vec4[MAX_LIGHTS] lights; //4th location is brightness
uniform vec4[MAX_LIGHTS] lightColors; //4th location is color influence

out vec4 color;

//--@calculateLightInfluenceFunction

//--@stepToVarFunction

void main(){

    vec4 textureColor = texture(displayTexture,texturePosition);
    //textureColor = vec4(textureColor.r);

    //textureColor = vec4((worldCoord.w-zMin)/abs(zMin - zMax));
    //textureColor = vec4(textureColor.r/10);

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

