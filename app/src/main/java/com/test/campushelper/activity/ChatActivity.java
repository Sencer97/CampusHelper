package com.test.campushelper.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.test.campushelper.R;

public class ChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_chat);

        setTitle("");
        setBackArrow();
    }
}
