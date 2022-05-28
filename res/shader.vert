#version 400

uniform mat4 u_projectionMatrix;
uniform mat4 u_modelViewMatrix;
uniform vec4 u_color;

in vec4 a_vertex;

out vec4 a_color;

void main(void) {
    a_color = u_color;
	vec4 vertex = u_modelViewMatrix * a_vertex;
	gl_Position = u_projectionMatrix * vertex;
}