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
//layout (location = 9) in mat3 TBN;

layout (location = 9) in float[] LightPos;
layout (location = 10) in float[] LightCols;

layout (location = 11) in float pass_texUnit;
layout (location = 12) in mat3 TBN;

out vec4 out_Color;
out vec4 FragColor;

const int MAX_POINT_LIGHTS = 1000;
const int MAX_SPOT_LIGHTS = 1000;

/*
uniform sampler2D textureSampler0;
uniform sampler2D textureSampler1;
uniform sampler2D textureSampler2;
uniform sampler2D textureSampler3;
uniform sampler2D textureSampler4;
uniform sampler2D textureSampler5;
*/

uniform sampler2D textureSamplers[6];
uniform sampler2D normalMaps[6];

/*
uniform sampler2D normalMap0;
uniform sampler2D normalMap1;
uniform sampler2D normalMap2;
uniform sampler2D normalMap3;
uniform sampler2D normalMap4;
uniform sampler2D normalMap5;
*/

//vec3 normale = vec3(texture(normalMap0, pass_uvs).rgb);



//uniform sampler2D normalMap;

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

struct spotlight{
	vec3 lightpos2;
	float ambient2;
	float specular2;
	float shininess2;
	vec4 color2;
	float constant2;
	float linear2;
	float quadratic2;
	vec3 direction2;
	float cutOff2;
};

uniform pointlight pointlightlist[MAX_POINT_LIGHTS];
uniform spotlight trashcan[MAX_SPOT_LIGHTS];
uniform int pointlightlist_size;
uniform int spotlightlist_size;




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




Sun sonne = Sun(vec3(-0.2f, 0.5f, -0.3f), 0.2, 0.5, 128);

