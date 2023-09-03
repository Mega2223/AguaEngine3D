calculateLightInfluenceFunction--:
float calculateLightInfluence(vec4 light,vec4 coord){
    float influence = distance(light.xyz,coord.xyz);
    float brightness = light.w;
    brightness = max(brightness,0);
    return clamp((1/influence)*brightness,0,1);
}
--!stepToVarFunction--:
float stepToVar(float var, float varTo, float unit){
    float difference = var - varTo;
    if(difference > unit){return -unit;}
    if(difference < -unit){return unit;}
    return -difference;
}
--!maxLightsConstant--:
const int MAX_LIGHTS = 10;