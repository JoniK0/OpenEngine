#version 450 core

layout (location = 0) in vec2 pass_uvs;
layout (location = 1) in vec3 Normals;
layout (location = 2) in vec3 FragPos;
layout (location = 3) in vec3 camPos;
layout (location = 4) in flat int fullbright;
layout (location = 5) in flat int globalFullbright;
layout (location = 6) in vec3 lightPos;
layout (location = 7) in vec3 lightSourceColor;

out vec4 out_Color;
out vec4 FragColor;

uniform sampler2D textureSampler;

//vec3 lightPos = {15.0, 5.0, 5.0};
float ambientStrength = 0.2;
vec4 lightcolor = vec4(lightSourceColor, 1.0);
float specularStrength = 0.5;
float shininess = 128;

vec4 ambient = ambientStrength * lightcolor;

vec3 norm = normalize(Normals);
vec3 lightDir = normalize(lightPos - FragPos);
float diff = max(dot(norm, lightDir), 0.0);
vec4 diffuse = diff * lightcolor;

vec3 viewDir = normalize(camPos - FragPos);
vec3 reflectDir = reflect(-lightDir, norm);
// Blinn-phong
vec3 halfwayDir = normalize(lightDir + viewDir);
float spec = pow(max(dot(norm, halfwayDir), 0.0), shininess);
// phong
//float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
//
vec4 specular = specularStrength * spec * lightcolor;

//vec4 test = vec4(0.0, 0.0, 1.0, 0.0) * lightcolor;

vec4 lighting = ambient + diffuse + specular;



void main(){
	if (fullbright == 1 || globalFullbright == 1)
	{
		lighting = vec4(1.0f, 1.0f, 1.0f, 1.0f);
	}


	out_Color = texture(textureSampler, pass_uvs) * lighting;
	//FragColor =  texture(textureSampler, pass_uvs) * vec4(1,0.5,0.5,1);
}