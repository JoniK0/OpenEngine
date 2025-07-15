package org.example.render;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import static org.example.render.ObjectLoader.createTanBitan;
import static org.lwjgl.assimp.Assimp.*;


public class StaticModelLoader {
    public static Mesh[] load(String modelDirName) throws Exception {
        File file = new File(StaticModelLoader.class.getResource("/textures/models/source/" + modelDirName + "/scene.gltf").getFile());
        String filepath = file.getAbsolutePath();
        InputStream stream = StaticModelLoader.class.getResourceAsStream("/textures/models/source/" + modelDirName + "/scene.gltf");
        byte[] byteArray = stream.readAllBytes();
        ByteBuffer buffer = BufferUtils.createByteBuffer(byteArray.length);
        buffer.put(byteArray);
        buffer.flip();


        return load(buffer, filepath, modelDirName, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate | aiProcess_FixInfacingNormals);
    }

    public static Mesh[] load(ByteBuffer buffer, String resourcePath, String modelDirName, int flags) throws Exception {
        AIScene aiScene = aiImportFile(resourcePath, flags);

        if (aiScene == null) {
            throw new Exception("Error: model cannot be loaded " + aiGetErrorString());
        }

        //Materials
        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<String> textures = new ArrayList<>();
        List<String> normals = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial, textures, modelDirName, normals);
        }
        //

        //Meshes
        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();
        Mesh[] meshes = new Mesh[numMeshes];

        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMeshes(aiMesh, textures, modelDirName, normals);
            meshes[i] = mesh;
            System.out.println("meshes loaded: " + i);
        }

        return meshes;
    }


    private static void processMaterial(AIMaterial aiMaterial, List<String> textures, String textureDir, List<String> normals) {

        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_BASE_COLOR, 0, path, (IntBuffer) null, null, null, null, null, null);
        String textPath = path.dataString();
        if (textPath != null && textPath.length() > 0) {
            File tex = new File(textPath);
            String texture = tex.getName();
            System.out.println("texture: " + texture);
            textures.add(texture);
        }
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_NORMALS, 0, path, (IntBuffer) null, null, null, null, null, null);
        textPath = path.dataString();
        if (textPath != null && textPath.length() > 0) {
            File norm = new File(textPath);
            String normal = norm.getName();
            normals.add(normal);
        }
    }

    private static Mesh processMeshes(AIMesh aiMesh, List<String> textures, String modelDirName, List<String> norms) {

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

        float[] texUnit = new float[Vertices.length];

        float[][] TanBitan = createTanBitan(Vertices, Texture);
        float[] Tan = TanBitan[0];
        float[] Bitan = TanBitan[1];

        Mesh mesh = MeshLoader.createMesh(Vertices, Texture, Indices, Normals, texUnit, Tan, Bitan);

        String tex;
        String normal;
        int materialIdx = aiMesh.mMaterialIndex();
        if (materialIdx >= 0 && materialIdx < textures.size()) {
            tex = textures.get(materialIdx);
            normal = norms.get(materialIdx);
        } else {
            tex = "white.jpg";
            normal = "";
        }

        mesh.addTexture(tex, "/textures/models/source/" + modelDirName + "/textures");
        mesh.addNormal(normal, "/textures/models/source/" + modelDirName + "/textures");

        return mesh;
    }

    private static float[] arrListToArrayFloat(List<Float> list) {
        Float[] Array = new Float[list.size()];
        Array = list.toArray(Array);
        float[] array = new float[Array.length];
        for (int i = 0; i < Array.length; i++) {
            array[i] = Array[i];
        }
        return array;
    }

    private static int[] arrListToArrayInt(List<Integer> list) {
        Integer[] Array = new Integer[list.size()];
        Array = list.toArray(Array);
        int[] array = new int[Array.length];
        for (int i = 0; i < Array.length; i++) {
            array[i] = Array[i];
        }
        return array;
    }

    private static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
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

    private static void processTextCoords(AIMesh aiMesh, List<Float> texture) {
        AIVector3D.Buffer aiTextCoords = aiMesh.mTextureCoords(0);
        while (aiTextCoords.remaining() > 0) {
            AIVector3D aiTextureCoord = aiTextCoords.get();
            texture.add(aiTextureCoord.x());
            texture.add(1 - aiTextureCoord.y());
        }
    }

    private static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < aiFaces.limit(); i++) {
            AIFace face = aiFaces.get(i);
            IntBuffer Indices = face.mIndices();

            if (face.mNumIndices() != 3 || Indices == null) {
                throw new IllegalStateException();
            }
            indices.add(Indices.get(0));
            indices.add(Indices.get(1));
            indices.add(Indices.get(2));
        }


    }

}
