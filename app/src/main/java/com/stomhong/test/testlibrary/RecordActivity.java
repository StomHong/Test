package com.stomhong.test.testlibrary;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.stomhong.camera.CameraConfig;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        CameraConfig config = new CameraConfig.Builder()
                .setHeight(1280)
                .setWidth(720)
                .build();

//        AlertDialog.Builder
    }
}
