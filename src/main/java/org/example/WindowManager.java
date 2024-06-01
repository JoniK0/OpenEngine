package org.example;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Vector;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;



import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
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

import javax.swing.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.nuklear.Nuklear.*;


public class WindowManager {

    private long window;
    public static int width = 900;
    public static int height = 600;
    public static double FPS = 1000;
    public render renderer;
    public LightSource source;
    public LightSource sourcetwo;
    public LightSource[] lightSources;
    public ArrayList<LightSource> lightSourcesList = new ArrayList<LightSource>();
    private double TickHZ = 144;

    //Imgui
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private String glslVersion = null;
    private ImGuiLayer imguilayer;

    Map map = new Map();


    public WindowManager(ImGuiLayer layer){
        imguilayer = layer;
    }



    public void run(){
        init();
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

    public void init(){

        glslVersion = "#version 330";
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 4);
        //Imgui
        /*
        initImGui();
        imGuiGlfw.init(window, true);
        imGuiGl3.init(glslVersion);

         */
        //

        System.out.println(GLFW.glfwGetVersionString());


        GLFWErrorCallback.createPrint(System.err).set();

        if(!GLFW.glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW_SAMPLES, 4);


        window = GLFW.glfwCreateWindow(width, height, "OpenEngine: "+FPS, NULL, NULL);
        if(window == NULL){
            throw new IllegalStateException("Unable to initialize window");
        }

        GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
            {
                GLFW.glfwSetWindowShouldClose(window, true);
            }
        });

        try(MemoryStack stack = stackPush()){
                        IntBuffer pWidth = stack.mallocInt(1);
                        IntBuffer pHeight = stack.mallocInt(1);

                        GLFW.glfwGetWindowSize(window, pWidth, pHeight);

                        GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

                        GLFW.glfwSetWindowPos(window,(vidmode.width() - pWidth.get(0)) / 2,(vidmode.height() - pHeight.get(0)) / 2);

                        GLFW.glfwMakeContextCurrent(window);
                        GLFW.glfwSwapInterval(0);
                        GLFW.glfwShowWindow(window);


        }

        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
        glfwSetInputMode(window, GLFW_STICKY_KEYS, GLFW_TRUE);
        GL.createCapabilities();



    }



    public void loop(){

        //GL.createCapabilities();

        float[] vertices = {-0.5f, 0.5f, 0.5f, //0
                -0.5f, -0.5f, 0.5f, //1
                0.5f, -0.5f, 0.5f, //2
                0.5f,0.5f, 0.5f, //3
                -0.5f,0.5f,-0.5f, //4
                -0.5f,-0.5f,-0.5f, //5
                0.5f,-0.5f,-0.5f, //6
                0.5f,0.5f,-0.5f}; //7

        int[] TestCubeindices = {
                0,1,3,//Front
                1,2,3,

                2,6,3,//Right side
                3,6,7,

                5,1,0,//Left side
                5,0,4,

                0,3,4, //Top
                3,7,4,

                5,2,1,//Bottom
                5,6,2,

                4,7,6,//Back
                5,4,6
        };

        float[] CubeVertices = {
                //Front
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f,0.5f, 0.5f,
                //Right side
                0.5f,0.5f, 0.5f,//3
                0.5f, -0.5f, 0.5f,//2
                0.5f,-0.5f,-0.5f,//6
                0.5f,0.5f,-0.5f,//7
                //left side
                -0.5f,0.5f,-0.5f,//4
                -0.5f,-0.5f,-0.5f,//5
                -0.5f, -0.5f, 0.5f,//1
                -0.5f, 0.5f, 0.5f,//0
                //back side
                0.5f,0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                -0.5f,-0.5f,-0.5f,
                -0.5f,0.5f,-0.5f,
                //top
                -0.5f,0.5f,-0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f,0.5f, 0.5f,
                0.5f,0.5f,-0.5f,
                //bottom
                -0.5f, -0.5f, 0.5f,
                -0.5f,-0.5f,-0.5f,
                0.5f,-0.5f,-0.5f,
                0.5f, -0.5f, 0.5f,

        };
        int[] CubeIndices = {
                //front
                0,1,3,
                1,2,3,
                //right
                4,5,7,
                5,6,7,
                //left
                8,9,11,
                9,10,11,
                //back
                12,13,15,
                13,14,15,
                //top
                16,17,19,
                17,18,19,
                //bottom
                20,21,23,
                21,22,23
        };
        float[] CubeUVs = {
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f
        };

        float[] SkyBoxVerts = {
                //back
                -100.5f, 100.5f, 100.5f,
                -100.5f, -100.5f, 100.5f,
                100.5f, -100.5f, 100.5f,
                100.5f,100.5f, 100.5f,
                //Right side
                100.5f,100.5f, 100.5f,//3
                100.5f, -100.5f, 100.5f,//2
                100.5f,-100.5f,-100.5f,//6
                100.5f,100.5f,-100.5f,//7
                //left side
                -100.5f,100.5f,-100.5f,//4
                -100.5f,-100.5f,-100.5f,//5
                -100.5f, -100.5f, 100.5f,//1
                -100.5f, 100.5f, 100.5f,//0
                //front side
                100.5f,100.5f,-100.5f,
                100.5f,-100.5f,-100.5f,
                -100.5f,-100.5f,-100.5f,
                -100.5f,100.5f,-100.5f,
                //top
                -100.5f,100.5f,-100.5f,
                -100.5f, 100.5f, 100.5f,
                100.5f,100.5f, 100.5f,
                100.5f,100.5f,-100.5f,
                //bottom
                -100.5f, -100.5f, 100.5f,
                -100.5f,-100.5f,-100.5f,
                100.5f,-100.5f,-100.5f,
                100.5f, -100.5f, 100.5f,
        };
        int[] SkyBoxIndices = {
                3,1,0,
                3,2,1,
                //right
                7,5,4,
                7,6,5,
                //left
                11,9,8,
                11,10,9,
                //back
                15,13,12,
                15,14,13,
                //top
                19,17,16,
                19,18,17,
                //bottom
                23,21,20,
                23,22,21
        };
        float[] SkyBoxUVs = {
                1.0f, 2/3f,
                1.0f, 1/3f,
                0.75f, 1/3f,
                0.75f, 2/3f,

                0.75f, 2/3f,
                0.75f, 1/3f,
                0.5f, 1/3f,
                0.5f, 2/3f,

                0.25f, 2/3f,
                0.25f, 1/3f,
                0.0f, 1/3f,
                0.0f, 2/3f,

                0.5f, 2/3f,
                0.5f, 1/3f,
                0.25f, 1/3f,
                0.25f, 2/3f,

                0.25f, 2/3f,
                0.25f, 1f,
                0.5f, 1f,
                0.5f, 2/3f,

                0.25f, 0f,
                0.25f, 1/3f,
                0.5f, 1/3f,
                0.5f, 0f,
        };




        int[] indices = {0,1,3,
                        3,1,2};
        float[] uvs = {
                0.0f, 1.0f,//A0 Bottom left
                0.0f, 0.0f,//A1 Bottom right
                1.0f, 0.0f,//A2 Top Right
                1.0f, 1.0f,//A3 Top left
                1.0f, 1.0f,//A4
                1.0f, 0.0f,//A5
                2.0f, 0.0f,//A6
                2.0f, 1.0f//A7
            };

        float[] cubeNormals =
                {
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,
                        0.0f, 0.0f, 1.0f,

                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,
                        1.0f, 0.0f, 0.0f,

                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,
                        0.0f, 0.0f, -1.0f,

                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,
                        -1.0f, 0.0f, 0.0f,

                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,
                        0.0f, 1.0f, 0.0f,

                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
                        0.0f, -1.0f, 0.0f,
        };




        //Mesh Tri = MeshLoader.createMesh(vertices, uvs, indices).addTexture("texture.jpg");
        //Mesh Cube = MeshLoader.createMesh(CubeVerts, uvs, CubeIndis);
        //Mesh TestCube = MeshLoader.createMesh(vertices, uvs, TestCubeindices).addTexture("texture.png");

        //Mesh Cube = MeshLoader.createMesh(CubeVertices, CubeUVs, CubeIndices, cubeNormals).addTexture("orange.png");


        //Mesh SkyBox = MeshLoader.createMesh(SkyBoxVerts, SkyBoxUVs, SkyBoxIndices).addTexture("SkyBox.png");




        //int texture = Texture.loadTexture("texture.png");
        renderer = new render();
        ObjectLoader objectLoader = new ObjectLoader();
        //source = new LightSource(20, 8, 5, 1, 1, 1);

        source = new LightSource(20, 8, 5, 0.0f, 0.0f, 1.0f);
        sourcetwo = new LightSource(0, 0, 0, 1.0f, 0.0f, 0.0f);

        lightSourcesList.add(source);
        lightSourcesList.add(sourcetwo);
        //lightSourcesList.add(source);




        float angle = 0.1f;

        Mesh SkyBox = objectLoader.createSkyBox(800f, "Galaxy.png");
        Mesh sphere = objectLoader.Sphere(5,50,50).addTexture("white.jpg");//
        Mesh lightbulb = objectLoader.Sphere(0.5f, 30, 30).addTexture("white.jpg");
        Mesh pointlight = objectLoader.Sphere(0.5f, 30, 30).addTexture("white.jpg");
        Mesh Cube = objectLoader.createCube(1).addTexture("white.jpg");//
        Mesh cube = objectLoader.createCube(5).addTexture("ctexture.png");
        //Mesh cube = objectLoader.createCube(5).addTexture("ctexture.png").addTexture("texture.png");



        double lastTime = glfwGetTime();
        int Frames = 0;

        //double TickHZ = 60;
        int tickCounter = 0;

        while(!GLFW.glfwWindowShouldClose(window)){
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);

            double currentTime = glfwGetTime();
            Frames++;
            if(currentTime - lastTime >= 1.0)
            {
            WindowManager.FPS = (int)((1/(currentTime-lastTime))*Frames);
            Frames = 0;
            lastTime += 1;
            GLFW.glfwSetWindowTitle(window, "OpenEngine: "+WindowManager.FPS);
            }

            //


            renderer.cleanup();

            /*
            renderer.draw(lightbulb, source.getLightPosition().x, source.getLightPosition().y, source.getLightPosition().z, true, 0);
            renderer.draw(pointlight, sourcetwo.getLightPosition().x, sourcetwo.getLightPosition().y, sourcetwo.getLightPosition().z, true, 0);
            renderer.draw(Cube,-20f, 0.0f, 3.0f, false, 0);
            renderer.draw(sphere, -2, -5.0f, 10.0f, false, 0);
            renderer.draw(sphere, 0.0f, 0.0f, -15f, false, 0);
            renderer.draw(SkyBox, 0.0f, 0.0f, 0.0f, true, 0);
            renderer.draw(cube, 20f, 2f, -2f, false, 0);

             */
            //map.dynamic();
            for(Map.object object : map.getObjects()){
                renderer.draw(object.element(), object.x(), object.y(), object.z(), object.fullbright(), object.scale());
            }
            //map.getObjects().removeLast();


            if(tickCounter >= (FPS/TickHZ)) {
                renderer.input();
                source.rotate(angle);
                //map.getLights().getFirst().rotate(angle);
                angle += 0.01;
                //System.out.println(tickCounter);
                tickCounter = 0;
            }
            //System.out.println(source.getLightPosition());
            tickCounter += 1;

            //imgui

            imGuiGlfw.newFrame();
            ImGui.newFrame();

            imguilayer.imgui();

            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            if(ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)){
                final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                GLFW.glfwMakeContextCurrent(backupWindowPtr);
            }

            //GLFW.glfwSwapBuffers(window);
            //GLFW.glfwPollEvents();


            //



            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
            //System.out.println("loop");
        }

    }

    public long getWindow(){
        return window;
    }


    public render getRender(){
        return renderer;
    }

    public LightSource getLightSource(){
        return source;
    }
    public ArrayList<Float> getLightSourcesPos(){
        ArrayList list = new ArrayList<>();
        for (LightSource lightSource : lightSourcesList) {
            list.add(lightSource.getLightPosition().x);
            list.add(lightSource.getLightPosition().y);
            list.add(lightSource.getLightPosition().z);
        }
        return list;
    }

    public ArrayList<Float> getLightSourceColor(){
        ArrayList list = new ArrayList<>();
        for (LightSource lightSource : lightSourcesList) {
            list.add(lightSource.getLightColor().x);
            list.add(lightSource.getLightColor().y);
            list.add(lightSource.getLightColor().z);
        }
        return list;
    }

    public LightSource[] getLightSourceArray(){
        /*
        LightSource[] array = new LightSource[lightSourcesList.size()];
        return array = lightSourcesList.toArray(array);

         */
        LightSource[] array = new LightSource[map.getLights().size()];
        return array = map.getLights().toArray(array);
    }



    public boolean isKeyPressed(int keycode){
        if(GLFW.glfwGetKey(window, keycode) == GLFW_PRESS){
            return true;
        }
        else {
            return false;
        }
    }
    public boolean isKeyReleased(int keycode){
        if(GLFW.glfwGetKey(window, keycode) == GLFW_RELEASE){
            return true;
        }
        else{
            return false;
        }
    }

    public void destroy(){
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(window);
        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }

    private void initImGui(){
        ImGui.createContext();
        imGuiGlfw.init(window, true);
        imGuiGl3.init(glslVersion);
    }
    public Map getMap(){
        return map;
    }








}
