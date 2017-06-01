package com.xiaxl.gl_load_obj.load;

import java.util.Vector;

public class ObjModel {

    //##########################坐标############################
    // 顶点坐标
    private Vector<Float> v;
    // 顶点纹理
    private Vector<Float> vt;
    // 顶点法向量
    private Vector<Float> vn;

    //#########################index############################
    // 顶点索引
    private Vector<Short> vIndex;
    // 纹理坐标索引
    private Vector<Short> vtIndex;
    // 顶点法向量索引
    private Vector<Short> vnIndex;


    //#########################一一对应的顶点、纹理、法向量############################

    private float[] vXYZ;
    private short[] viXYZ;
    private float[] tST;
    private float[] nXYZ;


    /**
     * @param v      顶点坐标
     * @param vt     顶点纹理
     * @param vn     顶点法向量
     * @param vIndex
     */
    public ObjModel(
            //
            Vector<Float> v,
            Vector<Float> vt,
            Vector<Float> vn,
            //
            Vector<Short> vIndex,
            Vector<Short> vtIndex,
            Vector<Short> vnIndex) {
        super();
        this.v = v;
        this.vn = vn;
        this.vt = vt;
        //
        this.vIndex = vIndex;
        this.vtIndex = vtIndex;
        this.vnIndex = vnIndex;
    }

    /**
     * 一一对应的顶点、纹理、法向量
     */
    public void build() {

        //###################顶点########################
//        Vector<Float> vResult = new Vector<Float>();
//        // 循环索引列表,每一个index对应一个x,y,z坐标
//        for (int i = 0; i < vIndex.size(); i++) {
//            int index = vIndex.get(i);
//            //
//            float x = v.get(3 * index);
//            float y = v.get(3 * index + 1);
//            float z = v.get(3 * index + 2);
//            vResult.add(x);
//            vResult.add(y);
//            vResult.add(z);
//        }
//        vResult.clear();
        //#############
        //生成顶点数组
        int size = v.size();
        vXYZ = new float[size];
        for (int i = 0; i < size; i++) {
            vXYZ[i] = v.get(i);
        }



        //###################顶点索引########################
        viXYZ = new short[vIndex.size()];
        for (int i = 0; i < vIndex.size(); i++) {
            viXYZ[i] = vIndex.get(i);
        }


        //####################纹理#######################
        Vector<Float> vtResult = new Vector<Float>();
        // 循环索引列表,每一个index对应一个s,t坐标
        for (int i = 0; i < vtIndex.size(); i++) {
            int index = vtIndex.get(i);
            //
            float s = vt.get(2 * index);
            float t = vt.get(2 * index + 1);
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
        Vector<Float> vnResult = new Vector<Float>();
        // 循环索引列表,每一个index对应一个x,y,z坐标
        for (int i = 0; i < vnIndex.size(); i++) {
            int index = vnIndex.get(i);
            //
            float x = vn.get(3 * index);
            float y = vn.get(3 * index + 1);
            float z = vn.get(3 * index + 2);
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


    public Vector<Float> getV() {
        return v;
    }

    public void setV(Vector<Float> v) {
        this.v = v;
    }

    public Vector<Float> getVt() {
        return vt;
    }

    public void setVt(Vector<Float> vt) {
        this.vt = vt;
    }

    public Vector<Float> getVn() {
        return vn;
    }

    public void setVn(Vector<Float> vn) {
        this.vn = vn;
    }

    public Vector<Short> getvIndex() {
        return vIndex;
    }

    public void setvIndex(Vector<Short> vIndex) {
        this.vIndex = vIndex;
    }

    public Vector<Short> getVtIndex() {
        return vtIndex;
    }

    public void setVtIndex(Vector<Short> vtIndex) {
        this.vtIndex = vtIndex;
    }

    public Vector<Short> getVnIndex() {
        return vnIndex;
    }

    public void setVnIndex(Vector<Short> vnIndex) {
        this.vnIndex = vnIndex;
    }

    public float[] getvXYZ() {
        return vXYZ;
    }

    public void setvXYZ(float[] vXYZ) {
        this.vXYZ = vXYZ;
    }


    public short[] getViXYZ() {
        return viXYZ;
    }

    public void setViXYZ(short[] viXYZ) {
        this.viXYZ = viXYZ;
    }

    public float[] gettST() {
        return tST;
    }

    public void settST(float[] tST) {
        this.tST = tST;
    }

    public float[] getnXYZ() {
        return nXYZ;
    }

    public void setnXYZ(float[] nXYZ) {
        this.nXYZ = nXYZ;
    }
}


