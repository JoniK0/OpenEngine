package org.example.render;
import org.example.WindowManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Math;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;


public class Transformations {


    float FOV = 75.0f;
    float far = 1500.0f;
    float near = 0.1f;
    float aspect = (float) WindowManager.width/WindowManager.height;
    float tanHalfFOV = Math.tan(Math.toRadians(FOV/2.0f));
    float range = far-near;
    float d = 1/(aspect*tanHalfFOV);
    public static float scale = 0.0f;

    Matrix4f Projection = new Matrix4f(d, 0.0f, 0.0f, 0.0f,
                                0.0f, 1/tanHalfFOV, 0.0f, 0.0f,
                                 0.0f, 0.0f, -((far+near)/range), -((2*far*near)/range),
                                   0.0f, 0.0f, -1.0f, 0.0f);




public Matrix4f TranslationMatrix(float X, float Y, float Z) {
    Matrix4f Translation = new Matrix4f(1.0f, 0.0f, 0.0f, X,
            0.0f, 1.0f, 0.0f, Y,
            0.0f, 0.0f, 1.0f, Z,
            0.0f, 0.0f, 0.0f, 1.0f);
    return Translation;
}

public Matrix4f RotationMatrix(float scale) {
    Matrix4f Rotation = new Matrix4f(
            Math.cos(scale), 0.0f, -(Math.sin(scale)), 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            Math.sin(scale), 0.0f, Math.cos(scale), 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    );
    Transformations.scale += 0.01;
    return Rotation;
}


Vector3f CameraPos = new Vector3f(0.0f, 0.0f, 3.0f);
Vector3f CameraU = new Vector3f(1.0f, 0.0f, 0.0f);
Vector3f CameraV = new Vector3f(0.0f, 1.0f, 0.0f);
Vector3f CameraN = new Vector3f(0.0f, 0.0f, 1.0f);


Matrix4f Camera = new Matrix4f(
        CameraU.x, CameraU.y, CameraU.z, -CameraPos.x,
        CameraV.x, CameraV.y, CameraV.z, -CameraPos.y,
        CameraN.x, CameraN.y, CameraN.z, -CameraPos.z,
        0.0f, 0.0f, 0.0f, 1.0f
);

    public Matrix4f getProjectionMatrix()
    {
        return Projection;
    }

    public Matrix4f getWorldTransformation(float TransX, float TransY, float TransZ, float Scale)
    {
        Matrix4f World = RotationMatrix(Scale).mul(TranslationMatrix(TransX, TransY, TransZ));
        return World;
    }

    public Matrix4f getCameraTransformation()
    {
        return Camera;
    }



}
