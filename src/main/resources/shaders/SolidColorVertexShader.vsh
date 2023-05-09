#version 330 core
layout(location = 0) in vec4 vertexPosition_modelspace;
layout(location = 2) in vec3 normal;

uniform mat4 translation;
uniform mat4 projection;

out vec4 worldCoord;
out vec3 normalAligment;
out vec4 objectiveCoord;

void main(){

    gl_Position.xyz = vertexPosition_modelspace.xyz;
    gl_Position.w = 1;

    vec4 toTrans = vec4(gl_Position.xyzw);
    gl_Position = projection * (toTrans*translation);
    worldCoord = gl_Position;
    objectiveCoord = toTrans * translation;
    normalAligment.xyz = ((vec4(normal.xyz,1) * translation)* projection).xyz;

}
