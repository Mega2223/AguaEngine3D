#version 330 core

layout(location = 0) in vec4 vertexPosition_modelspace;

uniform mat4 projection;
uniform mat4 translation;

out vec3 dir;

void main(){
    gl_Position.xyz = vertexPosition_modelspace.xyz;
    gl_Position.w = 1;

    gl_Position = projection * gl_Position;
    dir = vertexPosition_modelspace.xyz;

}