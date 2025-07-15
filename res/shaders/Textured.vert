

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uvs;
layout (location = 2) in vec3 normal;
layout (location = 3) in float texUnit;
layout (location = 4) in vec3 tangent;
layout (location = 5) in vec3 bitangent;

layout (location = 0) out vec2 pass_uvs;
layout (location = 1) out vec3 Normals;
layout (location = 2) out vec3 FragPos;
layout (location = 3) out vec3 campos;
layout (location = 6) out vec4 FragPosLightSpace;

layout (location = 11) out float pass_texUnit;
layout (location = 12) out mat3 TBN;

uniform mat4 Projection;
uniform mat4 Translation;
uniform mat4 Rotation;

uniform mat4 WorldTransform;
uniform mat4 CameraTransform;
uniform mat4 lightSpaceMatrix;

uniform vec3 lightSource;
uniform mat4 AxisRotation;


void main(){
	gl_Position = Projection * CameraTransform * WorldTransform * AxisRotation * vec4(position.x, position.y, position.z , 1.0);
	FragPos = vec3(WorldTransform * AxisRotation * vec4(position.x, position.y, position.z, 1.0));

	pass_uvs = uvs;
	Normals = normalize(mat3(transpose(inverse(WorldTransform * AxisRotation))) * vec3(normal.x, normal.y, normal.z));
	pass_texUnit = texUnit;
	FragPosLightSpace = lightSpaceMatrix * vec4(FragPos, 1.0f);

	mat4 model = WorldTransform * AxisRotation;
	vec3 T = normalize(vec3(model * vec4(tangent, 0.0)));
	vec3 B = normalize(vec3(model * vec4(bitangent, 0.0)));
	vec3 N = normalize(vec3(model * vec4(normal, 0.0)));
	TBN = mat3(T, B, N);

	//Flashlight = flash;

}