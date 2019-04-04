package com.stomhong.camera;


/**
 * 摄像头接口顶层
 */
public interface ICamera {
    /**
     * 前置摄像头
     */
    int CAMERA_FACE_FRONT = 0;
    /**
     * 后置摄像头
     */
    int CAMERA_FACE_BACK = 1;
    /**
     * 配置摄像头参数
     */
    void setCameraConfig(CameraConfig config);

    /**
     * 打开摄像头
     */
    void openCamera(int cameraId);

    /**
     * 切换摄像头
     */
    void switchCamera();

    /**
     * 开始预览
     */
    void startPreview();

    /**
     * 开始录制
     */
    void startRecord();

    /**
     * 停止录制
     */
    void stopRecord();

    /**
     * 释放摄像头资源
     */
    void releaseCamera();

    /**
     * 合成视频
     */
    void joinVideo();

    /**
     * 回删某一段视频
     */
    void backDeleteVideo();
}
