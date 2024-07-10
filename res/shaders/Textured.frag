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

layout (location = 9) in float[] LightPos;
layout (location = 10) in float[] LightCols;

out vec4 out_Color;
out vec4 FragColor;

const int MAX_POINT_LIGHTS = 1000;

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

uniform pointlight pointlightlist[MAX_POINT_LIGHTS];
uniform int pointlightlist_size;




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

vec4 Phong(vec3 direction, float phongambient, vec4 lightcolor, float phongshininess, float phongspecular){

	vec4 ambient = phongambient * lightcolor;

	vec3 norm = normalize(Normals);
	float diff = max(dot(norm, direction), 0.0);
	vec4 diffuse = diff * lightcolor;

	vec3 viewDir = normalize(camPos - FragPos);
	vec3 reflectDir = reflect(-direction, norm);
	// Blinn-phong
	vec3 halfwayDir = normalize(direction + viewDir);
	float spec = pow(max(dot(norm, halfwayDir), 0.0), phongshininess);
	// phong
	//float spec = pow(max(dot(viewDir, reflectDir), 0.0), shininess);
	//
	vec4 specular = phongspecular * spec * lightcolor;

	vec4 lighting = ambient+ diffuse + specular;

	return lighting;
}

//calculate sun
vec4 CalcSun(vec3 direction, float phongambient, vec4 lightcolor, float phongshininess, float phongspecular){
	vec4 lighting = Phong(direction, phongambient, lightcolor, phongshininess, phongspecular);
	return lighting;
}
//

//calculate pointlights
vec4 CalcPointlight(vec3 pos, float ambient, float specular, float shininess, vec4 color, float constant, float linear, float quadratic){
	pointlight point = pointlight(pos, ambient,specular, shininess, color, 1.0f, 0.03f, 0.0014f);
	float distance = length(point.lightpos - FragPos);
	vec3 lightdirection = normalize(point.lightpos - FragPos);
	float atten = 1.0 / (point.constant + point.linear * distance + point.quadratic * (distance * distance));
	vec4 Pointlight = Phong(lightdirection, ambient, color, shininess, specular) * atten;
	return Pointlight;
}
//

vec4 CalcSpotlight(vec3 position, vec3 direction, float cutoff){
	vec3 spotlightdirection = normalize(position - FragPos);
	Spotlight spot = Spotlight(position, direction, cos(radians(cutoff)), cos(radians(17.5)));
	float theta = dot(spotlightdirection, normalize(-spot.direction));
	//float epsilon = (spot.cutOff - spot.outerCutoff);
	float flashlightintensity = (1.0 - ( 1.0 - theta)/(1.0-spot.cutOff));
	if(theta > spot.cutOff)
	{
		vec4 spotlight = Phong(spotlightdirection, ambientStrength, lightcolor, shininess, specularStrength);
		spotlight *= flashlightintensity;
		return spotlight;
	}
	else
	{
		vec4 spotlight = vec4(0,0,0,0);
		return spotlight;
	}
}

vec4 sun = CalcSun(vec3(-0.2f, 0.5f, -0.3), ambientStrength,vec4(1,1,1,1), shininess, specularStrength);


vec4 pointl = CalcPointlight(lightPos, ambientStrength,specularStrength, shininess, lightcolor, 1.0f, 0.03f, 0.0014f);
vec4 flash = CalcSpotlight(camPos, camDir, 12.5);

vec4 lighting = vec4(0,0,0,0);





void main(){

	for(int i = 0; i < pointlightlist_size; i++){
		lighting += CalcPointlight(pointlightlist[i].lightpos, pointlightlist[i].ambient, pointlightlist[i].specular, pointlightlist[i].shininess, pointlightlist[i].color, pointlightlist[i].constant, pointlightlist[i].linear, pointlightlist[i].quadratic);
		//lighting = CalcPointlight(lightPos, ambientStrength,specularStrength, shininess, lightcolor, 1.0f, 0.03f, 0.0014f);
	}

	if (fullbright == 1 || globalFullbright == 1)
	{
		lighting = vec4(1.0f,1.0f,1.0f,1.0f);
		pointl = vec4(1,1,1,1);
	}

	out_Color = texture(textureSampler, pass_uvs) * lighting;


}