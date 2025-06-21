package org.example;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nuklear.Nuklear.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

import imgui.ImFont;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.*;
import org.example.render.*;
import org.example.render.Map.Map;
import org.joml.Vector3f;
import org.lwjgl.glfw.*;
import org.lwjgl.nuklear.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

public class WindowManager {
    Font font = new Font();
    private long window;
    public static int width = 1300;
    public static int height = 900;
    public static double FPS = 1000;
    public render renderer;
    public LightSource source;
    public LightSource sourcetwo;
    public LightSource[] lightSources;
    public ArrayList<LightSource> lightSourcesList = new ArrayList<LightSource>();
    private double TickHZ = 144;
    private static boolean clicked = false;

    // Imgui
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private String glslVersion = null;
    private ImGuiLayer imguilayer;

    Map map = new Map();

    public WindowManager(ImGuiLayer layer) {
        imguilayer = layer;
    }

    public void run() {
        init();
        // font.initFont();
        map.initMap();
        initImGui();
        loop();
        destroy();

    /*
    Callbacks.glfwFreeCallbacks(window);
    GLFW.glfwDestroyWindow(window);

    GLFW.glfwTerminate();
    GLFW.glfwSetErrorCallback(null).free();
    */
    }

    public void init() {
        glslVersion = "#version 450 core";
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 4);
        // Imgui
    /*
    initImGui();
    imGuiGlfw.init(window, true);
    imGuiGl3.init(glslVersion);

     */
        //

