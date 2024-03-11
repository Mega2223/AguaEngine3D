#version 330 core

layout(location = 0) in vec4 vertexPosition_modelspace;

uniform mat4 translation;
uniform mat4 projection;
uniform mat4 rotation = mat4(1,0,0,0 , 0,1,0,0 , 0,0,1,0 , 0,0,0,1);

out vec3 dir;

void main(){
    gl_Position.xyz = vertexPosition_modelspace.xyz;
    gl_Position.w = 1;

    gl_Position = projection * (rotation*gl_Position*translation);
    dir = vertexPosition_modelspace.xyz;

}