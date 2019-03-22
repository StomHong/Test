package com.stomhong.test.record;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.stomhong.test.R;

import java.io.IOException;

public class MediaCodec2Activity extends AppCompatActivity implements SurfaceHolder.Callback,
        Camera.PreviewCallback {

    private static final String TAG = MediaCodec2Activity.class.getSimpleName();

    private Camera.Parameters mParameters;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private SurfaceView mSurfaceView;

    MediaCodec.BufferInfo mBufferInfo;
    MediaCodec mEncoder;

    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 15;               // 15fps
    private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames
    private static final int NUM_FRAMES = 30;

    private int mBitRate = -1;

    private static final int VIDEO_WIDTH = 1920;
    private static final int VIDEO_HEIGHT = 1080;
    private MediaFormat format;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_media_codec);
        init();
    }

    private void init() {
        mSurfaceView = findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(this);

        mBufferInfo = new MediaCodec.BufferInfo();

        //配置媒体格式
        format = MediaFormat.createVideoFormat(MIME_TYPE, VIDEO_WIDTH, VIDEO_HEIGHT);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//颜色
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);//比特率
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);//帧率
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);//I帧间隔，I帧间隔越小视频压缩率越低,视频就会越大
    }

    /**
     * 打开摄像头
     *
     * @param position 摄像头位置
     */
    public void openCamera(int position, SurfaceHolder holder) {
        mCamera = Camera.open(position);//打开摄像头
        setCameraParameters();
        try {
            mCamera.setDisplayOrientation(90);//设置显示翻转，为0则是水平录像，90为竖屏
            mCamera.setPreviewDisplay(mSurfaceView.getHolder());//将onSurfaceTextureAvailable监听中的surface传入进来，设置预览的控件
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();//开始预览
        mCamera.setPreviewCallback(this);
    }

    /**
     * 设置参数
     */
    private void setCameraParameters() {
        try {
            mParameters = mCamera.getParameters();//设置参数
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//设置自动对焦
            mParameters.setPreviewFormat(ImageFormat.NV21);
            CameraUtils.choosePreviewSize(mParameters, VIDEO_WIDTH, VIDEO_HEIGHT);
//            mParameters.setPreviewSize(sizes.get(0).width,sizes.get(0).height);//设置预览尺寸，为了全屏展示，我们选择最大尺寸，同时TextureView也应该是match_parent全屏
            mCamera.setParameters(mParameters);//设置相机的参数
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放摄像头资源
     */
    public void releaseCamera() {
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, holder);
        try {
            //创建编码器
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //配置编码器
        mEncoder.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
        //启动编码器
        mEncoder.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSurfaceView.getHolder().removeCallback(this);
    }
}
