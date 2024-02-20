#version 330 core

layout(location = 0) in vec4 vertexPosition_modelspace;
layout(location = 1) in vec2 texturePos;

uniform mat4 projection;
uniform mat4 translation;
uniform mat4 rotation = mat4(1,0,0,0 , 0,1,0,0 , 0,0,1,0 , 0,0,0,1);
uniform int iteration;
uniform vec4 bounds = vec4(0,0,1,1);

out vec2 texturePosition;
out vec4 worldCoord;
out vec4 objectiveCoord;

void main(){
    gl_Position.xyz = vertexPosition_modelspace.xyz;
    gl_Position.w = 1;
    vec4 toTrans = vec4(gl_Position.xyzw);

    gl_Position = projection * (rotation*toTrans*translation);
    objectiveCoord = toTrans * translation;
    worldCoord = gl_Position;

    texturePosition = texturePos;

}
