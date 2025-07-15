package org.example.render;
import org.example.WindowManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Math;
import org.joml.Vector4f;

public class Transformations {
    public static float FOV = 75.0f;
    float far = 1500.0f;
    float near = 0.1f;
    public static float aspect = (float) WindowManager.width/WindowManager.height;
    float range = far-near;

public Matrix4f TranslationMatrix(float X, float Y, float Z) {
    Matrix4f Translation = new Matrix4f(1.0f, 0.0f, 0.0f, X,
            0.0f, 1.0f, 0.0f, Y,
            0.0f, 0.0f, 1.0f, Z,
            0.0f, 0.0f, 0.0f, 1.0f);
    return Translation;
}

public Matrix4f RotationMatrix(float scaleX, float scaleY, float scaleZ) {

    Matrix4f RotationX = new Matrix4f(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, Math.cos(scaleX), -Math.sin(scaleX), 0.0f,
            0.0f, Math.sin(scaleX), Math.cos(scaleX), 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    );

    Matrix4f RotationZ = new Matrix4f(
            Math.cos(scaleZ), -Math.sin(scaleZ), 0.0f, 0.0f,
            Math.sin(scaleZ), Math.cos(scaleZ), 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    );

    Matrix4f RotationY = new Matrix4f(
            Math.cos(scaleY), 0.0f, -(Math.sin(scaleY)), 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            Math.sin(scaleY), 0.0f, Math.cos(scaleY), 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
    );
    //Transformations.scale += 0.01;
    return RotationX.mul(RotationY).mul(RotationZ);
}

public Matrix4f ScaleMatrix(float scale){
    Matrix4f Scale = new Matrix4f(
            scale, 0.0f, 0.0f, 0.0f,
            0.0f, scale, 0.0f, 0.0f,
            0.0f, 0.0f, scale, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f);
    return Scale;
}

    public Matrix4f getProjectionMatrix()
    {
        float tanHalfFOV = Math.tan(Math.toRadians(FOV/2.0f));
        float d = 1/(aspect*tanHalfFOV);
        Matrix4f Projection = new Matrix4f(d, 0.0f, 0.0f, 0.0f,
                0.0f, 1/tanHalfFOV, 0.0f, 0.0f,
                0.0f, 0.0f, -((far+near)/range), -((2*far*near)/range),
                0.0f, 0.0f, -1.0f, 0.0f);

        return Projection;
    }

    public Matrix4f genOrthoMatrix(float left, float right, float bottom, float top, float near, float far)
    {
        Matrix4f ortho = new Matrix4f(2/(right-left), 0.0f, 0.0f, -((right+left)/(right-left)),
                0.0f, 2/(top-bottom), 0.0f, -((top+bottom)/(top-bottom)),
                0.0f, 0.0f, -2/(far-near), -((far+near)/(far-near)),
                0.0f, 0.0f, 0.0f, 1.0f);
        return ortho;
    }

    public Matrix4f getWorldTransformation(float TransX, float TransY, float TransZ, float Scale, float SizeScale)
    {
        Matrix4f World = ScaleMatrix(SizeScale).mul(TranslationMatrix(TransX, TransY, TransZ));
        return World;
    }


    public Vector3f rotateVector(Vector3f vector, float rotX, float rotY, float rotZ)
    {
        Matrix4f transformation = RotationMatrix(rotX, rotY, rotZ);
        Vector4f val = new Vector4f(vector, 1.0f);
        val = val.mul(transformation);
        return new Vector3f(val.x, val.y, val.z);
    }
}
