

layout (location = 0) in vec2 pass_uvs;
layout (location = 1) in vec3 Normals;
layout (location = 2) in vec3 FragPos;
layout (location = 6) in vec4 FragPosLightSpace;
layout (location = 7) in vec3 lightSourceColor;

layout (location = 11) in float pass_texUnit;
layout (location = 12) in mat3 TBN;

out vec4 out_Color;
out vec4 FragColor;

uniform sampler2D textureSamplers[6];
uniform sampler2D normalMaps[6];
uniform sampler2D shadowMap;

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
struct sun{
	vec3 direction;
	vec4 color;
};

uniform pointlight pointlightlist[MAX_POINT_LIGHTS];
uniform spotlight trashcan[MAX_SPOT_LIGHTS];
uniform int pointlightlist_size;
uniform int spotlightlist_size;

uniform sun directSun;
uniform int flash;

uniform vec3 camDirection;
uniform vec3 camPos;
uniform int Fullbright;
uniform int globalFullbright;



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


float shadowCalculation(vec4 fragPosLightSpace)
{
	vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
	projCoords = projCoords * 0.5 + 0.5;
	float closestDepth = texture(shadowMap, projCoords.xy).r;
	float currentDepth = projCoords.z;
	float shadow = currentDepth > closestDepth ? 1.0 : 0.0;

	return shadow;
}

vec4 Phong(vec3 direction, float phongambient, vec4 lightcolor, float phongshininess, float phongspecular, float atten){

	vec3 norm = vec3(0,0,0);
	for(int i = 0; i <=5; i++){
		if(round(pass_texUnit) == i) {
			if (round(vec3(texture(normalMaps[i], pass_uvs).rgb)) != vec3(0.0f, 0.0f, 0.0f)) {
				norm = normalize(vec3(texture(normalMaps[i], pass_uvs).rgb)* 2.0 - 1.0);
				norm = normalize(TBN * norm);
			}
			else{
				norm = normalize(Normals);
			}
		}
	}
	vec4 ambient = phongambient * lightcolor;
	vec3 dir = normalize(direction);

	vec3 null = vec3(0,1,0);

	float dotprod = dot(norm, dir);

	float diff = max(dotprod, 0.0f);
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
	float shadow = shadowCalculation(FragPosLightSpace);
	vec4 lighting = (diffuse * atten + specular * atten);

	return lighting;
}

//calculate sun
vec4 CalcSun(vec3 direction, float phongambient, vec4 lightcolor, float phongshininess, float phongspecular){
	vec4 lighting = Phong(direction, phongambient, lightcolor, phongshininess, phongspecular, 1);
	return lighting;
}
//

//calculate pointlights
vec4 CalcPointlight(vec3 pos, float ambient, float specular, float shininess, vec4 color, float constant, float linear, float quadratic){
	pointlight point = pointlight(pos, ambient,specular, shininess, color, 1.0f, 0.03f, 0.0014f);
	float distance = length(point.lightpos - FragPos);
	vec3 lightdirection = normalize(point.lightpos - FragPos);
	float atten = 1.0 / (point.constant + point.linear * distance + point.quadratic * (distance * distance));
	vec4 Pointlight = Phong(lightdirection, ambient, color, shininess, specular, atten);
	return Pointlight;
}

vec4 NewCalcSpotlight(spotlight spot)
{
	vec3 light_dir = spot.lightpos2 - FragPos;
	vec3 to_light_dir = normalize(light_dir);
	vec3 from_light_dir = -to_light_dir;
	float spot_alfa = dot(from_light_dir, normalize(spot.direction2));

	float theta = dot(light_dir, normalize(-spot.direction2));
	float epsilon = cos(radians(5)) - spot.cutOff2;

	float intensity = clamp((spot.cutOff2 - theta) / epsilon, 0.0f, 1.0f);
	vec4 color = vec4(0, 0, 0, 0);

	if(spot_alfa > spot.cutOff2)
	{
		vec3 null = vec3(0,0,0);
		color = Phong(light_dir, spot.ambient2, spot.color2, spot.shininess2, spot.specular2, 1);
		color *= (1.0 - (1.0 - spot_alfa) / (1.0 - spot.cutOff2));
	}

	return color;
}

vec4 lightcolora = vec4(1,1,1,1);
vec4 lighting = vec4(0,0,0,0);





void main(){


	lighting += CalcSun(normalize(directSun.direction), ambientStrength, directSun.color, shininess, specularStrength);
	float amb = 0;


	for(int i = 0; i < numDirLights; i++) {
		lighting += CalcPointlight(pointlightlist[i].lightpos, pointlightlist[i].ambient, pointlightlist[i].specular, pointlightlist[i].shininess, pointlightlist[i].color, pointlightlist[i].constant, pointlightlist[i].linear, pointlightlist[i].quadratic);
		//lighting = CalcPointlight(lightPos, ambientStrength,specularStrength, shininess, lightcolor, 1.0f, 0.03f, 0.0014f);
		amb = max(amb, pointlightlist[i].ambient);
	}

	lighting += amb;

    for(int i = 0; i < numSpotLights; i++) {
        lighting += NewCalcSpotlight(spotlight(trashcan[i].lightpos2, 0,specularStrength,shininess,trashcan[i].color2,1.0f,0.03f,0.0014f,trashcan[i].direction2, cos(radians(trashcan[i].cutOff2))));
    }

	spotlight spot = spotlight(camPos, ambientStrength,specularStrength,shininess,lightcolora,1.0f,0.03f,0.0014f,-camDirection, cos(radians(40)));
	vec4 dog = NewCalcSpotlight(spot);


	if(flash == 1)
	{
		lighting += dog;
	}


	if (Fullbright == 1 || globalFullbright == 1)
	{
		lighting = vec4(1.0f,1.0f,1.0f,1.0f);
	}

	vec4 tex1 = texture(textureSamplers[0], pass_uvs) * lighting;
	vec4 tex2 = texture(textureSamplers[1], pass_uvs) * lighting;
	vec4 tex3 = texture(textureSamplers[2], pass_uvs) * lighting;
	vec4 tex4 = texture(textureSamplers[3], pass_uvs) * lighting;
	vec4 tex5 = texture(textureSamplers[4], pass_uvs) * lighting;
	vec4 tex6 = texture(textureSamplers[5], pass_uvs) * lighting;

	if(pass_texUnit == 0){out_Color = tex1;}
	else if(round(pass_texUnit) == 1){out_Color = tex2;}
	else if(round(pass_texUnit) == 2){out_Color = tex3;}
	else if(round(pass_texUnit) == 3){out_Color = tex4;}
	else if(round(pass_texUnit) == 4){out_Color = tex5;}
	else if(round(pass_texUnit) == 5){out_Color = tex6;}
	else {out_Color = tex1;}


}