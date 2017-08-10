package com.xiaxl.gl_load_obj.gl.spirit;


import com.xiaxl.gl_load_obj.gl.anima.SpriteAnima;
import com.xiaxl.gl_load_obj.gl.scene.LeGLBaseScene;

import java.util.ArrayList;

/**
 * 基础的精灵类
 *
 * @author xiaxl1
 */
public class LeGLAnimaSprite {
    private static final String TAG = "LeGLAnimaSprite";

    //####################上下文对象######################
    /**
     * 上下文对象
     */
    private LeGLBaseScene mBaseScene = null;

    /**
     * 构造方法
     *
     * @param scene
     */
    public LeGLAnimaSprite(LeGLBaseScene scene) {
        this.mBaseScene = scene;

    }

    /**
     * 获取上下文对象
     *
     * @return
     */
    public LeGLBaseScene getBaseScene() {
        return mBaseScene;
    }

    //###################精灵的属性值#######################

    /**
     * 精灵的属性值
     */
    // 精灵缩放大小
    private float mSpriteScale = 1;
    // 精灵的alpha数值
    private float mSpriteAlpha = 1;
    // 旋转
    private float mSpriteAngleX = 0;
    private float mSpriteAngleY = 0;
    private float mSpriteAngleZ = 0;

    public float getSpriteScale() {
        return mSpriteScale;
    }

    public void setSpriteScale(float mSpriteScale) {
        this.mSpriteScale = mSpriteScale;
    }

    public float getSpriteAlpha() {
        return mSpriteAlpha;
    }

    public void setSpriteAlpha(float mSpriteAlpha) {
        this.mSpriteAlpha = mSpriteAlpha;
    }

    public float getSpriteAngleX() {
        return mSpriteAngleX;
    }

    public void setSpriteAngleX(float mSpriteAngleX) {
        this.mSpriteAngleX = mSpriteAngleX;
    }

    public float getSpriteAngleY() {
        return mSpriteAngleY;
    }

    public void setSpriteAngleY(float mSpriteAngleY) {
        this.mSpriteAngleY = mSpriteAngleY;
    }

    public float getSpriteAngleZ() {
        return mSpriteAngleZ;
    }

    public void setSpriteAngleZ(float mSpriteAngleZ) {
        this.mSpriteAngleZ = mSpriteAngleZ;
    }

    //###################动画#######################
    //
    private ArrayList<SpriteAnima> mAnimaList = new ArrayList<SpriteAnima>();

    /**
     * 添加动画
     *
     * @param anima
     */
    public void addAnima(SpriteAnima anima) {
        if (anima != null) {
            mAnimaList.add(anima);
        }
    }

    /**
     * 开启动画
     */
    public void startAnimas() {
        for (int i = 0; i < mAnimaList.size(); i++) {
            mAnimaList.get(i).startAnima();
        }
        // 请求刷新页面
        this.getBaseScene().requestRender();
    }

    private int index = 0;

    /**
     * 绘制方法
     *
     * @param drawTime
     */
    public void drawSelf(long drawTime) {
        // ---运行动画---
        if (mAnimaList != null && mAnimaList.size() != 0) {
            for (index = mAnimaList.size() - 1; index >= 0; index--) {
                SpriteAnima anima = mAnimaList.get(index);
                // 移除运行结束的动画
                if (anima.isAnimaFinished()) {
                    mAnimaList.remove(anima);
                    continue;
                }
                // 运行动画
                anima.runAnimation(drawTime);
            }
            // 请求刷新界面
            this.getBaseScene().requestRender();
        }
    }


}
