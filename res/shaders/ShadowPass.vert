layout (location = 0) in vec3 aPos;

uniform mat4 lightSpaceMatrix;
uniform mat4 model;


uniform mat4 WorldTransform;
uniform mat4 CameraTransform;

uniform mat4 AxisRotation;
uniform mat4 Projection;

void main()
{
    gl_Position = Projection * CameraTransform * WorldTransform * AxisRotation * vec4(aPos, 1.0);
}