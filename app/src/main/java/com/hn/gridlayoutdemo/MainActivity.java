package com.hn.gridlayoutdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtn;
    private HnGridLayout mHgl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtn = (Button) findViewById(R.id.btn);
        mHgl = (HnGridLayout) findViewById(R.id.hgl);
        mBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mHgl.setCollapse(!mHgl.isCollapse(), true);
    }
}
