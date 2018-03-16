package com.test.campushelper.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.test.campushelper.R;

public class EditDataActivity extends BaseActivity {

    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_edit_data);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        setTitle(title);
        setBackArrow();
    }
}