        System.out.println(GLFW.glfwGetVersionString());

        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW_SAMPLES, 4);

        window =
                GLFW.glfwCreateWindow(width, height, "OpenEngine: " + FPS, NULL, NULL);
        if (window == NULL) {
            throw new IllegalStateException("Unable to initialize window");
        }

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });

        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            GLFW.glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

            GLFW.glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);

            GLFW.glfwMakeContextCurrent(window);
            GLFW.glfwSwapInterval(1);
            GLFW.glfwShowWindow(window);
        }

        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        glfwSetInputMode(window, GLFW_STICKY_KEYS, GLFW_TRUE);
        GL.createCapabilities();
    }

    public void loop() {
        // GL.createCapabilities();

        // int texture = Texture.loadTexture("texture.png");
        renderer = new render();
        ObjectLoader objectLoader = new ObjectLoader();
        // source = new LightSource(20, 8, 5, 1, 1, 1);

        source = new LightSource("source", 20, 8, 5, 0.0f, 0.0f, 1.0f);
        sourcetwo = new LightSource("sourcetwo", 0, 0, 0, 1.0f, 0.0f, 0.0f);

        lightSourcesList.add(source);
        lightSourcesList.add(sourcetwo);
        // lightSourcesList.add(source);

        // map.setSun(new Sun(0.2f, -0.5f, 0.3f, 1, 1, 1));

        float angle = 0.1f;

        double lastTime = glfwGetTime();
        int Frames = 0;

        // double TickHZ = 60;
        int tickCounter = 0;

        while (!GLFW.glfwWindowShouldClose(window)) {
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            double currentTime = glfwGetTime();
            Frames++;
            if (currentTime - lastTime >= 1.0) {
                WindowManager.FPS = (int) ((1 / (currentTime - lastTime)) * Frames);
                Frames = 0;
                lastTime += 1;
                GLFW.glfwSetWindowTitle(window, "OpenEngine: " + WindowManager.FPS);
            }

            //

            renderer.cleanup();

            if (map.getSky() != null) {
                // System.out.println("sky not null");
                renderer.drawSkybox(map.getSky(), 0, 0, 0, 0);
            }

            for (Map.object obj : map.getObjects()) {
                renderer.draw(obj.element(), obj.x(), obj.y(), obj.z(),
                        obj.fullbright(), obj.rotX(), obj.rotY(), obj.rotZ(),
                        obj.sizeScale());
                // renderer.test(obj.element(), obj.x(), obj.y(), obj.z(),
                // obj.fullbright(), obj.rotX(), obj.rotY(), obj.rotZ(),
                // obj.sizeScale());
            }
            for (Map.model mod : map.getModels()) {
                for (Mesh mesh : mod.elements()) {
                    renderer.draw(mesh, mod.x(), mod.y(), mod.z(), mod.fullbright(),
                            mod.rotX(), mod.rotY(), mod.rotZ(), mod.sizeScale());
                }
            }
            // renderer.drawSkybox(map.getObjects().get(0).element(), 0, 0, 0, 0);

            // renderer.colorShaderFunc(map.getObjects().get(0).element());

            // map.getObjects().removeLast();

            if (tickCounter >= (FPS / TickHZ)) {
                renderer.input();
                // source.rotate(angle);
                // map.getLights().getFirst().rotate(angle);
                angle += 0.01;
                // System.out.println(tickCounter);
                tickCounter = 0;
            }
            // System.out.println(source.getLightPosition());
            tickCounter += 1;

            // imgui
            ImGui.getIO().addConfigFlags(ImGuiConfigFlags.DockingEnable);
            imGuiGlfw.newFrame();
            ImGui.newFrame();

            imguilayer.imgui();

            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr =
                        org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                GLFW.glfwMakeContextCurrent(backupWindowPtr);
            }

            // GLFW.glfwSwapBuffers(window);
            // GLFW.glfwPollEvents();

            //

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
            // System.out.println("loop");
        }
    }

    public long getWindow() {
        return window;
    }
    public long setWindow(long window) {
        this.window = window;
        return this.window;
    }

    public render getRender() {
        return renderer;
    }

    public LightSource getLightSource() {
        return source;
    }
    public ArrayList<Float> getLightSourcesPos() {
        ArrayList list = new ArrayList<>();
        for (LightSource lightSource : lightSourcesList) {
            list.add(lightSource.getLightPosition().x);
            list.add(lightSource.getLightPosition().y);
            list.add(lightSource.getLightPosition().z);
        }
        return list;
    }
    public Sun getSun() {
        return map.getSun();
    }

    public ArrayList<Float> getLightSourceColor() {
        ArrayList list = new ArrayList<>();
        for (LightSource lightSource : lightSourcesList) {
            list.add(lightSource.getLightColor().x);
            list.add(lightSource.getLightColor().y);
            list.add(lightSource.getLightColor().z);
        }
        return list;
    }

    public LightSource[] getLightSourceArray() {
    /*
    LightSource[] array = new LightSource[lightSourcesList.size()];
    return array = lightSourcesList.toArray(array);

     */
        LightSource[] array = new LightSource[map.getLights().size()];
        return array = map.getLights().toArray(array);
    }

    public boolean isKeyPressed(int keycode) {
        if (GLFW.glfwGetKey(window, keycode) == GLFW_PRESS) {
            return true;
        } else {
            return false;
        }
    }
    public boolean isKeyReleased(int keycode) {
        if (GLFW.glfwGetKey(window, keycode) == GLFW_RELEASE) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isKeyClicked(int keycode) {
        if (isKeyPressed(keycode) && !clicked) {
            // System.out.println(clicked);
            clicked = true;
            // System.out.println("why");
            return true;
        } else if (isKeyReleased(keycode)) {
            // System.out.println("lol");
            clicked = false;
            return false;
        } else {
            return false;
        }
        // return false;
    }

    public void destroy() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    private void initImGui() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        
        //io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);

        ImGuiStyle style = ImGui.getStyle();

        style.setWindowMinSize(160, 20);
        style.setWindowRounding(10);
        style.setFrameRounding(2);
        style.setColor(ImGuiCol.Text, 191, 191, 191, 255);
        style.setColor(ImGuiCol.WindowBg, 28, 27, 27, 255);
        style.setColor(ImGuiCol.Header, 26, 25, 24, 255);
        style.setColor(ImGuiCol.Button, 184, 59, 50, 255);
        style.setColor(ImGuiCol.ButtonHovered, 128, 35, 28, 255);
        style.setColor(ImGuiCol.ButtonActive, 128, 25, 28, 255);
        style.setColor(ImGuiCol.CheckMark, 184, 59, 50, 255);
        style.setColor(ImGuiCol.SliderGrab, 184, 59, 50, 255);
        style.setColor(ImGuiCol.SliderGrabActive, 128, 35, 28, 255);
        style.setColor(ImGuiCol.Tab, 26, 25, 24, 255);
        style.setColor(ImGuiCol.TabActive, 15, 15, 15, 255);
        style.setColor(ImGuiCol.TabHovered, 46, 44, 43, 255);
        style.setColor(ImGuiCol.TabUnfocused, 26, 25, 24, 255);
        style.setColor(ImGuiCol.TabUnfocusedActive, 15, 15, 15, 255);
        style.setColor(ImGuiCol.HeaderActive, 26, 25, 24, 255);
        style.setColor(ImGuiCol.MenuBarBg, 26, 25, 24, 255);
        style.setColor(ImGuiCol.ResizeGrip, 184, 59, 50, 255);
        style.setColor(ImGuiCol.ResizeGripHovered, 128, 35, 28, 255);
        style.setColor(ImGuiCol.ResizeGripActive, 128, 35, 28, 255);
        style.setColor(ImGuiCol.FrameBg, 15, 15, 15, 255);
        style.setColor(ImGuiCol.FrameBgHovered, 20, 20, 20, 255);
        style.setColor(ImGuiCol.FrameBgActive, 20, 20, 20, 255);
        style.setColor(ImGuiCol.TitleBgActive, 26, 25, 24, 255);

        imGuiGlfw.init(window, true);
        imGuiGl3.init(glslVersion);
    }
    public Map getMap() {
        return map;
    }
}