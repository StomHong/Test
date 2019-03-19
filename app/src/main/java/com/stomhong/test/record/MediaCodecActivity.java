package com.stomhong.test.record;

import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.BaseAdapter;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer2.upstream.DataSink;
import com.stomhong.test.R;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaCodecActivity extends AppCompatActivity implements SurfaceHolder.Callback, SurfaceTexture.OnFrameAvailableListener {

    private static final int VIDEO_WIDTH = 720;
    private static final int VIDEO_HIGHT = 1080;
    MediaCodec mEncoder;
    SurfaceView mSurfaceView;
    SurfaceTexture mSurfaceTexture;
    int textureId = -1;
    boolean eosReceived = false;
    MediaCodec.BufferInfo mBufferInfo;

    EglCore mEglCore;
    WindowSurface mWindowSurface;

    // parameters for the encoder
    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
    private static final int FRAME_RATE = 15;               // 15fps
    private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames
    private static final int NUM_FRAMES = 30;

    private int mBitRate = -1;

    Camera camera;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_codec);
        init();
    }

    private void init() {

        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);

        mSurfaceView = findViewById(R.id.surfaceView);

//        mBufferInfo = new MediaCodec.BufferInfo();
//
//        //配置媒体格式
//        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, VIDEO_WIDTH, VIDEO_HIGHT);
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//颜色
//        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);//比特率
//        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);//帧率
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);//I帧间隔，I帧间隔越小视频压缩率越低,视频就会越大
//        try {
//            //创建编码器
//            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //配置编码器
//        mEncoder.configure(format,null,null,0);
//        //启动编码器
//        mEncoder.start();
        handlerThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceTexture = new SurfaceTexture(-1);
        mSurfaceTexture.setOnFrameAvailableListener(this);

        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(1920,1080);//设置预览尺寸，为了全屏展示，我们选择最大尺寸，同时TextureView也应该是match_parent全屏
        camera.setParameters(parameters);//设置相机的参数
        camera.setDisplayOrientation(90);//设置显示翻转，为0则是水平录像，90为竖屏
        camera.setParameters(parameters);
        try {
            camera.setPreviewTexture(mSurfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.startPreview();

        mEglCore = new EglCore();
        mWindowSurface = new WindowSurface(mEglCore,mSurfaceTexture);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceTexture.release();
        mSurfaceTexture.releaseTexImage();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Texture2dProgram program = new Texture2dProgram(Texture2dProgram.ProgramType.TEXTURE_EXT);
        program.createTextureObject();
        mWindowSurface.swapBuffers();
       handlerThread.quit();
    }

    /**
     * updateTextImage()方法必须在包含texture数据的线程里调用
     */
    HandlerThread handlerThread = new HandlerThread("RenderThread");

    Handler handler = new Handler(handlerThread.getLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

}
