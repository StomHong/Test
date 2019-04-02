//package com.stomhong.test.record;
//
//import android.graphics.ImageFormat;
//import android.hardware.Camera;
//import android.media.MediaCodec;
//import android.media.MediaCodecInfo;
//import android.media.MediaFormat;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
//import android.view.WindowManager;
//
//import com.stomhong.test.R;
//
//import java.io.IOException;
//import java.nio.ByteBuffer;
//
//public class MediaCodec2Activity extends AppCompatActivity implements SurfaceHolder.Callback,
//        Camera.PreviewCallback {
//
//    private static final String TAG = MediaCodec2Activity.class.getSimpleName();
//
//    private Camera.Parameters mParameters;
//    private Camera mCamera;
//    private SurfaceHolder mHolder;
//    private SurfaceView mSurfaceView;
//
//    MediaCodec.BufferInfo mBufferInfo;
//    MediaCodec mEncoder;
//
//    // parameters for the encoder
//    private static final String MIME_TYPE = "video/avc";    // H.264 Advanced Video Coding
//    private static final int FRAME_RATE = 15;               // 15fps
//    private static final int IFRAME_INTERVAL = 10;          // 10 seconds between I-frames
//    private static final int NUM_FRAMES = 30;
//
//    private int mBitRate = -1;
//
//    private static final int VIDEO_WIDTH = 1920;
//    private static final int VIDEO_HEIGHT = 1080;
//    private MediaFormat format;
//    private boolean mIsEOS = false;
//    protected static final int TIMEOUT_USEC = 10000;	// 10[msec]
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_media_codec);
//        init();
//    }
//
//    private void init() {
//        mSurfaceView = findViewById(R.id.surfaceView);
//        mSurfaceView.getHolder().addCallback(this);
//
//        mBufferInfo = new MediaCodec.BufferInfo();
//
//        //配置媒体格式
//        format = MediaFormat.createVideoFormat(MIME_TYPE, VIDEO_WIDTH, VIDEO_HEIGHT);
//        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//颜色
//        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);//比特率
//        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);//帧率
//        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);//I帧间隔，I帧间隔越小视频压缩率越低,视频就会越大
//
////        mBufferInfo = new MediaCodec.BufferInfo();
////
////        //配置媒体格式
////        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, VIDEO_WIDTH, VIDEO_HEIGHT);
////        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);//颜色
////        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);//比特率
////        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);//帧率
////        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, IFRAME_INTERVAL);//I帧间隔，I帧间隔越小视频压缩率越低,视频就会越大
////        try {
////            //创建编码器
////            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        //配置编码器
////        mEncoder.configure(format,mSurfaceView.getHolder().getSurface(),null,0);
////        //启动编码器
////        mEncoder.start();
//
//    }
//
//    /**
//     * 打开摄像头
//     *
//     * @param position 摄像头位置
//     */
//    public void openCamera(int position, SurfaceHolder holder) {
//        mCamera = Camera.open(position);//打开摄像头
//        setCameraParameters();
//        try {
//            mCamera.setDisplayOrientation(90);//设置显示翻转，为0则是水平录像，90为竖屏
//            mCamera.setPreviewDisplay(mSurfaceView.getHolder());//将onSurfaceTextureAvailable监听中的surface传入进来，设置预览的控件
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mCamera.startPreview();//开始预览
//        mCamera.setPreviewCallback(this);
//    }
//
//    /**
//     * 设置参数
//     */
//    private void setCameraParameters() {
//        try {
//            mParameters = mCamera.getParameters();//设置参数
//            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//设置自动对焦
//            mParameters.setPreviewFormat(ImageFormat.NV21);
//            CameraUtils.choosePreviewSize(mParameters, VIDEO_WIDTH, VIDEO_HEIGHT);
////            mParameters.setPreviewSize(sizes.get(0).width,sizes.get(0).height);//设置预览尺寸，为了全屏展示，我们选择最大尺寸，同时TextureView也应该是match_parent全屏
//            mCamera.setParameters(mParameters);//设置相机的参数
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 释放摄像头资源
//     */
//    public void releaseCamera() {
//        mCamera.setPreviewCallback(null);
//        mCamera.stopPreview();
//        mCamera.release();
//        mCamera = null;
//    }
//
//    @Override
//    public void surfaceCreated(SurfaceHolder holder) {
//
//        openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT, holder);
//        try {
//            //创建编码器
//            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //配置编码器
//        mEncoder.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
//        //启动编码器
//        mEncoder.start();
//
//        openCamera(Camera.CameraInfo.CAMERA_FACING_BACK,holder);
//
//    }
//
//    @Override
//    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//    }
//
//    @Override
//    public void surfaceDestroyed(SurfaceHolder holder) {
//        releaseCamera();
//    }
//
//    @Override
//    public void onPreviewFrame(byte[] data, Camera camera) {
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mSurfaceView.getHolder().removeCallback(this);
//    }
//
//    /**
//     * Method to set byte array to the MediaCodec encoder
//     * @param buffer
//     * @param length　length of byte array, zero means EOS.
//     * @param presentationTimeUs
//     */
//    protected void encode(final ByteBuffer buffer, final int length, final long presentationTimeUs) {
////        if (!mIsCapturing) return;
//        final ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();
//        while (true) {
//            final int inputBufferIndex = mEncoder.dequeueInputBuffer(TIMEOUT_USEC);
//            Log.e(TAG, "inputBufferIndex: "+inputBufferIndex );
//            if (inputBufferIndex >= 0) {
//                final ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
//                inputBuffer.clear();
//                if (buffer != null) {
//                    inputBuffer.put(buffer);
//                }
////                if (DEBUG) Log.v(TAG, "encode:queueInputBuffer");
//                if (length <= 0) {
//                    // send EOS
//                    mIsEOS = true;
////                    if (DEBUG) Log.i(TAG, "send BUFFER_FLAG_END_OF_STREAM");
//                    mEncoder.queueInputBuffer(inputBufferIndex, 0, 0,
//                            presentationTimeUs, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//                    break;
//                } else {
//                    mEncoder.queueInputBuffer(inputBufferIndex, 0, length,
//                            presentationTimeUs, 0);
//                }
//                break;
//            } else if (inputBufferIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                // wait for MediaCodec encoder is ready to encode
//                // nothing to do here because MediaCodec#dequeueInputBuffer(TIMEOUT_USEC)
//                // will wait for maximum TIMEOUT_USEC(10msec) on each call
//            }
//        }
//    }
//
//    /**
//     * drain encoded data and write them to muxer
//     */
//    protected void drain() {
//        if (mMediaCodec == null) return;
//        ByteBuffer[] encoderOutputBuffers = mMediaCodec.getOutputBuffers();
//        Log.e(TAG, "encoderOutputBuffers: "+encoderOutputBuffers.length );
//        int encoderStatus, count = 0;
//        final MediaMuxerWrapper muxer = mWeakMuxer.get();
//        if (muxer == null) {
////        	throw new NullPointerException("muxer is unexpectedly null");
//            Log.w(TAG, "muxer is unexpectedly null");
//            return;
//        }
//        Log.e(TAG, "mIsCapturing: "+mIsCapturing );
//        LOOP:	while (mIsCapturing) {
//            // get encoded data with maximum timeout duration of TIMEOUT_USEC(=10[msec])
//            encoderStatus = mMediaCodec.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC);
//            Log.e(TAG, "encoderStatus: "+encoderStatus );
//            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                // wait 5 counts(=TIMEOUT_USEC x 5 = 50msec) until data/EOS come
//                if (!mIsEOS) {
//                    if (++count > 5)
//                        break LOOP;		// out of while
//                }
//            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
//                if (DEBUG) Log.v(TAG, "INFO_OUTPUT_BUFFERS_CHANGED");
//                // this shoud not come when encoding
//                encoderOutputBuffers = mMediaCodec.getOutputBuffers();
//            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
////                if (DEBUG) Log.v(TAG, "INFO_OUTPUT_FORMAT_CHANGED");
//                // this status indicate the output format of codec is changed
//                // this should come only once before actual encoded data
//                // but this status never come on Android4.3 or less
//                // and in that case, you should treat when MediaCodec.BUFFER_FLAG_CODEC_CONFIG come.
//                if (mMuxerStarted) {	// second time request is error
//                    throw new RuntimeException("format changed twice");
//                }
//                // get output format from codec and pass them to muxer
//                // getOutputFormat should be called after INFO_OUTPUT_FORMAT_CHANGED otherwise crash.
//                final MediaFormat format = mMediaCodec.getOutputFormat(); // API >= 16
//                mTrackIndex = muxer.addTrack(format);
//                mMuxerStarted = true;
//                if (!muxer.start()) {
//                    // we should wait until muxer is ready
//                    synchronized (muxer) {
//                        while (!muxer.isStarted())
//                            try {
//                                muxer.wait(100);
//                            } catch (final InterruptedException e) {
//                                break LOOP;
//                            }
//                    }
//                }
//            } else if (encoderStatus < 0) {
//                // unexpected status
//                if (DEBUG) Log.w(TAG, "drain:unexpected result from encoder#dequeueOutputBuffer: " + encoderStatus);
//            } else {
//                final ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
//                if (encodedData == null) {
//                    // this never should come...may be a MediaCodec internal error
//                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
//                }
//                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
//                    // You shoud set output format to muxer here when you target Android4.3 or less
//                    // but MediaCodec#getOutputFormat can not call here(because INFO_OUTPUT_FORMAT_CHANGED don't come yet)
//                    // therefor we should expand and prepare output format from buffer data.
//                    // This sample is for API>=18(>=Android 4.3), just ignore this flag here
//                    if (DEBUG) Log.d(TAG, "drain:BUFFER_FLAG_CODEC_CONFIG");
//                    mBufferInfo.size = 0;
//                }
//
//                if (mBufferInfo.size != 0) {
//                    // encoded data is ready, clear waiting counter
//                    count = 0;
//                    if (!mMuxerStarted) {
//                        // muxer is not ready...this will prrograming failure.
//                        throw new RuntimeException("drain:muxer hasn't started");
//                    }
//                    // write encoded data to muxer(need to adjust presentationTimeUs.
//                    mBufferInfo.presentationTimeUs = getPTSUs();
//                    muxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
//                    prevOutputPTSUs = mBufferInfo.presentationTimeUs;
//                }
//                // return buffer to encoder
//                mMediaCodec.releaseOutputBuffer(encoderStatus, false);
//                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
//                    // when EOS come.
//                    mIsCapturing = false;
//                    break;      // out of while
//                }
//            }
//        }
//    }
//}
