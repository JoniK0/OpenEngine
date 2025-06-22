
layout (location = 0) in vec3 position;

uniform mat4 WorldTransform;
uniform mat4 CameraTransform;
uniform mat4 AxisRotation;
uniform mat4 Projection;

void main() {
    gl_Position = Projection * CameraTransform * WorldTransform * AxisRotation * vec4(position.x, position.y, position.z , 1.0);
}
