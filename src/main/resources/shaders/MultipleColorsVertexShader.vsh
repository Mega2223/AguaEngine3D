#version 330 core
layout(location = 0) in vec4 vertexPosition_modelspace;
layout(location = 4) in vec4 vertexColor;

uniform mat4 translation;
uniform mat4 projection;
uniform mat4 rotation;

out vec4 relativeCoord;
out vec4 objectiveCoord;
out vec4 fragmentColor;

void main(){

    gl_Position.xyz = vertexPosition_modelspace.xyz;
    gl_Position.w = 1;

    vec4 toTrans = vec4(gl_Position.xyzw);

    gl_Position = projection * (rotation*toTrans*translation);
    relativeCoord = gl_Position;
    objectiveCoord = toTrans * translation;
    fragmentColor = vertexColor;

}
