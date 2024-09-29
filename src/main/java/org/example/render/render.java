package org.example.render;

import imgui.ImGui;
import org.example.ImGuiLayer;
import org.example.Main;
import org.example.WindowManager;
import org.example.render.Map.Map;
import org.example.render.shader.ShaderSky;
import org.example.render.shader.ShaderTextured;
import org.lwjgl.opengl.*;
import org.example.render.shader.Shader;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

public class render {

    ShaderTextured shader = new ShaderTextured();
    ShaderSky skyShader = new ShaderSky();
    Transformations transform = new Transformations();
    public static Camera cam = new Camera(0, 0, -1);
    public static Camera cam2 = new Camera(0, 0, -100);
    public static Camera activeCam = cam;
    WindowManager windowmanager = Main.getWindowManager();
    public static boolean globalFullbright = false;

    public void cleanup(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
    }
    public void drawSkybox(Mesh Skybox, float transformX, float transformY, float transformZ, float sizeScale){
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        try(MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer oldCullFaceMode = stack.mallocInt(1);
            IntBuffer oldDepthFunc = stack.mallocInt(1);
            GL11.glGetIntegerv(GL11.GL_CULL_FACE_MODE, oldCullFaceMode);
            GL11.glGetIntegerv(GL11.GL_DEPTH_FUNC, oldDepthFunc);


        //GL11.glGetIntegerv(GL11.GL_CULL_FACE_MODE, oldCullFaceMode);

        GL11.glCullFace(GL11.GL_FRONT);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glEnable(GL40.GL_DEPTH_CLAMP);

        GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP,GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP,GL11.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP,GL11.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP,GL20.GL_TEXTURE_WRAP_R, GL20.GL_CLAMP_TO_EDGE);

        GL20.glDepthMask(false);
        skyShader.start();

        skyShader.setUniform("Projection", transform.getProjectionMatrix());
        skyShader.setUniform("WorldTransform", transform.getWorldTransformation(transformX, transformY, transformZ, 0, sizeScale));
        skyShader.setUniform("View", activeCam.getMatrix());

        GL30.glBindVertexArray(Skybox.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL20.GL_TEXTURE_CUBE_MAP, Skybox.getTexture());
        skyShader.loadInt("skybox", 0);
        GL11.glDrawElements(GL11.GL_TRIANGLES, Skybox.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDepthMask(true);


        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        if(GL20.glGetError() != 0){
            //System.out.println(GL20.glGetError());
            //System.out.println("error");
        }
        GL11.glDepthFunc(oldDepthFunc.get());
        GL11.glCullFace(oldCullFaceMode.get());


        }
        catch(Exception e){
            e.printStackTrace();
        }


        skyShader.stop();
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDisable(GL40.GL_DEPTH_CLAMP);

    }
    public void draw(Mesh mesh, float transformX, float transformY, float transformZ, boolean fullbright, float rotX, float rotY, float rotZ, float sizeScale){
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL13.GL_MULTISAMPLE);

        GL11.glCullFace(GL11.GL_BACK);

        //System.out.println(activeCam.yaw);

        if(ImGuiLayer.polygonmode){
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }
        else{
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_REPEAT);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        shader.start();
        //GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        //GL13.glActiveTexture(GL13.GL_TEXTURE1);

        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getTexture());

        //cam.keyListener();
        shader.loadLights(windowmanager.getLightSourceArray());

        shader.setUniform("Projection", transform.getProjectionMatrix());
        shader.setUniform("WorldTransform", transform.getWorldTransformation(transformX, transformY, transformZ, 0, sizeScale));

        shader.loadVector("camPos", activeCam.m_pos);
        shader.loadVector("camDirection", activeCam.m_target);

        //System.out.println("target: " + cam.m_target);
        //System.out.println("pos" + cam.m_pos);

        shader.loadBoolean("Fullbright", fullbright);
        shader.loadBoolean("globalFullbright", globalFullbright);
        //shader.loadVector("lightSource", windowmanager.getLightSource().getLightPosition());

        //shader.loadList("LightPositions", windowmanager.getLightSourcesPos());
        //shader.loadList("LightColors", windowmanager.getLightSourceColor());

        //
        //shader.loadLights(windowmanager.getLightSourceArray());
        //

        shader.setUniform("AxisRotation", transform.RotationMatrix(rotX, rotY, rotZ));
        shader.loadVector("lightColor", windowmanager.getLightSource().getLightColor());
        //shader.setUniform("CameraTransform", transform.getCameraTransformation());

        shader.setUniform("CameraTransform", activeCam.getMatrix()); /////////////////////////////////////




        if(mesh.isMultex()){

            //System.out.println("hiii");

            /*
            shader.loadInt("textureSampler0", 0);
            shader.loadInt("textureSampler1", 1);
            shader.loadInt("textureSampler2", 2);
            shader.loadInt("textureSampler3", 3);
            shader.loadInt("textureSampler4", 4);
            shader.loadInt("textureSampler5", 5);
             */
            for(int i=0; i <= 5; i++){
                //shader.loadInt("textureSampler"+Integer.toString(i), i);
                shader.loadInt("textureSamplers["+Integer.toString(i)+"]", i);
            }
            for(int i=0; i<mesh.getMultextures().size(); i++){

                //System.out.println("size:"+mesh.getMultextures().size());
                GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getMultextures().get(i));
            }

            for(int i = 0; i <= 5; i++){
                shader.loadInt("normalMaps["+Integer.toString(i)+"]", i+6);
            }
            for(int i=0; i<mesh.getNormalMaps().size(); i++){
                GL13.glActiveTexture(GL13.GL_TEXTURE0+6+i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getNormalMaps().get(i));

                //System.out.println("id"+mesh.getVaoID());
                //System.out.println("normal"+mesh.getNormalMaps().get(i));
            }

            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        }
        else{
            shader.loadInt("textureSampler", 0);
            shader.loadInt("normalMap0", 6);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getTexture());
            GL13.glActiveTexture(GL13.GL_TEXTURE6);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getNormalMap());
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        }
        /////
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getTexture());

        //GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        /////

        if(GL20.glGetError() != 0){
            System.out.println(GL20.glGetError());
        }
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public void input(){
        activeCam.keyListener();
    }
}
