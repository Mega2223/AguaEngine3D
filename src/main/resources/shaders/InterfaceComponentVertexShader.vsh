#version 330 core

layout(location = 0) in vec4 vertexPosition_modelspace;
layout(location = 1) in vec2 textureCoords;

uniform mat4 translation;
uniform mat4 projection;
uniform mat4 rotation = mat4(1,0,0,0 , 0,1,0,0 , 0,0,1,0 , 0,0,0,1);

out vec2 texturePosition;
out vec4 objectiveCoord;

void main() {
    gl_Position.xyz = vertexPosition_modelspace.xyz;
    gl_Position.w = 1;
    objectiveCoord = translation * gl_Position;
    gl_Position = projection*(rotation*gl_Position*translation);
    texturePosition = textureCoords;
}