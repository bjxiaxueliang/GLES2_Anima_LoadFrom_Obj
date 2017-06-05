package com.xiaxl.gl_load_obj.gl;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.xiaxl.gl_load_obj.gl.config.LeGLConfig;
import com.xiaxl.gl_load_obj.gl.scene.LeGLBaseScene;
import com.xiaxl.gl_load_obj.gl.utils.MatrixState;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * 场景的渲染器
 * 
 * @author xiaxl1
 *
 */
public class MyGLSceneRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = "MyGLSceneRenderer";

	public LeGLBaseScene mGLSurfaceView;

	public MyGLSceneRenderer(LeGLBaseScene glSurfaceView) {
		this.mGLSurfaceView = glSurfaceView;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO GlThread
		//
		long drawTime = System.currentTimeMillis();
		// 清除深度缓冲与颜色缓冲
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
		//
        //开启混合
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc( GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA );
		//
		mGLSurfaceView.drawSelf(drawTime);

	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO GlThread
		// 设置屏幕背景色RGBA
		//GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		// 启用深度测试
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		// 设置为打开背面剪裁
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		// 初始化变换矩阵
		MatrixState.setInitStack();
		//初始化定位光光源
		MatrixState.setLightLocation(40, 10, 20);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// TODO GlThread
		// viewPort
		GLES20.glViewport(0, 0, width, height);
		//
		float ratio = (float) width / height;
		// 平行投影
//		MatrixState.setProjectOrtho(-ratio, ratio, -1, 1,
//				LeGLConfig.PROJECTION_NEAR, LeGLConfig.PROJECTION_FAR);
		MatrixState.setProjectFrustum(-ratio, ratio, -1, 1,
				LeGLConfig.PROJECTION_NEAR, LeGLConfig.PROJECTION_FAR);
		// camera
		MatrixState.setCamera(LeGLConfig.EYE_X, LeGLConfig.EYE_Y, LeGLConfig.EYE_Z,
				LeGLConfig.VIEW_CENTER_X, LeGLConfig.VIEW_CENTER_Y, LeGLConfig.VIEW_CENTER_Z,
				0f, 1f, 0f);
	}

}
