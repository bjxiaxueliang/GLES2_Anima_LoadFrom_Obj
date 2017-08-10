package com.xiaxl.gl_load_obj.gl.anima;

import android.animation.TimeInterpolator;

import com.xiaxl.gl_load_obj.gl.spirit.LeGLAnimaSprite;

import java.lang.reflect.Method;

/**
 * 精灵属性动画
 *
 * @author xiaxl1
 */
public class SpriteAnima {
    private static final String TAG = "SpriteAnima";

    // ######################接口相关开始#######################

    SpriteAnimaListener mSpriteAnimaListener = null;

    /**
     * @param spriteAnimationListener
     */
    public void setAnimationListener(
            SpriteAnimaListener spriteAnimationListener) {
        mSpriteAnimaListener = spriteAnimationListener;
    }

    /**
     * 动画的开始与结束回调
     */
    public interface SpriteAnimaListener {
        // 动画开始
        void onAnimaStart();

        // 进度
        void onAnimaProgress(float percent);

        // 动画结束
        void onAnimaFinish();
    }

    // #################################动画属性相关##################################


    // --------接口相关结束-------

    // 动画结束
    private boolean isAnimaRuning = false;
    // 动画持续时间
    private int mAnimaDuration = 1000;
    // 动画开始时间
    private long mAnimaStartTime = 0;

    // ---动画属性---
    private float fromValue = 0;
    private float toValue = 0;
    private float currentValue = 0;

    /**
     * 动画是否已经运行结束
     *
     * @return
     */
    public boolean isAnimaFinished() {
        if (mAnimaStartTime > 0 && isAnimaRuning == false) {
            return true;
        }
        return false;
    }


    //####################################反射set方法###################################
    LeGLAnimaSprite mSprite = null;
    // 对应的setter方法
    private Method mSetterMethod = null;

    /**
     * 用来反射 sprite属性的set方法
     *
     * @param sprite
     * @param mathodName 方法名称(方法单个参数，方法参数为float类型的方法)
     */
    public void setAnimaMethod(LeGLAnimaSprite sprite, String mathodName) {
        this.mSprite = sprite;
        try {
            mSetterMethod = sprite.getClass().getMethod(mathodName, float.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更改数据
     */
    public void changeSpriteValue(float currentValue) {
        try {
            if (mSetterMethod != null && mSprite != null) {
                mSetterMethod.invoke(mSprite, currentValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // #################################运行动画##################################
    ;


    /**
     * @param fromValue 开始值
     * @param toValue   结束值
     */
    public void setAnimaValue(float fromValue, float toValue, int duration) {
        // #######################
        this.fromValue = fromValue;
        this.toValue = toValue;
        //
        this.currentValue = fromValue;
        // #######################
        this.mAnimaDuration = duration;
    }

    /**
     * 开始动画
     */
    public void startAnima() {
        mAnimaStartTime = -1;
        isAnimaRuning = true;
    }

    /**
     * 在ondraw中调用该方法.通过在ondraw中不断循环调用
     *
     * @param drawTime
     */
    public void runAnimation(long drawTime) {
        if (!isAnimaRuning) {
            return;
        }
        //
        if (mAnimaStartTime == -1) {
            mAnimaStartTime = drawTime;
            // 回调接口,动画开始
            if (mSpriteAnimaListener != null) {
                mSpriteAnimaListener.onAnimaStart();
            }
        }
        // 计算 时间差
        long runTime = drawTime - mAnimaStartTime;
        // 计算 进度
        float percent = (float) runTime / mAnimaDuration;
        if (percent > 1) {
            // 动画结束
            percent = 1;
            // 更改动画帧数据
            changeFrameData(percent);
            // 动画结束
            isAnimaRuning = false;
            // 回调接口,动画结束
            if (mSpriteAnimaListener != null) {
                mSpriteAnimaListener.onAnimaFinish();
            }
            return;
        }
        // 运行动画
        changeFrameData(percent);
    }

    /**
     * 更改动画帧数据
     *
     * @param percent
     */
    private void changeFrameData(float percent) {
        // 回调进度
        if (mSpriteAnimaListener != null) {
            mSpriteAnimaListener.onAnimaProgress(percent);
        }
        // 计算 动画帧
        if (isAnimaRuning) {
            // 动画差值器
            if (mTimeInterpolator != null) {
                percent = mTimeInterpolator.getInterpolation(percent);
            }

            // 计算 动画帧
            currentValue = fromValue + percent * (toValue - fromValue);
            // 更改精灵属性值
            changeSpriteValue(currentValue);
        }
        //
    }


    // #################################动画差值器##################################
    // The time interpolator to be used if none is set on the animation
    private TimeInterpolator mTimeInterpolator = null;


    public void setInterpolator(TimeInterpolator value) {
        mTimeInterpolator = value;
    }


}
