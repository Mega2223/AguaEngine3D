#version 330 core
layout(location = 0) in vec4 vertexPosition_modelspace;

layout(location = 2) in vec4 vertexNormal;

uniform int iteration;
uniform mat4 translation;
uniform mat4 projection;
uniform mat4 rotation = mat4(1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1);

out vec4 worldCoord;
out vec4 objectiveCoord;

out vec4 perVertexNormal;

void main(){
    gl_Position.xyz = vertexPosition_modelspace.xyz;
    float itfl = float(iteration);
    gl_Position.y += sin(itfl*.00025F)*.5F;
    float z = vertexPosition_modelspace.z; float x = vertexPosition_modelspace.x;
    gl_Position.y += sin(x + (itfl *.0125F)) * .2F;
    gl_Position.y += sin(z + 2.33123F + (itfl *.015F)) * .2F;
    gl_Position.y += sin((z * .003F) + (x * -0025F) + 3.123F - (itfl *.0025F)) * .45F;

    gl_Position.w = 1;

    vec4 toTrans = vec4(gl_Position.xyzw);
    gl_Position = projection * (rotation*toTrans*translation);
    worldCoord = gl_Position;
    objectiveCoord = toTrans * translation;
    perVertexNormal = vertexNormal;
}
