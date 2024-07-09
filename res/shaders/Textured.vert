#version 450 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uvs;
layout (location = 2) in vec3 normal;

layout (location = 0) out vec2 pass_uvs;
layout (location = 1) out vec3 Normals;
layout (location = 2) out vec3 FragPos;
layout (location = 3) out vec3 campos;
layout (location = 4) out int fullbright;
layout (location = 5) out int globalFull;
layout (location = 6) out vec3 LightSource;
layout (location = 7) out vec3 LightColor;
layout (location = 8) out vec3 camDir;

layout (location = 9) out float[] LightPos;
layout (location = 10) out float[] LightCols;

uniform mat4 Projection;
uniform mat4 Translation;
uniform mat4 Rotation;
uniform mat4 Final;
uniform mat4 WorldTransform;
uniform mat4 CameraTransform;
uniform vec3 camPos;
uniform vec3 camDirection;
uniform int Fullbright;
uniform int globalFullbright;
uniform vec3 lightSource;
uniform mat4 AxisRotation;
uniform vec3 lightColor;

uniform float[] LightPositions;
uniform float[] LightColors;

void main(){
	//gl_Position = Projection * CameraTransform * AxisRotation * WorldTransform * vec4(position.x, position.y, position.z , 1.0);
	gl_Position = Projection * CameraTransform * WorldTransform * AxisRotation * vec4(position.x, position.y, position.z , 1.0);

	//gl_Position = Projection * CameraTransform * WorldTransform * vec4(normal.x, normal.y, normal.z , 1.0);

	FragPos = vec3(AxisRotation * WorldTransform * vec4(position.x, position.y, position.z, 1.0));
	pass_uvs = uvs;
	Normals = normal;
	campos = camPos;
	fullbright = Fullbright;
	globalFull = globalFullbright;
	LightSource = lightSource;
	LightColor = lightColor;
	camDir = -camDirection;

	LightPos = LightPositions;
	LightCols = LightColors;
}