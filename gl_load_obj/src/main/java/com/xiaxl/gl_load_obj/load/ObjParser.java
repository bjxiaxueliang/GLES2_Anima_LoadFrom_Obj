package com.xiaxl.gl_load_obj.load;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * 解析obj文件
 */
public class ObjParser {

    private static final String TAG = "ObjParser";


    Context context;


    //#########################坐标############################
    // 顶点坐标
    Vector<Float> v = new Vector<Float>();
    // 顶点纹理坐标
    Vector<Float> vt = new Vector<Float>();
    // 顶点法向量
    Vector<Float> vn = new Vector<Float>();

    //#########################index############################
    // 顶点索引
    Vector<Short> vIndex = new Vector<Short>();
    // 纹理坐标索引
    Vector<Short> vtIndex = new Vector<Short>();
    // 顶点法向量索引
    Vector<Short> vnIndex = new Vector<Short>();


    /**
     * 传入上下文对象
     *
     * @param ctx
     */
    public ObjParser(Context ctx) {
        context = ctx;
    }

    /**
     * @param fileName 文件路径
     * @return
     */
    public ObjModel parseOBJ(String fileName) {
        BufferedReader reader = null;
        String line = null;

        try { //try to open file
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
        } catch (IOException e) {
        }
        try {//try to read lines of the file
            while ((line = reader.readLine()) != null) {
                Log.v("obj", line);
                if (line.startsWith("f")) {//a polygonal face
                    processFLine(line);
                } else if (line.startsWith("vn")) {
                    processVNLine(line);
                } else if (line.startsWith("vt")) {
                    processVTLine(line);
                } else if (line.startsWith("v")) { //line having geometric position of single vertex
                    processVLine(line);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        //
        ObjModel objModel = new ObjModel(v, vt, vn, vIndex, vtIndex, vnIndex);
        objModel.build();
        return objModel;
    }


    private void processVLine(String line) {
        String[] tokens = line.split("[ ]+"); //split the line at the spaces
        int c = tokens.length;
        for (int i = 1; i < c; i++) { //add the vertex to the vertex array
            v.add(Float.valueOf(tokens[i]));
        }
    }

    private void processVNLine(String line) {
        String[] tokens = line.split("[ ]+"); //split the line at the spaces
        int c = tokens.length;
        for (int i = 1; i < c; i++) { //add the vertex to the vertex array
            vn.add(Float.valueOf(tokens[i]));
        }
    }

    private void processVTLine(String line) {
        String[] tokens = line.split("[ ]+"); //split the line at the spaces
        int c = tokens.length;
        for (int i = 1; i < c; i++) { //add the vertex to the vertex array
            vt.add(Float.valueOf(tokens[i]));
        }
    }

    private void processFLine(String line) {
        String[] tokens = line.split("[ ]+");
        int c = tokens.length;

        if (tokens[1].matches("[0-9]+")) {//f: v
            if (c == 4) {//3 faces
                for (int i = 1; i < c; i++) {
                    Short s = Short.valueOf(tokens[i]);
                    s--;
                    vIndex.add(s);
                }
            }
        }
        if (tokens[1].matches("[0-9]+/[0-9]+")) {//if: v/vt
            if (c == 4) {//3 faces
                for (int i = 1; i < c; i++) {
                    Short s = Short.valueOf(tokens[i].split("/")[0]);
                    s--;
                    vIndex.add(s);
                    s = Short.valueOf(tokens[i].split("/")[1]);
                    s--;
                    vtIndex.add(s);
                }
            }
        }
        if (tokens[1].matches("[0-9]+//[0-9]+")) {//f: v//vn
            if (c == 4) {//3 faces
                for (int i = 1; i < c; i++) {
                    Short s = Short.valueOf(tokens[i].split("//")[0]);
                    s--;
                    vIndex.add(s);
                    s = Short.valueOf(tokens[i].split("//")[1]);
                    s--;
                    vnIndex.add(s);
                }
            }
        }
        if (tokens[1].matches("[0-9]+/[0-9]+/[0-9]+")) {//f: v/vt/vn

            if (c == 4) {//3 faces
                for (int i = 1; i < c; i++) {
                    Short s = Short.valueOf(tokens[i].split("/")[0]);
                    s--;
                    vIndex.add(s);
                    s = Short.valueOf(tokens[i].split("/")[1]);
                    s--;
                    vtIndex.add(s);
                    s = Short.valueOf(tokens[i].split("/")[2]);
                    s--;
                    vnIndex.add(s);
                }
            }
        }
    }

}

