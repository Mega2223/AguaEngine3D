#version 330 core

layout(location = 0) in vec4 vertexPosition_modelspace;
layout(location = 1) in vec2 textureCoords;

uniform mat4 translation;
uniform mat4 projection;
uniform mat4 rotation = mat4(1,0,0,0 , 0,1,0,0 , 0,0,1,0 , 0,0,0,1);
uniform mat4 scale = mat4(1,0,0,0 , 0,1,0,0 , 0,0,1,0 , 0,0,0,1);
uniform int aligment = 0;

out vec2 texturePosition;
out vec4 objectiveCoord;

const int CENTER_ALIGMENT = 0,  BOTTOM_LEFT_ALIGMENT = 1,  BOTTOM_RIGHT_ALIGMENT = 2,  TOP_LEFT_ALIGMENT = 3,  TOP_RIGHT_ALIGMENT = 4;

void translateAligment();

void main() {
    gl_Position.xyz = vertexPosition_modelspace.xyz;
    gl_Position.w = 1;
    objectiveCoord = translation * gl_Position;
    gl_Position = projection*(rotation*gl_Position*translation)*scale;
    texturePosition = textureCoords;
    translateAligment();
}

void translateAligment(){
    float tX = aligment == CENTER_ALIGMENT ? 0 : aligment == BOTTOM_LEFT_ALIGMENT || aligment == TOP_LEFT_ALIGMENT ? -1F : 1F;
    float tY = aligment == CENTER_ALIGMENT ? 0 : aligment == BOTTOM_LEFT_ALIGMENT || aligment == BOTTOM_RIGHT_ALIGMENT ? -1F : 1F;
    gl_Position.x += tX; gl_Position.y += tY;
}