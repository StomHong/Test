package com.stomhong.camera;

import android.hardware.Camera;
import android.util.Log;

public class CameraImpl implements ICamera {
    private static final String TAG = CameraImpl.class.getSimpleName();
    private CameraConfig mCameraConfig;
    private int mCurrentCameraId;
    private Camera mCamera;

    @Override
    public void setCameraConfig(CameraConfig config) {
        this.mCameraConfig = config;
    }

    @Override
    public void switchCamera() {
        if (mCamera != null) {
            releaseCamera();
        }
        if (mCurrentCameraId == ICamera.CAMERA_FACE_FRONT) {
            openFrontCamera();
        } else {
            openBackCamera();
        }
    }

    @Override
    public void startPreview() {

    }

    /**
     * 打开后置摄像头
     */
    private void openBackCamera(){
        Camera.CameraInfo info = new Camera.CameraInfo();

        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCamera = Camera.open(i);
                mCurrentCameraId = ICamera.CAMERA_FACE_BACK;
                break;
            }
        }
        if (mCamera == null) {
            Log.d(TAG, "No back-facing camera found");
        }
    }

    /**
     * 打开前置摄像头
     */
    private void openFrontCamera(){
        Camera.CameraInfo info = new Camera.CameraInfo();

        int numCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numCameras; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCamera = Camera.open(i);
                mCurrentCameraId = ICamera.CAMERA_FACE_FRONT;
                break;
            }
        }
        if (mCamera == null) {
            Log.d(TAG, "No front-facing camera found");
            //如果打开前置摄像头失败，则打开后置摄像头
            mCamera = Camera.open();    // opens first back-facing camera
            mCurrentCameraId = ICamera.CAMERA_FACE_BACK;
        }
    }

    @Override
    public void openCamera(int cameraId) {
        if (mCamera != null) {
            throw new RuntimeException("camera already initialized");
        }
        if (cameraId == ICamera.CAMERA_FACE_FRONT){
            openFrontCamera();
        }else {
            openBackCamera();
        }
    }

    @Override
    public void startRecord() {

    }

    @Override
    public void stopRecord() {

    }


    @Override
    public void releaseCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
