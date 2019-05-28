package com.stomhong.test.testlibrary;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.authoring.tracks.h264.H264TrackImpl;
import com.stomhong.test.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        init();
    }

    private void init() {
        textView = findViewById(R.id.text);

//        try {
//            //将h264和aac文件合成MP4
//            H264TrackImpl h264Track = new H264TrackImpl(new FileDataSourceImpl("video.h264"));
//            AACTrackImpl aacTrack = new AACTrackImpl(new FileDataSourceImpl("audio.aac"));
//
//            Movie movie = new Movie();
//            movie.addTrack(h264Track);
//            movie.addTrack(aacTrack);
//
//            Container mp4file = new DefaultMp4Builder().build(movie);
//            FileChannel fc = new FileOutputStream(new File("output.mp4")).getChannel();
//            mp4file.writeContainer(fc);
//            fc.close();
//            //缩短音视频轨道
//            AACTrackImpl aacTrackOriginal = new AACTrackImpl(new FileDataSourceImpl("audio.aac"));
//            // removes the first sample and shortens the AAC track by ~22ms
//            CroppedTrack aacTrackShort = new CroppedTrack(aacTrackOriginal, 1, aacTrack.getSamples().size());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        String video1 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "video1.mp4";
        String video2 = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "video2.mp4";

        String videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "video.mp4";

        List<String> moviePaths = new ArrayList<>();
        moviePaths.add(video1);
        moviePaths.add(video2);
        moviePaths.add(video1);
//        combineVideo(moviePaths,videoPath);
    }

    private void combineVideo(List<String> sourcePaths, String outputPath) {
        try {
            //构造Movie对象
            List<Movie> sourceMovies = new ArrayList<>();
            for (String path : sourcePaths) {
                sourceMovies.add(MovieCreator.build(path));
            }
            //分别提取音频和视频轨道
            List<Track> videoTracks = new ArrayList<>();
            List<Track> audioTracks = new ArrayList<>();
            for (Movie movie : sourceMovies) {
                for (Track track : movie.getTracks()) {
                    if ("soun".equals(track.getHandler())){
                        audioTracks.add(track);
                    }
                    if ("vide".equals(track.getHandler())){
                        videoTracks.add(track);
                    }
                }
            }
            //分别追加音频和视频轨道
            Movie resultMovie = new Movie();
            resultMovie.addTrack(new AppendTrack(audioTracks.toArray(new Track[0])));
            resultMovie.addTrack(new AppendTrack(videoTracks.toArray(new Track[0])));
            //将音频和视频合成MP4
            Container mp4file = new DefaultMp4Builder().build(resultMovie);
            FileChannel fc = new FileOutputStream(new File(outputPath)).getChannel();
            mp4file.writeContainer(fc);
            fc.close();
            Toast.makeText(this,"合并成功",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
