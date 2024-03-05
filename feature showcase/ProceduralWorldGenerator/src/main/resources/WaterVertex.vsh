#version 330 core
layout(location = 0) in vec4 vertexPosition_modelspace;
layout(location = 2) in vec4 vertexNormal;

uniform mat4 translation;
uniform mat4 projection;
uniform mat4 rotation = mat4(1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1);
uniform int iteration;

out vec4 worldCoord;
out vec4 objectiveCoord;
out vec4 perVertexNormal;

void main(){

    gl_Position.xyz = vertexPosition_modelspace.xyz;
    gl_Position.w = 1;
    float c1 = cos(iteration * 0.000035F + gl_Position.x * 0.01 - gl_Position.z * 0.01);
    vec3 actualPos = (gl_Position * translation).xyz;
    gl_Position.y += (
        cos(actualPos.z/8 + iteration * 0.00025F - c1) * .1F + //if you're wondering these came to me in a dream
        cos(actualPos.x/8 - iteration * 0.00025F + c1) * .1F +
        cos(actualPos.z/3 - iteration * 0.0043F * sin(c1*3.14)) * .14F +
        cos(actualPos.x/3 - iteration * 0.0043F * cos(c1*3.14)) * .14F +
        cos(actualPos.z/1.5F + iteration * 0.005F) * .13F +
        cos(actualPos.x/1.5F + iteration * 0.005F) * .13F
    );

    vec4 toTrans = vec4(gl_Position.xyzw);
    gl_Position = projection * (rotation*toTrans*translation);
    worldCoord = gl_Position;
    objectiveCoord = toTrans * translation;
    perVertexNormal = vertexNormal;
}
