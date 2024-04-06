package org.example.render;
import java.nio.FloatBuffer; //The buffers that the Vertex data is ultimately stored in
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List; //List and ArrayLists are containers for storing data, in this case the VBO/VAO IDs

import org.lwjgl.BufferUtils; //For creating the FloatBuffer
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class MeshLoader {

    private static List<Integer> vaos = new ArrayList<Integer>();
    private static List<Integer> vbos = new ArrayList<Integer>();

    private static FloatBuffer createFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static IntBuffer createIntBuffer(int[] data){
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static void storeData(int attribute, int dimensions, float[] data){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = createFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attribute, dimensions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        //System.out.println(data[2]);
    }

    private static void bindIndices(int[] data){
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = createIntBuffer(data);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    public static Mesh createMesh(float[] positions,float[] UVs, int[] indices, float[] normals){
        int vao = genVAO();
        storeData(0,3,positions);
        storeData(1,2,UVs);
        storeData(2, 3, normals);
        //System.out.println(normals[2]);
        bindIndices(indices);
        GL30.glBindVertexArray(0);
        //System.out.println("vao: "+vao);
        //System.out.println(vbos);
        return new Mesh(vao, indices.length);
    }

    private static int genVAO(){
        int vao = GL30.glGenVertexArrays();
        vaos.add(vao);
        GL30.glBindVertexArray(vao);
        return vao;
    }


}
