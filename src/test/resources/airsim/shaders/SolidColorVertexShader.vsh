#version 330 core

layout(location = 0) in vec4 vertexPosition_modelspace;

uniform mat4 translation;
uniform mat4 projection;
uniform mat4 rotation;

void main(){
    gl_Position.xyz = vertexPosition_modelspace.xyz;
    gl_Position.w = 1;

    vec4 toTrans = vec4(gl_Position.xyzw);
    gl_Position = projection * (rotation*toTrans*translation);
    gl_Position.w = 1;
}