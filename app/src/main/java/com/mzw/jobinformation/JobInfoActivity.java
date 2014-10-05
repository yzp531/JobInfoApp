package com.mzw.jobinformation;


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class JobInfoActivity extends ActionBarActivity{

    TextView title;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_info);

        getActionBar().setBackgroundDrawable(new ColorDrawable(0xFF228b22));

        title = (TextView)findViewById(R.id.title);

        Intent intent = getIntent();
        Uri number = intent.getData();
        number.toString();
        title.setText("Job" + number);
    }
}
