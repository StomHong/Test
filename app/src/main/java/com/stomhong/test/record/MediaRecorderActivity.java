package com.stomhong.test.record;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.stomhong.test.R;

import java.io.File;
import java.io.IOException;

public class MediaRecorderActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {

    private static  final String TAG = MediaRecorderActivity.class.getSimpleName();

    TextureView mTextureView;
    MediaRecorder mMediaRecorder;
    Camera camera;
    Camera.Parameters parameters;
    private int current_camera;
    Button mBtnRecord;
    private boolean isRecord = false;
    SurfaceTexture surface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_recorder);

        init();
    }

    private void init() {
        mTextureView = findViewById(R.id.textureView);
        mTextureView.setSurfaceTextureListener(this);
        mBtnRecord = findViewById(R.id.btn_record);
        mBtnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecord) {
                    isRecord = true;
                    record();
                    mBtnRecord.setText("stop");
                }else {
                    isRecord = false;
                    stop();
                    mBtnRecord.setText("record");
                }
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.surface = surface;
        openCamera(Camera.CameraInfo.CAMERA_FACING_BACK,surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * 打开摄像头
     * @param position 摄像头位置
     */
    public void openCamera(int position,SurfaceTexture surface){
        current_camera = position;
        camera = Camera.open(position);//打开摄像头
        parameters= camera.getParameters();//设置参数   parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//设置自动对焦
        try {
            parameters.setPreviewSize(1920,1080);//设置预览尺寸，为了全屏展示，我们选择最大尺寸，同时TextureView也应该是match_parent全屏
            camera.setParameters(parameters);//设置相机的参数
            camera.setDisplayOrientation(90);//设置显示翻转，为0则是水平录像，90为竖屏
            camera.setPreviewTexture(surface);//将onSurfaceTextureAvailable监听中的surface传入进来，设置预览的控件
        } catch (IOException t) {
            Log.d(TAG, "onSurfaceTextureAvailable: IO异常");
        }
        camera.startPreview();//开始预览
        mTextureView.setAlpha(1.0f);
    }

    /**
     * 录制视频
     */
    public void record(){
        Log.d(TAG, "record: 开始录制");
        mMediaRecorder = new MediaRecorder();
        camera.unlock();
        mMediaRecorder.setCamera(camera);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH)); //setProfile不能和后面的setOutputFormat等方法一起使用
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);  // 设置视频的输出格式 为MP4

        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); // 设置音频的编码格式
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264); // 设置视频的编码格式
//        mediaRecorder.setVideoSize(176, 144);  // 设置视频大小
//        mediaRecorder.setVideoSize(320, 240);  // 设置视频大小
        mMediaRecorder.setVideoSize(1920, 1080);  // 设置视频大小
        mMediaRecorder.setVideoEncodingBitRate(5*1024*1024);
        mMediaRecorder.setVideoFrameRate(60); // 设置帧率
        /*
         * 设置视频文件的翻转角度
         * 改变保存后的视频文件播放时是否横屏(不加这句，视频文件播放的时候角度是反的)
         * */
        if (current_camera == Camera.CameraInfo.CAMERA_FACING_FRONT){
            mMediaRecorder.setOrientationHint(270);
        }else if (current_camera == Camera.CameraInfo.CAMERA_FACING_BACK){
            mMediaRecorder.setOrientationHint(90);
        }

//        mRecorder.setMaxDuration(10000); //设置最大录像时间为10s
//        mediaRecorder.setPreviewDisplay();
//        mediaRecorder.setPreviewDisplay(myTexture);


        //设置视频存储路径
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) + File.separator + "yuewu");
        if (!file.exists()) {
            //多级文件夹的创建
            file.mkdirs();
        }

        Log.d(TAG, "record: path " +file.getPath() + File.separator + "乐舞_" + System.currentTimeMillis() + ".mp4");
        mMediaRecorder.setOutputFile(file.getPath() + File.separator + "乐舞_" + System.currentTimeMillis() + ".mp4");

        //开始录制
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录制
     */
    public void stop(){
//        mediaRecorder.setOnErrorListener(null);
//        mediaRecorder.setOnInfoListener(null);
        camera.lock();

        mMediaRecorder.stop();
        mMediaRecorder.release();

        openCamera(Camera.CameraInfo.CAMERA_FACING_BACK,surface);//启动预览,可以判断之前是前置摄像头还是后置摄像头来继续启动预览

        Log.d(TAG, "stop: 录制完成");
    }
}
