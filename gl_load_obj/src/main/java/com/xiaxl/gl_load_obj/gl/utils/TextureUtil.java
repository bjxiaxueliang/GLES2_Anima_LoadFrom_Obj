package com.xiaxl.gl_load_obj.gl.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

public class TextureUtil {

	/**
	 * 
	 * @param context
	 * @param drawableId
	 * @return
	 */
	public static int getTextureIdByDrawableId(Context context, int drawableId)// textureId
	{
		//生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures
				(
						1,          //产生的纹理id的数量
						textures,   //纹理id的数组
						0           //偏移量
				);
		int textureId = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		//通过输入流加载图片===============begin===================
		InputStream is = context.getResources().openRawResource(drawableId);
		Bitmap bitmapTmp;
		try {
			bitmapTmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		//通过输入流加载图片===============end=====================
		GLUtils.texImage2D
				(
						GLES20.GL_TEXTURE_2D, //纹理类型
						0,
						GLUtils.getInternalFormat(bitmapTmp),
						bitmapTmp, //纹理图像
						GLUtils.getType(bitmapTmp),
						0 //纹理边框尺寸
				);
		bitmapTmp.recycle();          //纹理加载成功后释放图片
		return textureId;
	}

	
	
	/**
	 * 
	 * @param context
	 * @param bmp
	 * @return
	 */
	public static int getTextureIdByDrawableId(Context context, Bitmap bmp)// textureId
	{
		// 生成纹理ID
		int[] textures = new int[1];
		GLES20.glGenTextures(1, // 产生的纹理id的数量
				textures, // 纹理id的数组
				0 // 偏移量
		);
		int textureId = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);
		
		// 实际加载纹理
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, // 纹理类型，在OpenGL
													// ES中必须为GL10.GL_TEXTURE_2D
				0, // 纹理的层次，0表示基本图像层，可以理解为直接贴图
				bmp, // 纹理图像
				0 // 纹理边框尺寸
		);
		bmp.recycle(); // 纹理加载成功后释放图片

		return textureId;
	}
	
}
