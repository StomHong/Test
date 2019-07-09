package com.stomhong.test.record.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
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
    private long prevOutputPTSUs;
    private int mSampleRate = 44100;//采样率
    private int mBitRate = 96000;//码率 MediaCodecInfo.CodecProfileLevel.AACObjectLC >= 80Kbps
    Thread mEncoderThread;
    private AudioRecord mRecord;
    private MediaCodec.BufferInfo mBufferInfo;

    FileWriter mFileWriter;

    MediaMuxer muxer;
    int audioTrackIndex = -1;
    long startTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_encoder);
        init();
    }

    private void init() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/audio.mp4";
        findViewById(R.id.btn_encode_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MediaFormat format = new MediaFormat();
                    format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate);
                    format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 2);
                    format.setInteger(MediaFormat.KEY_SAMPLE_RATE, mSampleRate);
                    format.setString(MediaFormat.KEY_MIME, audioMime);
                    format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
                    mAudioCodec = MediaCodec.createEncoderByType(audioMime);
                    mAudioCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                    mAudioCodec.start();

                    mBufferInfo = new MediaCodec.BufferInfo();

                    int minBufferSize = AudioRecord.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_IN_STEREO,
                            AudioFormat.ENCODING_PCM_16BIT);
                    mRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, mSampleRate,
                            AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
                    mRecord.startRecording();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mEncoderThread = new EncoderThread();
                mEncoderThread.start();

//                mFileWriter = new FileWriter(path);
//                mFileWriter.startWrite();

                try {
                    muxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
        if (mAudioCodec != null) {
            mAudioCodec.stop();
            mAudioCodec.release();
            mAudioCodec = null;
        }
        if (mRecord != null) {
            mRecord.stop();
            mRecord.release();
            mRecord = null;
        }
        if (mFileWriter != null) {
            mFileWriter.stopWrite();
        }
        if (muxer != null){
            muxer.stop();
            muxer.release();
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
            if (audioTrackIndex == -1) {
                startTime = System.nanoTime();
            }
            mAudioCodec.queueInputBuffer(inputBufferIndex, 0, data.length,
                    (System.nanoTime()-startTime)/1000, 0);
        }

        int outputBufferIndex = mAudioCodec.dequeueOutputBuffer(mBufferInfo, 0);
        while (outputBufferIndex >= 0) {
            ByteBuffer outputData = mAudioCodec.getOutputBuffer(outputBufferIndex);
            if (audioTrackIndex == -1) {
                audioTrackIndex = muxer.addTrack(mAudioCodec.getOutputFormat());
                muxer.start();
            }
            //音频帧长度（头部+数据）
            int length = mBufferInfo.size + 7;
            byte[] adtsHeader = new byte[7];
            addADTStoPacket(adtsHeader, length);
            ByteBuffer outputBuffer = ByteBuffer.allocate(length);
            outputBuffer.put(adtsHeader);
            outputBuffer.put(outputData);
            outputBuffer.flip();
//            mFileWriter.writeToFile(outputBuffer);

            muxer.writeSampleData(audioTrackIndex,outputBuffer,mBufferInfo);

            mAudioCodec.releaseOutputBuffer(outputBufferIndex, false);
            outputBufferIndex = mAudioCodec.dequeueOutputBuffer(mBufferInfo, 0);

        }
    }

    /**
     * 0: 96000 Hz
     * 1: 88200 Hz
     * 2: 64000 Hz
     * 3: 48000 Hz
     * 4: 44100 Hz
     * 5: 32000 Hz
     * 6: 24000 Hz
     * 7: 22050 Hz
     * 8: 16000 Hz
     * 9: 12000 Hz
     * 10: 11025 Hz
     * 11: 8000 Hz
     * 12: 7350 Hz
     * 给编码出的aac裸流添加adts头字段
     *
     * @param packet    要空出前7个字节，否则会搞乱数据
     * @param packetLen
     */
    private void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2;  //AAC LC
        int freqIdx = 4;  //44.1KHz
        int chanCfg = 2;  //CPE
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF1;//MPEG-2:0xF9  MPEG-4:0xF1
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;//一般固定为0xFC
    }

    class EncoderThread extends Thread {
        @Override
        public void run() {

           byte[] buffer = new byte[1024];
            while (isRunning) {
                int num = mRecord.read(buffer,0,  buffer.length);
                if (num > 0) {
                    Log.d("tag === ", num + " " + buffer.toString());
                    encode(buffer);
                } else {
                    Log.e("tag === ", num + " 读取录音数据出错");
                }
            }

        }
    }
}
