package org.example.render;

import org.example.Main;
import org.example.WindowManager;
import org.example.render.Map.Map;
import org.example.render.shader.MouseInput;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Math;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    public float m_speed = 0.5f;
    public Vector3f m_pos;
    private final Vector3f m_up;
    public Vector3f m_target;
    private final WindowManager windowmanager;
    public float yaw = 90;
    public float pitch = 0;
    public static boolean isMouseEscape = false;
    public ObjectLoader objectLoader = new ObjectLoader();
    public static MouseInput mouseInput = new MouseInput();
    private static boolean clicked = false;


    public Camera(float x, float y, float z) {
        this.m_pos = new Vector3f(x, y, z);
        this.m_up = new Vector3f(0.0f, 1.0f, 0.0f);
        this.m_target = new Vector3f(0.0f, 0.0f, 1.0f);

        this.yaw = 90;
        this.pitch = 0;

        this.windowmanager = Main.getWindowManager();
        mouseInput.init();
    }

    public void setPosition(float x, float y, float z) {
        m_pos.x = x;
        m_pos.y = y;
        m_pos.z = z;
    }

    public Vector3f getPosition() {
        return m_pos;
    }

    public Vector3f getTarget() {
        return m_target;
    }

    public static Matrix4f CameraTransformation(Vector3f Pos, Vector3f Target, Vector3f Up) {
        Vector3f N = new Vector3f(Target);
        N.normalize();

        Vector3f U = new Vector3f(Up);
        U.cross(N);
        U.normalize();

        Vector3f V = new Vector3f();
        N.cross(U, V);


        Matrix4f CameraRot = new Matrix4f(
                U.x, U.y, U.z, 0,
                V.x, V.y, V.z, 0,
                N.x, N.y, N.z, 0,
                0.0f, 0.0f, 0.0f, 1.0f
        );

        Matrix4f CameraTrans = new Matrix4f(
                1.0f, 0.0f, 0.0f, -Pos.x,
                0.0f, 1.0f, 0.0f, -Pos.y,
                0.0f, 0.0f, 1.0f, -Pos.z,
                0.0f, 0.0f, 0.0f, 1.0f
        );
        Matrix4f Camera = CameraTrans.mul(CameraRot);

        return Camera;
    }

    public Matrix4f getMatrix() {
        return CameraTransformation(this.m_pos, this.m_target, this.m_up);
    }

    public void keyListener() {
        if (windowmanager.isKeyPressed(GLFW_KEY_B)) {
            Map.object old = Map.objects.get(0);
            Map.object obj = new Map.object(old.name(), old.element(), old.x(), old.y(), old.z(), old.fullbright(), old.rotX(), old.rotY() + 0.1f, old.rotZ(), old.sizeScale());
            Map.objects.remove(0);
            Map.objects.addFirst(obj);
        }

        if (windowmanager.isKeyClicked(GLFW_KEY_X)) {
            LightSource light = new LightSource("source", this.m_pos.x, this.m_pos.y, this.m_pos.z, 1.0f, 1.0f, 1.0f);
            Map.lights.add(light);
            clicked = true;
            System.out.println("NUMLIGHTS: " + windowmanager.getMap().getNumDirLights());
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_W))//up
        {
            Vector3f Add = new Vector3f();
            this.m_target.mul(-this.m_speed, Add);
            this.m_pos.add(Add);
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_S)) //down
        {
            Vector3f Add = new Vector3f();
            this.m_target.mul(this.m_speed, Add);
            this.m_pos.add(Add);
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_A))//left
        {
            Vector3f Left = new Vector3f();
            this.m_target.cross(this.m_up, Left);
            Left.normalize();
            Left.mul(this.m_speed);
            this.m_pos.add(Left);
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_D))//right
        {
            Vector3f Right = new Vector3f();
            this.m_up.cross(m_target, Right);
            Right.normalize();
            Right.mul(this.m_speed);
            this.m_pos.add(Right);
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_KP_ADD)) {
            this.m_speed += 0.1;
            System.out.println("Current speed: " + this.m_speed);
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_KP_SUBTRACT) && this.m_speed >= 0.5) {
            this.m_speed -= 0.1;
            System.out.println("Current speed: " + this.m_speed);
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_F)) {
            render.globalFullbright = true;
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_G)) {
            render.globalFullbright = false;
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_K)) {
            isMouseEscape = false;
            glfwSetInputMode(windowmanager.getWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_L)) {
            isMouseEscape = true;
            glfwSetInputMode(windowmanager.getWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_1)) {
            render.activeCam = render.cam;
        }
        if (windowmanager.isKeyPressed(GLFW_KEY_2)) {
            render.activeCam = render.cam2;
        }

        mouseInput.input();
        render.activeCam.TargetVectorTransformation();
    }

    public void TargetVectorTransformation() {
        this.m_target.x = Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch));
        this.m_target.y = Math.sin(Math.toRadians(this.pitch));
        this.m_target.z = Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch));
        this.m_target.normalize();
    }


}
