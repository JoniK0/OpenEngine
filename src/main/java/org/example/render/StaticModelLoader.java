package org.example.render;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.assimp.AIMaterial.*;


public class StaticModelLoader {
    public static Mesh[] load(String modelDirName) throws Exception{
        File file = new File("res/textures/models/source/"+modelDirName+"/scene.gltf");
        String filepath = file.getAbsolutePath();
        //String resourcePath = "/home/joni/IdeaProjects/Github/OpenEngine/res/textures/models/source/"+modelName;
        return load(filepath, modelDirName, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
    }

    public static Mesh[] load(String resourcePath, String modelDirName, int flags) throws Exception{
        AIScene aiScene = aiImportFile(resourcePath, flags);
        //Assimp.aiReleaseImport(aiScene);
        if(aiScene == null){
            throw new Exception("Error: model cannot be loaded");
        }

        //Materials
        int numMaterials = aiScene.mNumMaterials();
        //System.out.println("numMaterials:"+numMaterials);
        PointerBuffer aiMaterials = aiScene.mMaterials();
        System.out.println("aiMaterial pointer: "+aiMaterials.get(0));
        List<String> textures = new ArrayList<>();
        for(int i = 0; i < numMaterials; i++){
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            System.out.println("aiMaterial.create:  "+aiMaterial);
            processMaterial(aiMaterial, textures, modelDirName);
        }
        //

        //Meshes
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshes = new Mesh[numMeshes];

        System.out.println("nummeshes"+numMeshes);

        for(int i = 0; i < numMeshes; i++){
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMeshes(aiMesh, textures, modelDirName);
            meshes[i] = mesh;
            System.out.println("meshes loaded: "+i);
        }
        //



        return meshes;
    }




    private static void processMaterial(AIMaterial aiMaterial, List<String> textures, String textureDir){

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_BASE_COLOR, 0, path, (IntBuffer) null, null, null, null, null, null);
        String textPath = path.dataString();
        //Texture texture = null;
        if(textPath != null && textPath.length() > 0){
            //System.out.println("hey im here "+textPath);
            File tex = new File(textPath);
            String texture = tex.getName();
            System.out.println("texture: "+texture);
            textures.add(texture);
        }
        //textures.add(textPath);
        System.out.println("textpath: "+textPath);
        System.out.println("test: ");

    }

    private static Mesh processMeshes(AIMesh aiMesh, List<String> textures, String modelDirName){

        List<Float> vertices = new ArrayList<>();
        List<Float> texture = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();


        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, texture);
        processIndices(aiMesh, indices);

        float[] Vertices = arrListToArrayFloat(vertices);
        float[] Texture = arrListToArrayFloat(texture);
        float[] Normals = arrListToArrayFloat(normals);
        int[] Indices = arrListToArrayInt(indices);

        //System.out.println("vertices:" + vertices+" count:"+ vertices.size());
        //System.out.println("normals:"+normals+" count:"+ normals.size());
        //System.out.println("indices:"+indices+" count:"+ normals.size());
        //System.out.println("texcoords:"+texture+" count:"+ normals.size());

        float[] texUnit = new float[Vertices.length];
        Mesh mesh = MeshLoader.createMesh(Vertices, Texture, Indices, Normals, texUnit);

        String tex;
        int materialIdx = aiMesh.mMaterialIndex();
        if( materialIdx >= 0 && materialIdx < textures.size()){
            tex = textures.get(materialIdx);
        }
        else{
            tex = "white.jpg";
        }

        //mesh.addTexture(tex);
        mesh.addTexture(tex, "res/textures/models/source/"+modelDirName+"/textures");


        return mesh;
    }

    private static float[] arrListToArrayFloat(List<Float> list){
        Float[] Array = new Float[list.size()];
        Array = list.toArray(Array);
        float[] array = new float[Array.length];
        for(int i = 0; i < Array.length; i++){
            array[i] = Array[i];
        }
        return array;
    }
    private static int[] arrListToArrayInt(List<Integer> list){
        Integer[] Array = new Integer[list.size()];
        Array = list.toArray(Array);
        int[] array = new int[Array.length];
        for(int i = 0; i < Array.length; i++){
            array[i] = Array[i];
        }
        return array;
    }

    private static void processVertices(AIMesh aiMesh, List<Float> vertices){
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while(aiVertices.remaining() > 0){
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }

    }
    private static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }
    private static void processTextCoords(AIMesh aiMesh, List<Float> texture){
        AIVector3D.Buffer aiTextCoords = aiMesh.mTextureCoords(0);
        while (aiTextCoords.remaining() > 0) {
            AIVector3D aiTextureCoord = aiTextCoords.get();
            texture.add(aiTextureCoord.x());
            texture.add(1-aiTextureCoord.y());
            //texture.add(aiTextureCoord.x());
        }
    }
    private static void processIndices(AIMesh aiMesh, List<Integer> indices){
        //System.out.println("num faces: "+aiMesh.mNumFaces());

        AIFace.Buffer aiFaces= aiMesh.mFaces();
        for (int i = 0;i < aiFaces.limit(); i++) {
            AIFace face = aiFaces.get(i);
            IntBuffer Indices = face.mIndices();

            if(face.mNumIndices() != 3 || Indices == null){
                throw new IllegalStateException();
            }
            //System.out.println("numindices:"+face.mNumIndices());
            //Indices.array();
            //System.out.println("Limit: "+Indices.limit());
            indices.add(Indices.get(0));
            indices.add(Indices.get(1));
            indices.add(Indices.get(2));
        }



    }

}
