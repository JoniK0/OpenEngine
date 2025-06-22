package org.example.render.shader;
import org.example.Main;
import org.example.WindowManager;
import org.example.render.Camera;
import org.example.render.render;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class MouseInput {
    public Vector2d prevPos, curPos;
    private final Vector2f displVec;
    private boolean inWindow = false;
    public static float Sensitivity = 0.2f;

    public MouseInput(){
        prevPos = new Vector2d(WindowManager.width/2, WindowManager.height/2);
        curPos = new Vector2d(0, 0);
        displVec = new Vector2f();
    }

    public void init(){
        GLFW.glfwSetCursorPosCallback(Main.getWindowManager().getWindow(), (window, xpos, ypos) ->{
            curPos.x = xpos;
            curPos.y = ypos;
        });
    }

    public void input(){
        double x = 0;
        double y = 0;
        displVec.x = 0;
        displVec.y = 0;
        if(prevPos.x > 0 && prevPos.y > 0) {
            x = curPos.x - prevPos.x;
            y = curPos.y - prevPos.y;
        }


            curPos.x = WindowManager.width/2;
            curPos.y = WindowManager.height/2;

        if(!Camera.isMouseEscape){
            //System.out.println("input");
            render.activeCam.yaw += x * Sensitivity;
            render.activeCam.pitch += y * Sensitivity;
            GLFW.glfwSetCursorPos(Main.getWindowManager().getWindow(), WindowManager.width/2, WindowManager.height/2);
        }

    }


}
