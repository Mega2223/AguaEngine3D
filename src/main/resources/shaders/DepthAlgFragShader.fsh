#version 330 core

in vec4 gl_FragCoord;

layout(location = 0) out float fragDepth;

void main(){
    fragDepth = gl_FragCoord.z;
}