package com.xiaxl.gl_load_obj.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xiaxl.gl_load_obj.R;
import com.xiaxl.gl_load_obj.gl.scene.LeGLBaseScene;
import com.xiaxl.gl_load_obj.gl.spirit.LeGLObjSprite;
import com.xiaxl.gl_load_obj.gl.utils.MatrixState;
import com.xiaxl.gl_load_obj.gl.utils.TextureUtil;
import com.xiaxl.gl_load_obj.objloader2.ObjLoaderUtil;

import java.util.ArrayList;

/*
 * GL SurfaceView
 */
public class MyGLScene extends LeGLBaseScene {

    private static final String TAG = "MyGLScene";

    public MyGLScene(Context context) {
        super(context);

        // 初始化render
        initRender();

    }

    public MyGLScene(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化render
        initRender();

    }

    public MyGLScene(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        // 初始化render
        initRender();

    }

    public void initRender() {
        // 初始化render
        MyGLSceneRenderer render = new MyGLSceneRenderer(this);
        this.setRenderer(render);
        // 渲染模式(被动渲染)
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //
        this.setSceneWidthAndHeight(this.getMeasuredWidth(),
                this.getMeasuredHeight());
    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);
    }

    @Override
    public void drawSelf(long drawTime) {
        super.drawSelf(drawTime);

        if (isInintFinsh == false) {
            //
            initUI();
            //
            initTexture();
            //
            isInintFinsh = true;
        }

        /**
         * 绘制地球
         */
        MatrixState.pushMatrix();
        MatrixState.translate(0, 0, -20);
        //
        for (int i = 0; i < mObjSprites.size(); i++) {
            LeGLObjSprite sprite = mObjSprites.get(i);
            sprite.drawSelf(textureGoldId, drawTime);
        }

        MatrixState.popMatrix();

    }

    /**
     * 数据
     */
    // 是否初始化
    private boolean isInintFinsh = false;
    // 宽
    private float mSceneWidth = 720;
    // 高
    private float mSceneHeight = 1280;

    /**
     * 纹理id
     */
    // ---纹理id开始---
    // 系统分配的纹理id
    private int textureGoldId = 0;

    /**
     * UI
     */


    private ArrayList<LeGLObjSprite> mObjSprites = new ArrayList<LeGLObjSprite>();

    /**
     * 初始化场景中的精灵实体类
     */
    private void initUI() {

        /**
         * ----勋章---
         */
        try {
            ArrayList<ObjLoaderUtil.ObjData> mObjList = ObjLoaderUtil.load("multiobjects.obj", this.getResources());
            if (mObjList != null) {
                for (int i = 0; i < mObjList.size(); i++) {
                    ObjLoaderUtil.ObjData data = mObjList.get(i);
                    mObjSprites.add(new LeGLObjSprite(this, data.aVertices, data.aNormals, data.aTexCoords, data.mtlData != null ? data.mtlData.Kd_Color : 0xffffffff));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化纹理
     */
    private void initTexture() {
        // 两球之间连线的纹理图片
        textureGoldId = TextureUtil.getTextureIdByDrawableId(
                this.getContext(), R.drawable.banana);
    }

    public float getSceneWidth() {
        return mSceneWidth;
    }

    public float getSceneHeight() {
        return mSceneHeight;
    }

    public void setSceneWidthAndHeight(float mSceneWidth, float mSceneHeight) {
        this.mSceneWidth = mSceneWidth;
        this.mSceneHeight = mSceneHeight;
    }


    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;//角度缩放比例
    private float mPreviousY;//上次的触控位置Y坐标
    private float mPreviousX;//上次的触控位置X坐标

    //触摸事件回调方法
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//计算触控笔Y位移
                float dx = x - mPreviousX;//计算触控笔X位移
                //
                for (int i = 0; i < mObjSprites.size(); i++) {
                    LeGLObjSprite sprite = mObjSprites.get(i);
                    float yAngle = sprite.getSpriteAngleY();
                    sprite.setSpriteAngleY(yAngle += dx * TOUCH_SCALE_FACTOR);
                }

                this.requestRender();//重绘画面
        }
        mPreviousY = y;//记录触控笔位置
        mPreviousX = x;//记录触控笔位置
        return true;
    }

    //##################################################

    /**
     *
     */
    public void startXZAnima() {
        //###################旋转动画###################
//        // 创建动画
//        SpriteAnima rotateAnima = new SpriteAnima();
//        // 动画更改的方法
//        rotateAnima.setAnimaMethod(mLeGLObjSpirit, "setSpriteAngleY");
//        // 从0到360度
//        rotateAnima.setAnimaValue(0, 360, 2100);
//        // 添加动画差值器(超过，再回来)
//        rotateAnima.setInterpolator(new OvershootInterpolator());
//
//        //###################缩放动画###################
//        SpriteAnima scaleAnima = new SpriteAnima();
//        scaleAnima.setAnimaMethod(mLeGLObjSpirit, "setSpriteScale");
//        scaleAnima.setAnimaValue(0.1f, 1.0f, 700);
//
//        //###################
//        // 添加动画
//        mLeGLObjSpirit.addAnima(rotateAnima);
//        mLeGLObjSpirit.addAnima(scaleAnima);
//        //##################
//        // 开启动画
//        mLeGLObjSpirit.startAnimas();
    }


}