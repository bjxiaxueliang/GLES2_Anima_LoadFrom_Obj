package com.xiaxl.gl_load_obj.gl.spirit;

import android.graphics.Bitmap;

import com.xiaxl.gl_load_obj.gl.scene.LeGLBaseScene;
import com.xiaxl.gl_load_obj.gl.utils.BitmapUtil;
import com.xiaxl.gl_load_obj.gl.utils.MatrixState;
import com.xiaxl.gl_load_obj.objloader.ObjLoaderUtil;

import java.util.ArrayList;

/**
 * @author xiaxveliang
 */
public class LeGLObjSpriteGroup extends LeGLAnimaSprite {
    private static final String TAG = LeGLObjSpriteGroup.class.getSimpleName();


    private ArrayList<LeGLObjSpirit> mObjSprites = new ArrayList<LeGLObjSpirit>();

    public LeGLObjSpriteGroup(LeGLBaseScene scene, ArrayList<ObjLoaderUtil.ObjData> objDatas) {
        super(scene);
        //
        initObjs(objDatas);
    }

    private void initObjs(ArrayList<ObjLoaderUtil.ObjData> objDatas) {
        mObjSprites.clear();
        if (objDatas != null) {
            for (int i = 0; i < objDatas.size(); i++) {
                ObjLoaderUtil.ObjData data = objDatas.get(i);
                //
                int diffuseColor = data.mtlData != null ? data.mtlData.Kd_Color : 0xffffffff;
                float alpha = data.mtlData != null ? data.mtlData.alpha : 1.0f;
                String texturePath = data.mtlData != null ? data.mtlData.Kd_Texture : "";
                Bitmap bmp = BitmapUtil.getBitmapFromAsset(getBaseScene().getContext(), texturePath);
                mObjSprites.add(new LeGLObjSpirit(getBaseScene(), data.aVertices, data.aNormals, data.aTexCoords, diffuseColor, alpha, bmp));
            }
        }
    }


    @Override
    public void drawSelf(long drawTime) {
        super.drawSelf(drawTime);

        MatrixState.pushMatrix();

        // 缩放
        MatrixState.scale(this.getSpriteScale(),
                this.getSpriteScale(), this.getSpriteScale());
        // 旋转
        //MatrixState.rotate(this.getSpriteAngleX(), 1, 0, 0);
        // 旋转
        MatrixState.rotate(this.getSpriteAngleY(), 0, 1, 0);
        // 绘制
        for (int i = 0; i < mObjSprites.size(); i++) {
            LeGLObjSpirit sprite = mObjSprites.get(i);
            sprite.drawSelf(drawTime);
        }

        MatrixState.popMatrix();

    }

}