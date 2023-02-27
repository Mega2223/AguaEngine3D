#version 330 core

layout(location = 0) in vec4 vertexPosition_modelspace;
layout(location = 1) in vec2 textureCoords;

in vec3 teste;

uniform mat4 translation;

out vec2 texturePosition;

void main(){

    gl_Position.xyz = vertexPosition_modelspace.xyz;
    gl_Position.w = 1;

    vec4 toTrans = vec4(gl_Position.xyzw);
    gl_Position = translation * toTrans;
    texturePosition = textureCoords;
}
