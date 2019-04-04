package com.stomhong.camera;

import android.view.SurfaceHolder;

public class CameraEngine implements SurfaceHolder.Callback {
    ICamera mICamera;

    public CameraEngine() {
        mICamera = new CameraImpl();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mICamera.releaseCamera();
    }
}
