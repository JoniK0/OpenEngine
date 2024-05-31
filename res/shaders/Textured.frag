#version 450 core

layout (location = 0) in vec2 pass_uvs;
layout (location = 1) in vec3 Normals;
layout (location = 2) in vec3 FragPos;
layout (location = 3) in vec3 camPos;
layout (location = 4) in flat int fullbright;
layout (location = 5) in flat int globalFullbright;
layout (location = 6) in vec3 lightPos;
layout (location = 7) in vec3 lightSourceColor;
layout (location = 8) in vec3 camDir;

out vec4 out_Color;
out vec4 FragColor;

uniform sampler2D textureSampler;

float ambientStrength = 0.2;
vec4 lightcolor = vec4(lightSourceColor, 1.0);
float specularStrength = 0.5;
float shininess = 128;

struct pointlight{
	vec3 lightpos;
	float ambient;
	float specular;
	float shininess;
	vec4 color;
	float constant;
	float linear;
	float quadratic;
};

struct Light{
	vec3 lightpos;
	float ambient;
	float specular;
	float shininess;
	vec4 color;
	float constant;
	float linear;
	float quadratic;
	float cutOff;
	float outerCutoff;
};




struct Sun{
	vec3 direction;
	float ambient;
	float specular;
	float shininess;
};

struct Spotlight{
	vec3 position;
	vec3 direction;
	float cutOff;
	float outerCutoff;
};

Sun sonne = Sun(vec3(-0.2f, 0.5f, -0.3f), 0.2, 0.5, 128);

vec4 Phong(vec3 direction, float phongambient, vec4 lightcolor, float phongshininess, float phongspecular, float attenuation, float intens){

	vec4 ambient = phongambient * lightcolor;

	vec3 norm = normalize(Normals);
	float diff = max(dot(norm, direction), 0.0);
	vec4 diffuse = diff * lightcolor*intens;

	vec3 viewDir = normalize(camPos - FragPos);
	vec3 reflectDir = reflect(-direction, norm);
	// Blinn-phong
	vec3 halfwayDir = normalize(direction + viewDir);
	float spec = pow(max(dot(norm, halfwayDir), 0.0), phongshininess);
	// phong
	//float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
	//
	vec4 specular = phongspecular * spec * lightcolor*intens;

	vec4 lighting = ambient*attenuation + diffuse*attenuation + specular*attenuation;

	return lighting;
}

/*

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
*/
//vec3 testpos = vec3(27.8, 7.5, -27);
//Sun
vec3 lightDir = normalize(lightPos - FragPos);
vec4 Sunlight = Phong(sonne.direction, sonne.ambient, vec4(1,1,1,1), sonne.shininess, sonne.specular, 1, 1);
//

//pointlight
//vec3 testpos = vec3(27.8, 7.5, -27);
pointlight point = pointlight(lightPos, ambientStrength,specularStrength, shininess, lightcolor, 1.0f, 0.014f, 0.0007f);
float distance = length(point.lightpos - FragPos);
float atten = 1.0 / (point.constant + point.linear * distance + point.quadratic * (distance * distance));
vec4 Pointlight = Phong(lightDir, ambientStrength, lightcolor, shininess, specularStrength, atten, 1);
//

//spotlight (flahslight)
vec3 spotlightdirection = normalize(camPos - FragPos);
Spotlight spot = Spotlight(camPos, camDir, cos(radians(12.5)), cos(radians(17.5)));
float theta = dot(spotlightdirection, normalize(-spot.direction));
float epsilon = (spot.cutOff - spot.outerCutoff);

float flashlightintensity = (1.0 - ( 1.0 - theta)/(1.0-spot.cutOff));
//

vec3 dir = vec3(0.2, -1, 0);
Spotlight lamp = Spotlight(lightPos, dir, cos(radians(1.5)), cos(radians(17.5)));
float teta = dot(lightDir, normalize(-lamp.direction));
float eps = (lamp.cutOff - lamp.outerCutoff);
float lampintensity = clamp((teta - lamp.outerCutoff) / eps, 0.0, 1.0);
vec4 lamplight = vec4(1,1,1,1);

//float epsilon = 0.09;

float intensity = clamp((theta - spot.outerCutoff) / epsilon, 0.0, 1.0);
//float intensity = 0.56;
vec4 spotlight = vec4(1,1,1,1);

//

void main(){
	if (fullbright == 1 || globalFullbright == 1)
	{
		Sunlight = vec4(1.0f, 1.0f, 1.0f, 1.0f);
		Pointlight = vec4(1.0f, 1.0f, 1.0f, 1.0f);
		//spotlight = vec4(1.0f, 1.0f, 1.0f, 1.0f);
	}

	if(theta > spot.cutOff)
	{
		spotlight = Phong(spotlightdirection, ambientStrength, lightcolor, shininess, specularStrength, 1, intensity);
		spotlight *= flashlightintensity;
	}
	else{
		spotlight = vec4(lightcolor * ambientStrength);
		spotlight = vec4(0, 0,0,0);
	}
	//

	if(teta > lamp.cutOff)
	{
		lamplight = Phong(lightDir, ambientStrength, lightcolor, shininess, specularStrength,1,lampintensity);
	}
	else{
		lamplight = vec4(lightcolor * ambientStrength);
	}

	if (fullbright == 1 || globalFullbright == 1)
	{
		Sunlight = vec4(1.0f, 1.0f, 1.0f, 1.0f);
		Pointlight = vec4(1.0f, 1.0f, 1.0f, 1.0f);
		spotlight = vec4(1.0f, 1.0f, 1.0f, 1.0f);
	}


	//out_Color = texture(textureSampler, pass_uvs) * lighting;
	//out_Color = texture(textureSampler, pass_uvs) * Sunlight;
	//out_Color = texture(textureSampler, pass_uvs) * Pointlight;
	out_Color = texture(textureSampler, pass_uvs) * spotlight;
	//out_Color = texture(textureSampler, pass_uvs) * lamplight;





	//FragColor =  texture(textureSampler, pass_uvs) * vec4(1,0.5,0.5,1);
}