#version 330 core

layout (triangles) in;
layout (triangle_strip, max_vertices = 3) out;

in vec4 objectiveCoord[];
in vec4 worldCoords[];
in vec4 vertexShNormal[];

out vec4 worldCoord;
out vec4 objectiveCoords;
out vec3 primitiveNormal;
out vec4 perVertexNormal;

void main() {
    vec3 nor = cross(
    objectiveCoord[1].xyz-objectiveCoord[0].xyz,
    objectiveCoord[2].xyz-objectiveCoord[0].xyz);

    for(int i = 0; i < 3; i++){
        objectiveCoords = objectiveCoord[i];
        gl_Position = gl_in[i].gl_Position;
        worldCoord = worldCoords[i];
        primitiveNormal = normalize(nor);
        perVertexNormal = vertexShNormal[i];
        EmitVertex();
    }
    EndPrimitive();

}