package com.test.campushelper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.test.campushelper.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends Activity implements View.OnClickListener{

    @BindView(R.id.about_toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_update)
    Button updateBtn;
    @BindView(R.id.fab_share)
    FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        toolbar.setNavigationIcon(R.mipmap.ic_back_white_48dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateBtn.setOnClickListener(this);
        floatingActionButton.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update:
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("更新提示")
                        .setMessage("当前已是最新版本哦~")
                        .setPositiveButton("朕知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                break;
            case R.id.fab_share:
                //调用系统分享.
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"来自「校园帮」的分享：CampusHelper");
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent,"分享到"));
//                Constant.share(getBaseContext(),"分享到","来自「校园帮」的分享：CampusHelper");
                break;
        }
    }
}
