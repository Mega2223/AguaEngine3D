#version 330 core

in vec3 dir;

uniform int iteration;
uniform vec4 fogColor;

out vec4 color;

const float PI = 3.14159265359;
const float planeYDist = 5;

vec3 findPosAtPlane(vec3 vec, float planeHeight){
    float m = 1/vec.y;
    vec3 pointAtYPlane = vec * m * planeHeight;
    return pointAtYPlane;
}

vec2 pseudoRand(vec2 c, float r){
    float ang = fract(sin(dot(c.xy ,vec2(0.070,0.480))) * 43758.129)*2.*3.14;
    return vec2(sin(ang+r),cos(ang+r));
}

float sm(float n){ //lazy smoothstep
    return smoothstep(0.,1.,n);
}

float perlin(vec2 c, float r){
    vec2 ffC = floor(c), fcC = ffC + vec2(0,1), cfC = ffC + vec2(1,0), ccC = ffC + vec2(1,1);
    vec2 ffDsp = (c - ffC), fcDsp = (c - fcC), cfDsp = (c - cfC), ccDsp = (c - ccC);
    vec2 ffR = pseudoRand(ffC,r),fcR = pseudoRand(fcC,r),
    cfR = pseudoRand(cfC,r),ccR = pseudoRand(ccC,r);
    float dotFF = dot(ffR,ffDsp), dotFC = dot(fcR,fcDsp),
    dotCF = dot(cfR,cfDsp), dotCC = dot(ccR,ccDsp);
    float a = mix(dotFF,dotFC,sm(ffDsp.y)), b = mix(dotCF,dotCC,sm(ffDsp.y));
    float f = mix(a,b,sm(ffDsp.x));
    return f;
}

float perlin(vec2 c){
    return perlin(c,0);
}

void main(){

    vec3 dirA = dir;
    vec3 dirN = normalize(dirA);
    //vec3 sunCoord = vec3(cos(iteration/100.0F),sin(iteration/100.0F),0); // remove dps :)

    vec3 point = findPosAtPlane(dirN,25);
    vec3 point7 = findPosAtPlane(dirN,79);
    vec3 point40 = findPosAtPlane(dirN,400);

    float itf = iteration / 60F;

    float s = 0;
    float l = 0;
    for(int i = 0; i < 16; i++){
        vec3 pointTo = point40 + vec3(iteration/5.0 + 23400.0,0,24300.0 + iteration/7.0);
        float weight = 1.0F/pow(i+1,1.1);
        float scale = 3000*(1F/(pow(i,2.5)+1));
        float speed = (0.00025F/(i+1));
        s+=perlin(pointTo.xz / scale, iteration * speed) * weight;
        l+=weight;
    }
    s/=l; s*=1.6F;
    s -= max(length(point)/16000.0,0);
    s = clamp(s,0,1);
    if(dirN.y <= 0){s = 0;}

    color = mix(fogColor,vec4(1,1,1,1),s);
}