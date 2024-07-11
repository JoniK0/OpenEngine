package org.example.render;

import imgui.ImGui;
import org.example.ImGuiLayer;
import org.example.Main;
import org.example.WindowManager;
import org.example.render.Map.Map;
import org.example.render.shader.ShaderTextured;
import org.lwjgl.opengl.*;
import org.example.render.shader.Shader;

public class render {

    ShaderTextured shader = new ShaderTextured();
    Transformations transform = new Transformations();
    Camera cam = new Camera();
    WindowManager windowmanager = Main.getWindowManager();
    public static boolean globalFullbright = false;

    public void cleanup(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
    }
    public void draw(Mesh mesh, float transformX, float transformY, float transformZ, boolean fullbright, float rotX, float rotY, float rotZ, float sizeScale){
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL13.GL_MULTISAMPLE);

        if(ImGuiLayer.polygonmode){
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        }
        else{
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT);
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

        shader.setUniform("Projection", transform.getProjectionMatrix());
        shader.setUniform("WorldTransform", transform.getWorldTransformation(transformX, transformY, transformZ, 0, sizeScale));

        shader.loadVector("camPos", cam.m_pos);
        shader.loadVector("camDirection", cam.m_target);

        //System.out.println("target: " + cam.m_target);
        //System.out.println("pos" + cam.m_pos);

        shader.loadBoolean("Fullbright", fullbright);
        shader.loadBoolean("globalFullbright", globalFullbright);
        shader.loadVector("lightSource", windowmanager.getLightSource().getLightPosition());

        shader.loadList("LightPositions", windowmanager.getLightSourcesPos());
        shader.loadList("LightColors", windowmanager.getLightSourceColor());

        shader.loadLights(windowmanager.getLightSourceArray());

        shader.setUniform("AxisRotation", transform.RotationMatrix(rotX, rotY, rotZ));
        shader.loadVector("lightColor", windowmanager.getLightSource().getLightColor());
        //shader.setUniform("CameraTransform", transform.getCameraTransformation());

        shader.setUniform("CameraTransform", cam.getMatrix());




        if(mesh.isMultex()){

            //System.out.println("hiii");

            //GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getMultextures().get(0));
            shader.loadInt("textureSampler", 0);
            shader.loadInt("textureSampler1", 1);
            shader.loadInt("textureSampler2", 2);
            shader.loadInt("textureSampler3", 3);
            shader.loadInt("textureSampler4", 4);
            shader.loadInt("textureSampler5", 5);

            for(int i=0; i<mesh.getMultextures().size(); i++){

                GL13.glActiveTexture(GL13.GL_TEXTURE0+i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getMultextures().get(i));


                //GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount()/mesh.getMultextures().size(), GL11.GL_UNSIGNED_INT, 0);
            }
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        }
        else{
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getTexture());

            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        }
        /////
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getTexture());

        //GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        /////
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    public void input(){
        cam.keyListener();
    }
}
