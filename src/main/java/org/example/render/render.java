package org.example.render;

import imgui.ImGui;
import org.example.ImGuiLayer;
import org.example.Main;
import org.example.WindowManager;
import org.example.render.Map.Map;
import org.example.render.shader.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class render {

    ShaderTextured shader = new ShaderTextured();
    ShaderSky skyShader = new ShaderSky();
    ShadowPass shadowPass = new ShadowPass();
    ShaderColored colorShader = new ShaderColored();
    Transformations transform = new Transformations();
    public static Camera cam = new Camera(0, 0, -1);
    public static Camera cam2 = new Camera(0, 0, -100);
    public static Camera activeCam = cam;
    WindowManager windowmanager = Main.getWindowManager();
    public static boolean globalFullbright = false;
    public int depthMapFBO;
    public int depthMap;
    public Matrix4f lightSpaceMatrix;

    public int numLoadedLights = windowmanager.getMap().getLights().size();

    public void cleanup() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
    }

    public void updateShader() {
        this.shader = new ShaderTextured();
    }

    public void drawSkybox(Mesh Skybox, float transformX, float transformY, float transformZ, float sizeScale) {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        try (MemoryStack stack = MemoryStack.stackPush()) {

            IntBuffer oldCullFaceMode = stack.mallocInt(1);
            IntBuffer oldDepthFunc = stack.mallocInt(1);
            GL11.glGetIntegerv(GL11.GL_CULL_FACE_MODE, oldCullFaceMode);
            GL11.glGetIntegerv(GL11.GL_DEPTH_FUNC, oldDepthFunc);


            //GL11.glGetIntegerv(GL11.GL_CULL_FACE_MODE, oldCullFaceMode);

            GL11.glCullFace(GL11.GL_FRONT);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glEnable(GL40.GL_DEPTH_CLAMP);

            GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL20.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL20.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL20.GL_TEXTURE_CUBE_MAP, GL20.GL_TEXTURE_WRAP_R, GL20.GL_CLAMP_TO_EDGE);

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

            if (GL20.glGetError() != 0) {
                //System.out.println(GL20.glGetError());
                //System.out.println("error");
            }
            GL11.glDepthFunc(oldDepthFunc.get());
            GL11.glCullFace(oldCullFaceMode.get());


        } catch (Exception e) {
            e.printStackTrace();
        }


        skyShader.stop();
        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDisable(GL40.GL_DEPTH_CLAMP);

    }

    public int genDepthFBO() {
        //generate depthMap
        int depthMapFBO = 0;
        depthMapFBO = GL30.glGenFramebuffers();

        //generate depthTexture

        depthMap = GL30.glGenTextures();
        GL30.glBindTexture(GL11.GL_TEXTURE_2D, depthMap);
        GL30.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, Variables.shadowWidth, Variables.shadowHeight, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        //bind Framebuffer and attach depthtexture

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO);
        GL32.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthMap, 0);
        GL30.glDrawBuffer(0);
        GL30.glReadBuffer(0);

        if(GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE)
        {
            System.out.println("Framebuffer Error: "+GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER));
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        return depthMapFBO;
    }

    public void configShadowPass(Matrix4f worldTransform, Matrix4f rotation)
    {
        Matrix4f ortho = transform.genOrthoMatrix(-10.0f, 10.0f, -10.0f, 10.0f, 1.0f, 7.5f);
        Matrix4f lightTransform = Camera.CameraTransformation(new Vector3f(30, 10, 35), new Vector3f(-1, -1, 0), new Vector3f(0, 1, 0));
        lightSpaceMatrix = ortho.mul(lightTransform);
        shadowPass.start();
        shadowPass.setUniform("lightSpaceMatrix", lightSpaceMatrix);
        shadowPass.setUniform("model", worldTransform.mul(rotation));
    }

    public void initDepthBuffer()
    {
        depthMapFBO = genDepthFBO();
    }

    public void draw(Mesh mesh, float transformX, float transformY, float transformZ, boolean fullbright, float rotX, float rotY, float rotZ, float sizeScale) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL40.glDepthMask(true);

        GL11.glEnable(GL11.GL_BLEND);


        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        //GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        //GL11.glStencilMask(0xFF);


        //GL40.glBlendEquation(GL40.GL_FUNC_ADD);
        //GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);

        GL11.glCullFace(GL11.GL_BACK);

        //System.out.println(activeCam.yaw);

        if (ImGuiLayer.polygonmode) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }

        ////////////////////////////////////////////////////////////////////////////////////
        //ShadowPass

        /*

        configShadowPass(transform.getWorldTransformation(transformX, transformY, transformZ, 0, sizeScale), transform.RotationMatrix(rotX, rotY, rotZ));
        GL11.glViewport(0, 0, Variables.shadowWidth, Variables.shadowHeight);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, depthMapFBO);
        GL30.glClear(GL11.GL_DEPTH_BUFFER_BIT);

        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);


        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        //GL30.glDeleteFramebuffers(depthMapFBO);

        shadowPass.stop();

         */



        /////////////////////////////////////////////////////////////////////////////////////

        GL11.glViewport(0, 0, WindowManager.width, WindowManager.height);

        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_REPEAT);

        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        //GL30.glTexParameteri(GL11.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 3);

        if (windowmanager.getMap().getLights().size() != numLoadedLights) {
            numLoadedLights = windowmanager.getMap().getLights().size();
            updateShader();
            System.out.println("SHADER UPDATED");
        }

        shader.start();


        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilMask(0xFF);

        //GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        shader.loadLights(windowmanager.getLightSourceArray());
        //System.out.println(windowmanager.getLightSourceArray().length);
        shader.loadSun(windowmanager.getSun());

        shader.setUniform("Projection", transform.getProjectionMatrix());
        shader.setUniform("WorldTransform", transform.getWorldTransformation(transformX, transformY, transformZ, 0, sizeScale));
        //shader.setUniform("lightSpaceMatrix", lightSpaceMatrix);

        shader.loadVector("camPos", activeCam.m_pos);
        shader.loadVector("camDirection", activeCam.m_target);

        shader.loadBoolean("Fullbright", fullbright);
        shader.loadBoolean("globalFullbright", globalFullbright);
        shader.loadBoolean("flash", Variables.flashlight);

        shader.setUniform("AxisRotation", transform.RotationMatrix(rotX, rotY, rotZ));
        shader.loadVector("lightColor", windowmanager.getLightSource().getLightColor());
        //shader.setUniform("CameraTransform", transform.getCameraTransformation());

        shader.setUniform("CameraTransform", activeCam.getMatrix()); /////////////////////////////////////


        shader.loadInt("shadowMap", 12);





        if (mesh.isMultex()) {

            for (int i = 0; i <= 5; i++) {
                //shader.loadInt("textureSampler"+Integer.toString(i), i);
                shader.loadInt("textureSamplers[" + Integer.toString(i) + "]", i);
            }
            for (int i = 0; i < mesh.getMultextures().size(); i++) {

                GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);

                GL13.glEnable(GL11.GL_TEXTURE_2D);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getMultextures().get(i));

                GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
            }

            for (int i = 0; i <= 5; i++) {
                shader.loadInt("normalMaps[" + Integer.toString(i) + "]", i + 6);
            }
            for (int i = 0; i < mesh.getNormalMaps().size(); i++) {
                //GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getNormalMaps().get(i));
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + 6 + i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getNormalMaps().get(i));

            }


            /*
            GL13.glActiveTexture(GL13.GL_TEXTURE12);
            GL13.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthMap);
            GL13.glActiveTexture(0);

             */



            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

            if (GL20.glGetError() != 0) {
                System.out.println(GL20.glGetError());
            }

        } else {
            shader.loadInt("textureSampler", 0);
            shader.loadInt("normalMap0", 6);


            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getTexture());
            GL13.glActiveTexture(GL13.GL_TEXTURE6);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getNormalMap());
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);



        }
        GL30.glBindVertexArray(0);

        shader.stop();


        //System.out.println(GL20.glGetError());

        GL40.glGetString(GL40.GL_VERSION);

        GL11.glDisable(GL11.GL_BLEND);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        //shader.stop();
    }

    public void colorShaderFunc(Mesh mesh) {

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL40.glDepthMask(true);

        GL11.glEnable(GL11.GL_BLEND);


        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        //GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        //GL11.glStencilMask(0xFF);


        //GL40.glBlendEquation(GL40.GL_FUNC_ADD);
        //GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);

        GL11.glCullFace(GL11.GL_BACK);

        //System.out.println(activeCam.yaw);

        if (ImGuiLayer.polygonmode) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }


        colorShader.start();
        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);

        shader.setUniform("Projection", transform.getProjectionMatrix());
        shader.setUniform("WorldTransform", transform.getWorldTransformation(0, 0, 0, 0, 0));

        shader.setUniform("AxisRotation", transform.RotationMatrix(0, 0, 0));
        //shader.setUniform("CameraTransform", transform.getCameraTransformation());

        shader.setUniform("CameraTransform", activeCam.getMatrix()); /////////////////////////////////////

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL30.glBindVertexArray(0);
        colorShader.stop();
    }

    public void input() {
        activeCam.keyListener();
    }

    public void test(Mesh mesh, float transformX, float transformY, float transformZ, boolean fullbright, float rotX, float rotY, float rotZ, float sizeScale) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        GL11.glEnable(GL11.GL_STENCIL_TEST);

        GL40.glDepthMask(true);

        GL11.glEnable(GL11.GL_BLEND);


        GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_REPLACE);
        //GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        //GL11.glStencilMask(0xFF);


        //GL40.glBlendEquation(GL40.GL_FUNC_ADD);
        //GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);

        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);

        GL11.glCullFace(GL11.GL_BACK);

        //System.out.println(activeCam.yaw);

        if (ImGuiLayer.polygonmode) {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
        } else {
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        }

        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_REPEAT);

        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        //GL30.glTexParameteri(GL11.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAX_LEVEL, 3);

        colorShader.start();

        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glStencilMask(0xFF);

        //GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);

        shader.loadLights(windowmanager.getLightSourceArray());
        shader.loadSun(windowmanager.getSun());

        shader.setUniform("Projection", transform.getProjectionMatrix());
        shader.setUniform("WorldTransform", transform.getWorldTransformation(transformX, transformY, transformZ, 0, sizeScale));

        shader.loadVector("camPos", activeCam.m_pos);
        shader.loadVector("camDirection", activeCam.m_target);

        shader.loadBoolean("Fullbright", fullbright);
        shader.loadBoolean("globalFullbright", globalFullbright);
        shader.loadBoolean("flash", Variables.flashlight);

        shader.setUniform("AxisRotation", transform.RotationMatrix(rotX, rotY, rotZ));
        shader.loadVector("lightColor", windowmanager.getLightSource().getLightColor());
        //shader.setUniform("CameraTransform", transform.getCameraTransformation());

        shader.setUniform("CameraTransform", activeCam.getMatrix()); /////////////////////////////////////


        if (mesh.isMultex()) {

            for (int i = 0; i <= 5; i++) {
                //shader.loadInt("textureSampler"+Integer.toString(i), i);
                shader.loadInt("textureSamplers[" + Integer.toString(i) + "]", i);
            }
            for (int i = 0; i < mesh.getMultextures().size(); i++) {

                GL13.glActiveTexture(GL13.GL_TEXTURE0 + i);

                GL13.glEnable(GL11.GL_TEXTURE_2D);

                GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getMultextures().get(i));

                GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR_MIPMAP_LINEAR);
                GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
            }

            for (int i = 0; i <= 5; i++) {
                shader.loadInt("normalMaps[" + Integer.toString(i) + "]", i + 6);
            }
            for (int i = 0; i < mesh.getNormalMaps().size(); i++) {
                //GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getNormalMaps().get(i));
                GL13.glActiveTexture(GL13.GL_TEXTURE0 + 6 + i);
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getNormalMaps().get(i));

            }

            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

            if (GL20.glGetError() != 0) {
                System.out.println(GL20.glGetError());
            }

        } else {
            shader.loadInt("textureSampler", 0);
            shader.loadInt("normalMap0", 6);

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getTexture());
            GL13.glActiveTexture(GL13.GL_TEXTURE6);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, mesh.getNormalMap());
            GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        }
        GL30.glBindVertexArray(0);

        colorShader.stop();

        /*

        GL11.glStencilFunc(GL11.GL_NOTEQUAL, 1, 0xFF);
        GL11.glStencilMask(0x00);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        colorShader.start();

        GL30.glBindVertexArray(mesh.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL20.glEnableVertexAttribArray(3);

        colorShader.setUniform("Projection", transform.getProjectionMatrix());
        colorShader.setUniform("WorldTransform", transform.getWorldTransformation(transformX, transformY, transformZ, 1, sizeScale*0.7f));
        colorShader.setUniform("AxisRotation", transform.RotationMatrix(rotX, rotY, rotZ));
        colorShader.setUniform("CameraTransform", activeCam.getMatrix());

        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);

        GL11.glStencilMask(0xFF);
        GL11.glStencilFunc(GL11.GL_ALWAYS, 1, 0xFF);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        colorShader.stop();

        if(GL20.glGetError() != 0){
            System.out.println(GL20.glGetError());
        }

         */


        //System.out.println(GL20.glGetError());

        GL40.glGetString(GL40.GL_VERSION);

        GL11.glDisable(GL11.GL_BLEND);

        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        //shader.stop();
    }
}
