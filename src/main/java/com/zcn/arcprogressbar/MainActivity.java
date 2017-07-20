package com.zcn.arcprogressbar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ArcProgressBar arcProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arcProgressBar = (ArcProgressBar)findViewById(R.id.arcProgressBar);
        arcProgressBar.setOnProgressChangeListener(new ArcProgressBar.OnProgressChangeListener() {
            @Override
            public void onProgress(int progress) {
                Log.d("Chunna.zheng","output current progress :"+progress);
            }
        });
    }
}
