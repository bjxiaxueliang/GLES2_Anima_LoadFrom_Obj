package com.xiaxl.gl_load_obj.objloader;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 解析 assets下的obj文件
 * <p>
 * create by xiaxl on 2017.06.01
 */
public class ObjParser {

    private static final String TAG = "ObjParser";


    /**
     * 解析 assets下的obj文件
     *
     * @param fname assets的obj文件名称
     * @param res   Resources
     * @return
     */
    public static ObjData loadObj(String fname, Resources res) {
        Log.d(TAG, "---loadObj---");

        if (res == null || TextUtils.isEmpty(fname)) {
            return null;
        }

        //##########坐标##########
        // 顶点坐标
        ArrayList<Float> vList = new ArrayList<Float>();
        // 顶点纹理
        ArrayList<Float> vtList = new ArrayList<Float>();
        // 顶点法向量
        ArrayList<Float> vnList = new ArrayList<Float>();

        //#########index##########
        // 顶点索引
        ArrayList<Short> vIndexList = new ArrayList<Short>();
        // 纹理坐标索引
        ArrayList<Short> vtIndexList = new ArrayList<Short>();
        // 顶点法向量索引
        ArrayList<Short> vnIndexList = new ArrayList<Short>();

        //#########loadObj begin##########
        long begin = System.currentTimeMillis();

        try {
            // 读取assets下文件
            InputStream in = res.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            // 每一行的信息
            String temps = null;
            while ((temps = br.readLine()) != null) {
                // 空格分割 各个部分
                String[] tempsa = temps.split("[ ]+");
                // 顶点坐标
                if (tempsa[0].trim().equals("v")) {
                    //若为顶点坐标行则提取出此顶点的XYZ坐标添加到原始顶点坐标列表中
                    vList.add(Float.parseFloat(tempsa[1]));
                    vList.add(Float.parseFloat(tempsa[2]));
                    vList.add(Float.parseFloat(tempsa[3]));
                }
                // 纹理坐标
                else if (tempsa[0].trim().equals("vt")) {//此行为纹理坐标行
                    //若为纹理坐标行则提取ST坐标并添加进原始纹理坐标列表中
                    vtList.add(Float.parseFloat(tempsa[1]));
                    vtList.add(Float.parseFloat(tempsa[2]));
                }
                // 法向量
                else if (tempsa[0].trim().equals("vn")) {
                    vnList.add(Float.parseFloat(tempsa[1]));
                    vnList.add(Float.parseFloat(tempsa[2]));
                    vnList.add(Float.parseFloat(tempsa[3]));
                }
                // 三角形面
                else if (tempsa[0].trim().equals("f")) {
                    // f 的结构 v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
                    String[] f1 = tempsa[1].split("/");
                    String[] f2 = tempsa[2].split("/");
                    String[] f3 = tempsa[3].split("/");
                    //######顶点index#########
                    Short vIndex1 = Short.valueOf(f1[0]);
                    vIndex1--;
                    Short vIndex2 = Short.valueOf(f2[0]);
                    vIndex2 --;
                    Short vIndex3 = Short.valueOf(f3[0]);
                    vIndex3--;
                    // 顶点index
                    vIndexList.add(vIndex1);
                    vIndexList.add(vIndex2);
                    vIndexList.add(vIndex3);
                    // #######顶点纹理index#######
                    Short vtIndex1 = Short.valueOf(f1[1]);
                    vtIndex1--;
                    Short vtIndex2 = Short.valueOf(f2[1]);
                    vtIndex2 --;
                    Short vtIndex3 = Short.valueOf(f3[1]);
                    vtIndex3--;
                    // 顶点纹理index
                    vtIndexList.add(vtIndex1);
                    vtIndexList.add(vtIndex2);
                    vtIndexList.add(vtIndex3);
                    // #######顶点法向量index#######
                    Short vnIndex1 = Short.valueOf(f1[2]);
                    vnIndex1--;
                    Short vnIndex2 = Short.valueOf(f2[2]);
                    vnIndex2--;
                    Short vnIndex3 = Short.valueOf(f3[2]);
                    vnIndex3--;
                    // 顶点纹理index
                    vnIndexList.add(vnIndex1);
                    vnIndexList.add(vnIndex2);
                    vnIndexList.add(vnIndex3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        long end1 = System.currentTimeMillis();

        Log.d(TAG, "load time: " + (end1 - begin));
        //
        ObjData objModel = new ObjData(vList, vtList, vnList, vIndexList, vtIndexList, vnIndexList);
        objModel.build();
        //
        long end2 = System.currentTimeMillis();

        Log.d(TAG, "load and build time: " + (end2 - begin));

        return objModel;
    }
}
