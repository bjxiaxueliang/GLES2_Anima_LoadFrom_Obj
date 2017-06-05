package com.xiaxl.gl_load_obj;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.xiaxl.gl_load_obj.gl.MyGLScene;

public class MainActivity extends Activity {
    private MyGLScene mGLSurfaceView;
    //
    private Button mBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        setContentView(R.layout.main_activity);
        //
        initUI();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
    }

    private void initUI() {
        //初始化GLSurfaceView
        mGLSurfaceView = (MyGLScene) findViewById(R.id.glscene);
        mGLSurfaceView.requestFocus();//获取焦点
        mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控
        //
        mBtn = (Button) findViewById(R.id.button);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGLSurfaceView.startXZAnima();
            }
        });
    }

}



