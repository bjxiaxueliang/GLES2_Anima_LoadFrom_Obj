package com.xiaxl.gl_load_obj.objloader2;

import java.util.ArrayList;

public class ObjData {

    // 对象名称
    public String name;
    // 材质
    public LoaderMtlUtil.MtlData mtlData;

    /**
     * 顶点、纹理、法向量一一对应后的数据
     */
    public float[] aVertices;
    public float[] aTexCoords;
    public float[] aNormals;

    /**
     * index数组(顶点、纹理、法向量一一对应后，以下三个列表会清空)
     */
    // 顶点index数组
    public ArrayList<Integer> vertexIndices;
    // 纹理index数组
    public ArrayList<Integer> texCoordIndices;
    // 法向量index数组
    public ArrayList<Integer> normalIndices;

}


