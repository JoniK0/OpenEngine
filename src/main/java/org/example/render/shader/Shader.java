package org.example.render.shader;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.example.Main;
import org.example.render.LightSource;
import org.example.render.Spotlight;
import org.example.render.Sun;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL44;

public abstract class Shader {
    private int programID;
    private int vertexID;
    private int fragmentID;

    private FloatBuffer matrix = BufferUtils.createFloatBuffer(16);

    public Shader(String Vert, String Frag) {
        vertexID = loadShader(Vert, GL44.GL_VERTEX_SHADER);
        fragmentID = loadShader(Frag, GL44.GL_FRAGMENT_SHADER);
        programID = GL44.glCreateProgram();
        GL44.glAttachShader(programID, vertexID);
        GL44.glAttachShader(programID, fragmentID);
        bindAttributes();
        GL44.glLinkProgram(programID);
        GL44.glValidateProgram(programID);
        getAllUniformLocations();
        System.out.println("Shader initialized");


    }

    public void start() {
        GL44.glUseProgram(programID);
    }

    public void stop() {
        GL44.glUseProgram(0);
    }

    protected abstract void bindAttributes();

    protected abstract void getAllUniformLocations();

    private static int loadShader(String file, int type) {
        StringBuilder shaderSource = new StringBuilder();
        try {

            BufferedReader reader = new BufferedReader(new InputStreamReader(Shader.class.getResourceAsStream("/shaders/" + file)));

            shaderSource.append("#version 450 core").append("\n");
            shaderSource.append("#define numSpotLights " + Main.getWindowManager().getMap().getNumSpotLights()).append("\n");
            shaderSource.append("#define numDirLights " + Main.getWindowManager().getMap().getNumDirLights()).append("\n");
            if (Main.getWindowManager().getMap().getNumDirLights() == 0) {
                shaderSource.append("#define MAX_POINT_LIGHTS 1").append("\n");
            } else {
                shaderSource.append("#define MAX_POINT_LIGHTS " + Main.getWindowManager().getMap().getNumDirLights()).append("\n");
            }

            if (Main.getWindowManager().getMap().getNumSpotLights() == 0) {
                shaderSource.append("#define MAX_SPOT_LIGHTS 1").append("\n");
            } else {
                shaderSource.append("#define MAX_SPOT_LIGHTS " + Main.getWindowManager().getMap().getNumSpotLights()).append("\n");
            }

            System.out.println("NUMDIRLIGHTS: " + Main.getWindowManager().getMap().getNumDirLights());

            System.out.println(file);
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Can't read file");
            e.printStackTrace();
            System.exit(-1);
        }
        int ID = GL44.glCreateShader(type);
        GL44.glShaderSource(ID, shaderSource);
        GL44.glCompileShader(ID);


        if (GL20.glGetShaderi(ID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.out.println(GL20.glGetShaderInfoLog(ID, 512));
            System.err.println("Couldn't compile the shader");
            System.exit(-1);
        }
        System.out.println("Shader loaded " + ID);
        return ID;
    }

    protected int getUniformLocation(String uniformName) {
        return GL44.glGetUniformLocation(programID, uniformName);
    }

    public void setUniform(String name, Matrix4f value) {
        FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
        matrix.clear();
        value.get(matrix);
        GL44.glUniformMatrix4fv(getUniformLocation(name), true, matrix);
    }

    protected void bindAttribute(int attribute, String variableName) {
        GL44.glBindAttribLocation(programID, attribute, variableName);
    }

    public void loadFloat(String name, float value) {
        GL44.glUniform1f(getUniformLocation(name), value);
    }

    public void loadVector(String name, Vector3f vector) {
        GL44.glUniform3f(getUniformLocation(name), vector.x, vector.y, vector.z);
    }

    public void loadInt(String name, int integer) {
        GL44.glUniform1i(getUniformLocation(name), integer);
    }

    public void loadBoolean(String name, boolean value) {
        int tovec = 0;
        if (value) {
            tovec = 1;
        }
        GL44.glUniform1i(getUniformLocation(name), tovec);
    }

    public void loadList(String name, ArrayList<Float> list) {
        float[] array = new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        GL44.glUniform1fv(getUniformLocation(name), array);
    }

    public void loadSun(Sun sun) {
        if (sun != null) {
            GL20.glUniform3f(getUniformLocation("directSun.direction"), sun.getDirection().x, sun.getDirection().y, sun.getDirection().z);
            GL20.glUniform4f(getUniformLocation("directSun.color"), sun.getColor().x, sun.getColor().y, sun.getColor().z, 1);
        }
    }

    public void loadLights(LightSource list[]) {
        int numLights = 0;
        int numSpotLights = 0;
        int i = 0;
        for (LightSource light : list) {

            if (light instanceof Spotlight) {
                GL20.glUniform3f(getUniformLocation("trashcan[" + numSpotLights + "].lightpos2"), light.getLightPosition().x, light.getLightPosition().y, light.getLightPosition().z);
                GL20.glUniform4f(getUniformLocation("trashcan[" + numSpotLights + "].color2"), list[i].getLightColor().x, list[i].getLightColor().y, list[i].getLightColor().z, 1);

                GL20.glUniform1f(getUniformLocation("trashcan[" + numSpotLights + "].ambient2"), 0.2f);
                GL20.glUniform1f(getUniformLocation("trashcan[" + numSpotLights + "].specular2"), 0.5f);
                GL20.glUniform1f(getUniformLocation("trashcan[" + numSpotLights + "].shininess2"), 128f);

                GL20.glUniform1f(getUniformLocation("trashcan[" + numSpotLights + "].constant2"), 1f);
                GL20.glUniform1f(getUniformLocation("trashcan[" + numSpotLights + "].linear2"), 0.03f);
                GL20.glUniform1f(getUniformLocation("trashcan[" + numSpotLights + "].quadratic2"), 0.0014f);

                GL20.glUniform3f(getUniformLocation("trashcan[" + numSpotLights + "].direction2"), ((Spotlight) list[i]).getDirection().x, ((Spotlight) list[i]).getDirection().y, ((Spotlight) list[i]).getDirection().z);
                GL20.glUniform1f(getUniformLocation("trashcan[" + numSpotLights + "].cutOff2"), ((Spotlight) list[i]).getCutoff());

                numSpotLights += 1;
            } else {

                GL20.glUniform3f(getUniformLocation("pointlightlist[" + numLights + "].lightpos"), light.getLightPosition().x, list[i].getLightPosition().y, list[i].getLightPosition().z);
                GL20.glUniform4f(getUniformLocation("pointlightlist[" + numLights + "].color"), list[i].getLightColor().x, list[i].getLightColor().y, list[i].getLightColor().z, 1);

                GL20.glUniform1f(getUniformLocation("pointlightlist[" + numLights + "].ambient"), 0.02f);
                GL20.glUniform1f(getUniformLocation("pointlightlist[" + numLights + "].specular"), 0.5f);
                GL20.glUniform1f(getUniformLocation("pointlightlist[" + numLights + "].shininess"), 128f);

                GL20.glUniform1f(getUniformLocation("pointlightlist[" + numLights + "].constant"), 1f);
                GL20.glUniform1f(getUniformLocation("pointlightlist[" + numLights + "].linear"), 0.03f);
                GL20.glUniform1f(getUniformLocation("pointlightlist[" + numLights + "].quadratic"), 0.0014f);

                numLights += 1;
            }
            i += 1;

        }

        GL20.glUniform1i(getUniformLocation("pointlightlist_size"), numLights);
        GL20.glUniform1f(getUniformLocation("spotlightlist_size"), numSpotLights);
    }
}
