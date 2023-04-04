#version 330 core

in vec2 texturePosition;
in vec4 gl_FragCoord;
in vec4 worldCoord;
in vec3 normalAligment;

uniform sampler2D samplerTexture;

uniform float fogStart = 10;
uniform float fogDissolve = 10;
uniform vec4 fogColor = vec4(.5,.5,.6,1);

out vec4 color;

void calculateNormal(inout vec4 color,vec4 absentColor, vec3 lightDirection, vec3 normal){
    if(normal.xyz == 0){return;}
    float influence = dot(normal,lightDirection);
    influence = clamp(influence,0,1);
    color = mix(color,absentColor,influence);
}
void test(){

}

void main(){
    color = texture(samplerTexture,texturePosition);

    float range = (distance(worldCoord,vec4(0,0,0,0)));

    range-=fogStart;
    range/=fogDissolve;

    range = clamp(range,0,1);

    color = mix(color,fogColor,range);
    const vec3 lightDir = vec3(0,1,0);
    calculateNormal(color,fogColor,lightDir,normalAligment);

}

