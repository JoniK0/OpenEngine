package org.example;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

public class Main {
    private static WindowManager window;
    public static void main(String[] args) {
        System.out.println("Hello world!");
        window = new WindowManager(new ImGuiLayer());
        window.run();
    }

    public static WindowManager getWindowManager() {
        return window;
    }
}