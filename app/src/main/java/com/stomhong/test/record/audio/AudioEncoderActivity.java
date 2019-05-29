package com.stomhong.test.record.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.stomhong.test.R;

import java.io.IOException;
import java.nio.ByteBuffer;

public class AudioEncoderActivity extends AppCompatActivity {

    boolean isRunning = true;

    private static final String audioMime = "audio/mp4a-latm";

    private MediaCodec mAudioCodec;
    private AudioRecord mAudioRecorder;
    private int minBufferSize;
    private long prevOutputPTSUs;
    private int mSampleRate = 44100;
    private int mBitRate = 80000;// MediaCodecInfo.CodecProfileLevel.AACObjectLC >= 80Kbps
    Thread mEncoderThread;
    private AudioRecord mRecord;
    private byte[] mBuffer;
    private MediaCodec.BufferInfo mBufferInfo;
    private byte[] mFrameByte;
    private int mBufferSize = 1024;

    FileWriter mFileWriter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_encoder);
        init();
    }

    private void init() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/audio.aac";
        findViewById(R.id.btn_encode_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MediaFormat format = new MediaFormat();
                    format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
                    format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
                    format.setInteger(MediaFormat.KEY_SAMPLE_RATE, mSampleRate);
                    format.setString(MediaFormat.KEY_MIME,audioMime);
                    format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLTP);
                    mAudioCodec = MediaCodec.createEncoderByType(audioMime);
                    mAudioCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                    mAudioCodec.start();

                    mBufferInfo = new MediaCodec.BufferInfo();

                    int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_STEREO,
                            AudioFormat.ENCODING_PCM_16BIT);
                    mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mSampleRate,
                            AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 2);
                    mRecord.startRecording();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mEncoderThread = new EncoderThread();
                mEncoderThread.start();

                mFileWriter = new FileWriter(path);
                mFileWriter.startWrite();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        if (mAudioCodec != null){
            mAudioCodec.stop();
            mAudioCodec.release();
            mAudioCodec = null;
        }
        if (mRecord != null){
            mRecord.stop();
            mRecord.release();
            mRecord = null;
        }
        if (mFileWriter != null){
            mFileWriter.stopWrite();
        }
    }

    @Override
    public void onBackPressed() {
        isRunning = false;
        super.onBackPressed();
    }

    private void encode(byte[] data) {
        if (mAudioCodec == null) return;
        int inputBufferIndex = mAudioCodec.dequeueInputBuffer(-1);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = mAudioCodec.getInputBuffer(inputBufferIndex);
            inputBuffer.clear();
            inputBuffer.put(data);
            inputBuffer.limit(data.length);
            mAudioCodec.queueInputBuffer(inputBufferIndex, 0, data.length,
                    System.nanoTime(), 0);
        }

        int outputBufferIndex = mAudioCodec.dequeueOutputBuffer(mBufferInfo, 0);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputBuffer = mAudioCodec.getOutputBuffer(outputBufferIndex);
            //给adts头字段空出7的字节
            int length=mBufferInfo.size+7;
            if(mFrameByte==null||mFrameByte.length<length){
                mFrameByte=new byte[length];
            }
            outputBuffer.position(mBufferInfo.offset);
            outputBuffer.limit(mBufferInfo.offset + mFrameByte.length);
            addADTStoPacket(mFrameByte,length);
            outputBuffer.get(mFrameByte,7,mBufferInfo.size);
            outputBuffer.position(mBufferInfo.offset);
//            boolean isSusscess1=mClient.sendInt(length);
//            boolean isSusscess2=mClient.send(mFrameByte,0,length);
//            if(!(isSusscess1&&isSusscess2)){
//                isRunning=false;
//                mClient.release();
//            }
            mFileWriter.writeToFile(ByteBuffer.wrap(mFrameByte));
            mAudioCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mAudioCodec.dequeueOutputBuffer(mBufferInfo, 0);

            Log.d("mFrameByte === ",mFrameByte.toString());
        }
    }

    /**
     * 给编码出的aac裸流添加adts头字段
     * @param packet 要空出前7个字节，否则会搞乱数据
     * @param packetLen
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        int freqIdx = 4;  //44.1KHz
        int chanCfg = 2;  //CPE
        packet[0] = (byte)0xFF;
        packet[1] = (byte)0xF9;
        packet[2] = (byte)(((profile-1)<<6) + (freqIdx<<2) +(chanCfg>>2));
        packet[3] = (byte)(((chanCfg&3)<<6) + (packetLen>>11));
        packet[4] = (byte)((packetLen&0x7FF) >> 3);
        packet[5] = (byte)(((packetLen&7)<<5) + 0x1F);
        packet[6] = (byte)0xFC;
    }

    class EncoderThread extends Thread{
        @Override
        public void run() {
            mBuffer = new byte[mBufferSize];
            while (isRunning){
                int num = mRecord.read(mBuffer,0,mBufferSize);
                Log.d("tag === ",num + " " + mBuffer.toString());
                encode(mBuffer);
            }

        }
    }
}
