package com.xiaxl.gl_load_obj.objloader2;

import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * obj model
 */
public class LoaderObjUtil {

    private static final String TAG = "LoaderObjUtil";


    /**
     * 解析
     *
     * @param fname assets的obj文件路径
     * @param res   Resources
     * @return
     */
    public static ArrayList<ObjData> load(String fname, Resources res) throws Exception {
        Log.d(TAG, "---loadObj---");
        // 返回的数据列表
        ArrayList<ObjData> objectList = new ArrayList<ObjData>();
        //
        if (res == null || TextUtils.isEmpty(fname)) {
            return objectList;
        }

        /**
         * 所有顶点信息
         */
        // 顶点数据
        ArrayList<Float> vertices = new ArrayList<Float>();
        // 纹理数据
        ArrayList<Float> texCoords = new ArrayList<Float>();
        // 法向量数据
        ArrayList<Float> normals = new ArrayList<Float>();
        // 全部材质列表
        HashMap<String, LoaderMtlUtil.MtlData> mtlMap = null;

        // Ojb索引数据
        ObjData currObjData = new ObjData();
        // 当前材质名称
        String currMaterialName = null;
        // 是否有面数据的标识
        boolean currObjHasFaces = false;

        //###############################解析开始#################################
        try {
            // 每一行的信息
            String line = null;
            // 读取assets下文件
            InputStream in = res.getAssets().open(fname);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader buffer = new BufferedReader(isr);

            // 循环读取每一行的数据
            while ((line = buffer.readLine()) != null) {
                // 忽略 空行和注释
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }
                // 以空格分割String
                StringTokenizer parts = new StringTokenizer(line, " ");
                int numTokens = parts.countTokens();
                if (numTokens == 0) {
                    continue;
                }
                // 打头的字符
                String type = parts.nextToken();

                // 材质
                if (type.equals(LoaderObjUtil.MTLLIB)) {
                    if (!parts.hasMoreTokens()) {
                        continue;
                    }
                    // 需要重命名材质文件(应该取同一路径下的,这里暂时先这么写吧)??????????????????????????????
                    String materialLibPath = parts.nextToken();
                    //#########加载材质信息############
                    if (TextUtils.isEmpty(materialLibPath) == false) {
                        // obj同一路径下的mtl文件
                        File materialFile = new File(new File(fname).getParent() + File.separatorChar + materialLibPath);
                        mtlMap = LoaderMtlUtil.load(materialFile.getAbsolutePath(), res);
                    }

                }
                // 对象名称
                else if (type.equals(LoaderObjUtil.O)) {
                    // 对象名称
                    String objName = parts.hasMoreTokens() ? parts.nextToken() : "def";
                    // 面数据
                    if (currObjHasFaces) {
                        // 添加到数组中
                        objectList.add(currObjData);
                        // 创建新的索引对象
                        currObjData = new ObjData();
                        currObjHasFaces = false;
                    }
                    currObjData.name = objName;
                    // 对应材质
                    if (TextUtils.isEmpty(currMaterialName) == false && mtlMap != null) {
                        currObjData.mtlData = mtlMap.get(currMaterialName);
                    }
                }
                // "v" 顶点属性 添加到顶点数组
                else if (type.equals(LoaderObjUtil.V)) {
                    vertices.add(Float.parseFloat(parts.nextToken()));
                    vertices.add(Float.parseFloat(parts.nextToken()));
                    vertices.add(Float.parseFloat(parts.nextToken()));
                }
                // 纹理
                else if (type.equals(LoaderObjUtil.VT)) {
                    // 这里纹理的Y值，需要(Y = 1-Y0)
                    texCoords.add(Float.parseFloat(parts.nextToken()));
                    texCoords.add(1f - Float.parseFloat(parts.nextToken()));
                }
                // 法向量
                else if (type.equals(LoaderObjUtil.VN)) {
                    normals.add(Float.parseFloat(parts.nextToken()));
                    normals.add(Float.parseFloat(parts.nextToken()));
                    normals.add(Float.parseFloat(parts.nextToken()));
                }
                // 使用材质
                else if (type.equals(LoaderObjUtil.USEMTL)) {
                    // 材质名称
                    currMaterialName = parts.nextToken();
                    if (currObjHasFaces) {
                        // 添加到数组中
                        objectList.add(currObjData);
                        // 创建一个index对象
                        currObjData = new ObjData();
                        currObjHasFaces = false;
                    }
                    // 材质名称
                    if (TextUtils.isEmpty(currMaterialName) == false && mtlMap != null) {
                        currObjData.mtlData = mtlMap.get(currMaterialName);
                    }
                }
                // "f"面属性  索引数组
                else if (type.equals(F)) {
                    // 当前obj对象有面数据
                    currObjHasFaces = true;
                    // 是否为矩形(android 均为三角形，这里暂时先忽略多边形的情况)
                    boolean isQuad = numTokens == 5;
                    int[] quadvids = new int[4];
                    int[] quadtids = new int[4];
                    int[] quadnids = new int[4];

                    // 如果含有"//" 替换
                    boolean emptyVt = line.indexOf("//") > -1;
                    if (emptyVt) {
                        line = line.replace("//", "/");
                    }
                    // "f 103/1/1 104/2/1 113/3/1"以" "分割
                    parts = new StringTokenizer(line);
                    // “f”
                    parts.nextToken();
                    // "103/1/1 104/2/1 113/3/1"再以"/"分割
                    StringTokenizer subParts = new StringTokenizer(parts.nextToken(), "/");
                    int partLength = subParts.countTokens();

                    // 纹理数据
                    boolean hasuv = partLength >= 2 && !emptyVt;
                    // 法向量数据
                    boolean hasn = partLength == 3 || (partLength == 2 && emptyVt);
                    // 索引index
                    int idx;
                    for (int i = 1; i < numTokens; i++) {
                        if (i > 1) {
                            subParts = new StringTokenizer(parts.nextToken(), "/");
                        }
                        // 顶点索引
                        idx = Integer.parseInt(subParts.nextToken());
                        if (idx < 0) {
                            idx = (vertices.size() / 3) + idx;
                        } else {
                            idx -= 1;
                        }
                        if (!isQuad) {
                            currObjData.vertexIndices.add(idx);
                        } else {
                            quadvids[i - 1] = idx;
                        }
                        // 纹理索引
                        if (hasuv) {
                            idx = Integer.parseInt(subParts.nextToken());
                            if (idx < 0) {
                                idx = (texCoords.size() / 2) + idx;
                            } else {
                                idx -= 1;
                            }
                            if (!isQuad) {
                                currObjData.texCoordIndices.add(idx);
                            } else {
                                quadtids[i - 1] = idx;
                            }
                        }
                        // 法向量数据
                        if (hasn) {
                            idx = Integer.parseInt(subParts.nextToken());
                            if (idx < 0) {
                                idx = (normals.size() / 3) + idx;
                            } else {
                                idx -= 1;
                            }
                            if (!isQuad) {
                                currObjData.normalIndices.add(idx);
                            } else {
                                quadnids[i - 1] = idx;
                            }
                        }
                    }
                    // 如果是多边形
                    if (isQuad) {
                        int[] indices = new int[]{0, 1, 2, 0, 2, 3};
                        for (int i = 0; i < 6; ++i) {
                            int index = indices[i];
                            currObjData.vertexIndices.add(quadvids[index]);
                            currObjData.texCoordIndices.add(quadtids[index]);
                            currObjData.normalIndices.add(quadnids[index]);
                        }
                    }
                }
            }
            //
            buffer.close();
            // 存在索引面数据，添加到index列表中
            if (currObjHasFaces) {
                // 添加到数组中
                objectList.add(currObjData);
            }
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e.getCause());
        }

        //###############################顶点、法向量、纹理一一对应#################################

        // 循环索引对象列表
        int numObjects = objectList.size();
        for (int j = 0; j < numObjects; ++j) {
            ObjData objData = objectList.get(j);

            int i;
            // 顶点数据 初始化
            float[] aVertices = new float[objData.vertexIndices.size() * 3];
            // 顶点纹理数据 初始化
            float[] aTexCoords = new float[objData.texCoordIndices.size() * 2];
            // 顶点法向量数据 初始化
            float[] aNormals = new float[objData.normalIndices.size() * 3];
            // 按照索引，重新组织顶点数据
            for (i = 0; i < objData.vertexIndices.size(); ++i) {
                // 顶点索引，三个一组做为一个三角形
                int faceIndex = objData.vertexIndices.get(i) * 3;
                int vertexIndex = i * 3;
                try {
                    // 按照索引，重新组织顶点数据
                    aVertices[vertexIndex] = vertices.get(faceIndex);
                    aVertices[vertexIndex + 1] = vertices.get(faceIndex + 1);
                    aVertices[vertexIndex + 2] = vertices.get(faceIndex + 2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 按照索引组织 纹理数据
            if (texCoords != null && texCoords.size() > 0) {
                for (i = 0; i < objData.texCoordIndices.size(); ++i) {
                    int texCoordIndex = objData.texCoordIndices.get(i) * 2;
                    int ti = i * 2;
                    aTexCoords[ti] = texCoords.get(texCoordIndex);
                    aTexCoords[ti + 1] = texCoords.get(texCoordIndex + 1);
                }
            }
            // 按照索引组织 法向量数据
            for (i = 0; i < objData.normalIndices.size(); ++i) {
                int normalIndex = objData.normalIndices.get(i) * 3;
                int ni = i * 3;
                if (normals.size() == 0) {
                    throw new Exception("There are no normals specified for this model. Please re-export with normals.");
                }
                aNormals[ni] = normals.get(normalIndex);
                aNormals[ni + 1] = normals.get(normalIndex + 1);
                aNormals[ni + 2] = normals.get(normalIndex + 2);
            }
            // 数据设置到oid.targetObj中
            objData.aVertices = aVertices;
            objData.aTexCoords = aTexCoords;
            objData.aNormals = aNormals;
            //
            if (objData.vertexIndices != null) {
                objData.vertexIndices.clear();
            }
            if (objData.texCoordIndices != null) {
                objData.texCoordIndices.clear();
            }
            if (objData.normalIndices != null) {
                objData.normalIndices.clear();
            }
        }
        return objectList;
    }

    //###################################################################################
    /**
     * obj需解析字段
     */
    // obj对应的材质文件
    private static final String MTLLIB = "mtllib";
    // 组名称
    private static final String G = "g";
    // o 对象名称(Object name)
    private static final String O = "o";
    // 顶点
    private static final String V = "v";
    // 纹理坐标
    private static final String VT = "vt";
    // 顶点法线
    private static final String VN = "vn";
    // 使用的材质
    private static final String USEMTL = "usemtl";
    // v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3(索引起始于1)
    private static final String F = "f";


    //###################################################################################

    public static class ObjData {

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
}