vec4 Phong(vec3 direction, float phongambient, vec4 lightcolor, float phongshininess, float phongspecular){

	vec3 norm = vec3(0,0,0);
	for(int i = 0; i <=5; i++){
		if(pass_texUnit == i) {
			if (vec3(texture(normalMaps[i], pass_uvs).rgb) != vec3(0, 0, 0)) {
				norm = normalize(vec3(texture(normalMaps[i], pass_uvs).rgb)* 2.0 - 1.0);
				norm = normalize(TBN * norm);
			}
			else{
				norm = normalize(Normals);
			}
		}
	}

	vec4 ambient = phongambient * lightcolor;

	//vec3 norm = normalize(Normals);

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

vec4 CalcSpotlight(vec3 position, float ambient, float specular, float shininess, vec4 color, float constant, float linear, float quadratic, vec3 direction, float cutoff){

	vec3 spotlightdirection = normalize(position - FragPos);
	spotlight spot = spotlight(position, ambient, specular, shininess, color, 1.0f, 0.03f, 0.0014f, direction, cos(radians(cutoff)));
	float theta = dot(spotlightdirection, normalize(-spot.direction2));
	//float epsilon = (spot.cutOff - spot.outerCutoff);
	float flashlightintensity = (1.0 - ( 1.0 - theta)/(1.0-spot.cutOff2));


	if(theta > spot.cutOff2)
	{
		vec4 spotlight = Phong(spotlightdirection, ambient, color, shininess, specular);
		spotlight *= flashlightintensity;
		return spotlight;
	}
	else
	{
		vec4 spotlight = vec4(0,0,0,0);
		return spotlight;
	}

	return vec4(0,0,0,0);
}

vec4 sun = CalcSun(vec3(-0.2f, 0.5f, -0.3), ambientStrength,vec4(1,1,1,1), shininess, specularStrength);


vec4 pointl = CalcPointlight(lightPos, ambientStrength,specularStrength, shininess, lightcolor, 1.0f, 0.03f, 0.0014f);
vec4 lightcolora = vec4(1,1,1,1);
vec4 flash = CalcSpotlight(camPos, ambientStrength,specularStrength,shininess,lightcolora,1.0f,0.03f,0.0014f,camDir, 40.5);

vec4 lighting = vec4(0,0,0,0);





void main(){

	//float trash = trashcan[0].lightpos.x;
	for(int i = 0; i < pointlightlist_size; i++) {
		lighting += CalcPointlight(pointlightlist[i].lightpos, pointlightlist[i].ambient, pointlightlist[i].specular, pointlightlist[i].shininess, pointlightlist[i].color, pointlightlist[i].constant, pointlightlist[i].linear, pointlightlist[i].quadratic);
		//lighting = CalcPointlight(lightPos, ambientStrength,specularStrength, shininess, lightcolor, 1.0f, 0.03f, 0.0014f);
	}

	for(int i = 0; i < spotlightlist_size; i++) {

		/*
		vec3 pos = trashcan[i].lightpos;
		float ambient = trashcan[i].ambient;
		float specular = trashcan[i].specular;
		float shininess = trashcan[i].shininess;
		vec4 color = trashcan[i].color;
		float constant = trashcan[i].constant;
		float linear = trashcan[i].linear;
		float quadratic = trashcan[i].quadratic;
		vec3 spotdirection = trashcan[i].direction;
		float spotcutoff = trashcan[i].cutOff;
*/


		//lighting += CalcPointlight(trashcan[i].lightpos2, trashcan[i].ambient2, trashcan[i].specular2, trashcan[i].shininess2, trashcan[i].color2, trashcan[i].constant2, trashcan[i].linear2, trashcan[i].quadratic2);
		//lighting += CalcSpotlight(trashcan[i].lightpos, trashcan[i].ambient, trashcan[0].specular, trashcan[0].shininess, trashcan[0].color, trashcan[0].constant, trashcan[0].linear, trashcan[0].quadratic, trashcan[0].direction, trashcan[0].cutOff);
		//lighting += CalcSpotlight(trashcan[i].lightpos2, trashcan[i].ambient2, trashcan[i].specular2, trashcan[i].shininess2, trashcan[i].color2, trashcan[i].constant2, trashcan[i].linear2, trashcan[i].quadratic2, trashcan[i].direction2, trashcan[i].cutOff2);
		//lighting += CalcSpotlight(pos,ambient,specular,shininess,color, constant,linear,quadratic,spotdirection,spotcutoff);
		//lighting += CalcSpotlight(vec3(1,0,0), 1,1,1, vec4(1,2,3,4), 0,1,2, vec3(1,2,3), 0.5);
		lighting += CalcSpotlight(
			trashcan[i].lightpos2,
			trashcan[i].ambient2,
			trashcan[i].specular2,
			trashcan[i].shininess2,
			trashcan[i].color2,
			trashcan[i].constant2,
			trashcan[i].linear2,
			trashcan[i].quadratic2,
			trashcan[i].direction2,
			trashcan[i].cutOff2);

		//lighting = color;


	}

	if (fullbright == 1 || globalFullbright == 1)
	{
		lighting = vec4(1.0f,1.0f,1.0f,1.0f);
		pointl = vec4(1,1,1,1);
	}

	//lighting = flash;

	//lighting = vec4(1,1,1,1)*spotlightlist_size;





/*
	if(pass_texUnit == 0.0f){out_Color = texture(textureSampler0, pass_uvs) * lighting;}
	else if(pass_texUnit == 1.0f){out_Color = texture(textureSampler1, pass_uvs) * lighting;}
	else if(pass_texUnit == 2.0f){out_Color = texture(textureSampler2, pass_uvs) * lighting;}
	else if(pass_texUnit == 3.0f){out_Color = texture(textureSampler3, pass_uvs) * lighting;}
	else if(pass_texUnit == 4.0f){out_Color = texture(textureSampler4, pass_uvs) * lighting;}
	else if(pass_texUnit == 5.0f){out_Color = texture(textureSampler5, pass_uvs) * lighting;}
	else{out_Color = texture(textureSampler2, pass_uvs) * lighting;}
	*/

	for(int i = 0; i <= 5; i++){
		if(pass_texUnit == i){out_Color = texture(textureSamplers[i], pass_uvs) * lighting;}
	}



	//out_Color = texture(textureSampler, pass_uvs) * lighting;


}