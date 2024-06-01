package org.example.render.shader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import org.example.render.LightSource;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryUtil;


public abstract class Shader {
    private int programID;
    private int vertexID;
    private int fragmentID;

    private FloatBuffer matrix = BufferUtils.createFloatBuffer(16);

    public Shader(String Vert, String Frag){
        vertexID = loadShader(Vert,GL20.GL_VERTEX_SHADER);
        fragmentID = loadShader(Frag,GL20.GL_FRAGMENT_SHADER);
        programID = GL20.glCreateProgram();
        GL20.glAttachShader(programID, vertexID);
        GL20.glAttachShader(programID, fragmentID);
        bindAttributes();
        GL20.glLinkProgram(programID);
        GL20.glValidateProgram(programID);
        getAllUniformLocations();
        System.out.println("Shader initialized");


    }

    public void start(){
        GL20.glUseProgram(programID);
    }
    public void stop(){
        GL20.glUseProgram(0);
    }

    protected  abstract  void bindAttributes();

    protected  abstract  void getAllUniformLocations();

    private static int loadShader(String file, int type){
        StringBuilder shaderSource = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new FileReader("res/shaders/"+file));
            System.out.println(file);
            String line;
            while((line = reader.readLine()) != null){
                shaderSource.append(line).append("\n");
            }
            reader.close();
        }catch (IOException e){
            System.err.println("Can't read file");
            e.printStackTrace();
            System.exit(-1);
        }
        int ID = GL20.glCreateShader(type);
        GL20.glShaderSource(ID, shaderSource);
        GL20.glCompileShader(ID);
        if(GL20.glGetShaderi(ID, GL20.GL_COMPILE_STATUS)==GL11.GL_FALSE){
            System.out.println(GL20.glGetShaderInfoLog(ID, 512));
            System.err.println("Couldn't compile the shader");
            System.exit(-1);
        }
        System.out.println("Shader loaded "+ID);
        return ID;
    }

    protected int getUniformLocation(String uniformName) {
        return GL20.glGetUniformLocation(programID, uniformName);
    }

    public void setUniform(String name, Matrix4f value) {
        FloatBuffer matrix = BufferUtils.createFloatBuffer(16);
        matrix.clear();
        value.get(matrix);
        //matrix.flip();
        GL20.glUniformMatrix4fv(getUniformLocation(name), true, matrix);
    }

    protected void bindAttribute(int attribute, String variableName) {
        GL20.glBindAttribLocation(programID, attribute, variableName);
    }

    public void loadFloat(String name, float value){
        GL20.glUniform1f(getUniformLocation(name), value);
    }
    public void loadVector(String name, Vector3f vector){
        GL20.glUniform3f(getUniformLocation(name), vector.x, vector.y, vector.z);
    }

    public void loadBoolean(String name, boolean value){
        int tovec = 0;
        if(value){
            tovec = 1;
        }
        //GL20.glUniform1f(getUniformLocation(name), tovec);
        GL20.glUniform1i(getUniformLocation(name), tovec);
    }

    public void loadList(String name, ArrayList<Float> list){
        float[] array = new float[list.size()];
        for(int i = 0; i < list.size(); i++){
            array[i] = list.get(i);
        }
        GL20.glUniform1fv(getUniformLocation(name), array);
    }

    public void loadLights(LightSource list[]){
        int numLights = list.length;
        for(int i = 0; i < numLights; i++) {

            //System.out.println("i:"+i+" pos: "+list[i].getLightPosition());
            //System.out.println("i: "+i+" color: "+list[i].getLightColor());

            GL20.glUniform3f(getUniformLocation("pointlightlist["+i+"].lightpos"), list[i].getLightPosition().x, list[i].getLightPosition().y, list[i].getLightPosition().z);
            GL20.glUniform4f(getUniformLocation("pointlightlist["+i+"].color"), list[i].getLightColor().x, list[i].getLightColor().y, list[i].getLightColor().z, 1);

            GL20.glUniform1f(getUniformLocation("pointlightlist["+i+"].ambient"), 0.2f);
            GL20.glUniform1f(getUniformLocation("pointlightlist["+i+"].specular"), 0.5f);
            GL20.glUniform1f(getUniformLocation("pointlightlist["+i+"].shininess"), 128f);

            GL20.glUniform1f(getUniformLocation("pointlightlist["+i+"].constant"), 1f);
            GL20.glUniform1f(getUniformLocation("pointlightlist["+i+"].linear"), 0.03f);
            GL20.glUniform1f(getUniformLocation("pointlightlist["+i+"].quadratic"), 0.0014f);
        }
    }

    protected void loadMatrix(int location, Matrix4f value){
        value.get(matrix);
        matrix.flip();
        GL20.glUniformMatrix4fv(location, false, matrix);
    }

}
