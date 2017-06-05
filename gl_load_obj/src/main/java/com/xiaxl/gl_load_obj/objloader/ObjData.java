package com.xiaxl.gl_load_obj.objloader;

import java.util.ArrayList;


/**
 * 存储obj文件解析后的数据
 * <p>
 * create by xiaxl on 2017.06.01
 */
public class ObjData {
    /**
     * 解析obj文件后的原始数据
     */
    //#####坐标########
    // 顶点坐标
    private ArrayList<Float> vList;
    // 顶点纹理
    private ArrayList<Float> vtList;
    // 顶点法向量
    private ArrayList<Float> vnList;

    //#####index#######
    // 顶点索引
    private ArrayList<Short> vIndexList;
    // 纹理坐标索引
    private ArrayList<Short> vtIndexList;
    // 顶点法向量索引
    private ArrayList<Short> vnIndexList;

    /**
     * 顶点、纹理、法向量一一对应后的数据
     */
    private float[] vXYZ;
    private float[] tST;
    private float[] nXYZ;


    /**
     * @param vList       顶点坐标
     * @param vtList      顶点纹理
     * @param vnList      顶点法向量
     * @param vIndexList
     * @param vtIndexList
     * @param vnIndexList
     */
    public ObjData(
            //
            ArrayList<Float> vList,
            ArrayList<Float> vtList,
            ArrayList<Float> vnList,
            //
            ArrayList<Short> vIndexList,
            ArrayList<Short> vtIndexList,
            ArrayList<Short> vnIndexList) {
        super();
        this.vList = vList;
        this.vtList = vtList;
        this.vnList = vnList;
        //
        this.vIndexList = vIndexList;
        this.vtIndexList = vtIndexList;
        this.vnIndexList = vnIndexList;
    }

    /**
     * 一一对应的顶点、纹理、法向量
     */
    public void build() {

        //###################顶点########################
        ArrayList<Float> vResult = new ArrayList<Float>();
        // 循环索引列表,每一个index对应一个x,y,z坐标
        for (int i = 0; i < vIndexList.size(); i++) {
            int index = vIndexList.get(i);
            //
            Float x = vList.get(3 * index);
            Float y = vList.get(3 * index + 1);
            Float z = vList.get(3 * index + 2);
            vResult.add(x);
            vResult.add(y);
            vResult.add(z);
        }
        //#############
        //生成顶点数组
        int size = vResult.size();
        vXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            vXYZ[i] = vResult.get(i);
        }
        vResult.clear();


        //####################纹理#######################
        ArrayList<Float> vtResult = new ArrayList<Float>();
        // 循环索引列表,每一个index对应一个s,t坐标
        for (int i = 0; i < vtIndexList.size(); i++) {
            int index = vtIndexList.get(i);
            //
            Float s = vtList.get(2 * index);
            Float t = vtList.get(2 * index + 1);
            vtResult.add(s);
            vtResult.add(t);
        }
        //#############
        //生成纹理数组
        size = vtResult.size();
        tST = new float[size];
        for (int i = 0; i < size; i++) {
            tST[i] = vtResult.get(i);
        }
        //
        vtResult.clear();

        //###################法向量########################
        ArrayList<Float> vnResult = new ArrayList<Float>();
        // 循环索引列表,每一个index对应一个x,y,z坐标
        for (int i = 0; i < vnIndexList.size(); i++) {
            int index = vnIndexList.get(i);
            //
            Float x = vnList.get(3 * index);
            Float y = vnList.get(3 * index + 1);
            Float z = vnList.get(3 * index + 2);
            vnResult.add(x);
            vnResult.add(y);
            vnResult.add(z);
        }
        //#####
        size = vnResult.size();
        //生成法向量数组
        nXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            nXYZ[i] = vnResult.get(i);
        }
        //
        vnResult.clear();
    }

    //###############################

    public ArrayList<Float> getvList() {
        return vList;
    }

    public ArrayList<Float> getVtList() {
        return vtList;
    }

    public ArrayList<Float> getVnList() {
        return vnList;
    }

    public ArrayList<Short> getvIndexList() {
        return vIndexList;
    }

    public ArrayList<Short> getVtIndexList() {
        return vtIndexList;
    }

    public ArrayList<Short> getVnIndexList() {
        return vnIndexList;
    }

    //###############################
    public float[] getvXYZ() {
        return vXYZ;
    }

    public float[] gettST() {
        return tST;
    }

    public float[] getnXYZ() {
        return nXYZ;
    }
}


