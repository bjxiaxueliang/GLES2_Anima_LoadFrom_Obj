package com.xiaxl.gl_load_obj.gl.spiritgroup;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.xiaxl.gl_load_obj.gl.scene.LeGLBaseScene;
import com.xiaxl.gl_load_obj.gl.spirit.LeGLBaseSpirit;
import com.xiaxl.gl_load_obj.gl.spirit.LeGLObjColorSpirit;
import com.xiaxl.gl_load_obj.gl.spirit.LeGLObjTextureSpirit;
import com.xiaxl.gl_load_obj.gl.utils.BitmapUtil;
import com.xiaxl.gl_load_obj.gl.utils.MatrixState;
import com.xiaxl.gl_load_obj.objloader.ObjLoaderUtil;

import java.util.ArrayList;

/**
 * @author xiaxveliang
 */
public class LeGLObjSpriteGroup extends LeGLAnimaSprite {
    private static final String TAG = LeGLObjSpriteGroup.class.getSimpleName();


    private ArrayList<LeGLBaseSpirit> mObjSprites = new ArrayList<LeGLBaseSpirit>();

    public LeGLObjSpriteGroup(LeGLBaseScene scene, ArrayList<ObjLoaderUtil.ObjData> objDatas) {
        super(scene);
        //
        initObjs(objDatas);
    }

    private void initObjs(ArrayList<ObjLoaderUtil.ObjData> objDatas) {
        mObjSprites.clear();
        if (objDatas != null) {
            for (int i = 0; i < objDatas.size(); i++) {

                Log.e("xiaxl: ", "i: " + i);

                ObjLoaderUtil.ObjData data = objDatas.get(i);
                //
                int diffuseColor = data.mtlData != null ? data.mtlData.Kd_Color : 0xffffffff;
                float alpha = data.mtlData != null ? data.mtlData.alpha : 1.0f;
                String texturePath = data.mtlData != null ? data.mtlData.Kd_Texture : "";

                // 构造对象
                if (data.aTexCoords != null && data.aTexCoords.length != 0 && TextUtils.isEmpty(texturePath) == false) {
                    Log.e("xiaxl: ", "texture");

                    Bitmap bmp = BitmapUtil.getBitmapFromAsset(getBaseScene().getContext(), texturePath);
                    LeGLBaseSpirit spirit = new LeGLObjTextureSpirit(getBaseScene(), data.aVertices, data.aNormals, data.aTexCoords, alpha, bmp);
                    mObjSprites.add(spirit);
                } else {

                    Log.e("xiaxl: ", "color");

                    LeGLBaseSpirit spirit = new LeGLObjColorSpirit(getBaseScene(), data.aVertices, data.aNormals, diffuseColor, alpha);
                    mObjSprites.add(spirit);
                }
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
            LeGLBaseSpirit sprite = mObjSprites.get(i);
            sprite.drawSelf(drawTime);
        }

        MatrixState.popMatrix();

    }

}