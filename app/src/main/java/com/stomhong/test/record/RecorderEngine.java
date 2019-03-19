package com.stomhong.test.record;

public interface RecorderEngine {

    /**
     * 打开摄像头
     */
    void openCamera();
    /**
     * 切换摄像头
     */
    void switchCamera();
    /**
     * 开始录制
     */
    void startRecord();
    /**
     * 完成录制
     */
    void stopRecord();
    /**
     * 释放摄像头资源
     */
    void release();
}
