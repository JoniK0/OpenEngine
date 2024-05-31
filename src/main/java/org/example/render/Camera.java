package org.example.render;
import org.example.ImGuiLayer;
import org.example.Main;
import org.example.WindowManager;
import org.example.render.shader.MouseInput;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.example.render.shader.MouseInput;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {
    public float m_speed = 0.5f;
    public Vector3f m_pos;
    private final Vector3f m_up;
    public Vector3f m_target;
    private final WindowManager windowmanager;
    public static float yaw = 90;
    public static float pitch = 0;
    public static boolean isMouseEscape = false;
    public MouseInput mouseInput = new MouseInput();



    public Camera(){
        m_pos = new Vector3f(0.0f, 0.0f, 1.0f);
        m_up = new Vector3f(0.0f, 1.0f, 0.0f);
        m_target = new Vector3f(0.0f, 0.0f, 1.0f);
        this.windowmanager = Main.getWindowManager();
        mouseInput.init();
    }

    public void setPosition(float x, float y, float z){
        m_pos.x = x;
        m_pos.y = y;
        m_pos.z = z;
    }

    public Matrix4f CameraTransformation(Vector3f Pos, Vector3f Target, Vector3f Up){
        //System.out.println(m_up);

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


    public Matrix4f getMatrix(){
        return CameraTransformation(m_pos,m_target,m_up);
    }

    public void keyListener(){
        if(windowmanager.isKeyPressed(GLFW_KEY_W))//up
        {
            Vector3f Add = new Vector3f();
            m_target.mul(-m_speed, Add);
            m_pos.add(Add);
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_S)) //down
        {
            Vector3f Add = new Vector3f();
            m_target.mul(m_speed, Add);
            m_pos.add(Add);
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_A))//left
        {
            Vector3f Left = new Vector3f();
            m_target.cross(m_up, Left);
            Left.normalize();
            Left.mul(m_speed);
            m_pos.add(Left);
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_D))//right
        {
            Vector3f Right = new Vector3f();
            m_up.cross(m_target, Right);
            Right.normalize();
            Right.mul(m_speed);
            m_pos.add(Right);
        }

        if(windowmanager.isKeyPressed(GLFW_KEY_D))
        {

            //yaw += 3;
            TargetVectorTransformation();
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_A))
        {

            //yaw -= 3;
            TargetVectorTransformation();
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_W) && pitch > -89.9)
        {
            //pitch -= 3;
            TargetVectorTransformation();
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_S) && pitch < 89.9)
        {
            //pitch += 3;
            TargetVectorTransformation();
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_KP_ADD))
        {
            m_speed += 0.1;
            System.out.println("Current speed: "+m_speed);
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_KP_SUBTRACT) && m_speed >= 0.5)
        {
            m_speed -= 0.1;
            System.out.println("Current speed: "+m_speed);
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_F))
        {
                render.globalFullbright = true;
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_G))
        {
            render.globalFullbright = false;
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_P))
        {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_O))
        {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_K))
        {
            isMouseEscape = false;
            GLFW.glfwSetInputMode(windowmanager.getWindow(), GLFW.GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
        if(windowmanager.isKeyPressed(GLFW_KEY_L))
        {
            isMouseEscape = true;
            GLFW.glfwSetInputMode(windowmanager.getWindow(), GLFW.GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
        mouseInput.input();
        TargetVectorTransformation();
    }

    public void TargetVectorTransformation()
    {
        m_target.x = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        m_target.y = Math.sin(Math.toRadians(pitch));
        m_target.z = Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        m_target.normalize();
    }



}
