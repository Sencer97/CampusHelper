package com.test.campushelper.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.test.campushelper.R;
import com.test.campushelper.adapter.GridViewAdapter;
import com.test.campushelper.model.CommentItem;
import com.test.campushelper.model.Notice;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.utils.GifSizeFilter;
import com.test.campushelper.utils.PathGetter;
import com.test.campushelper.view.ShowPicGridView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * 教师发布通知或学生提问、学院发公告
 */
public class PublishActivity extends BaseActivity implements View.OnClickListener{

    private static final int REQUEST_CODE_CHOOSE = 23;
    private EditText et_content;
    private Button publishBtn;
    private List<Uri> selectedUri = new ArrayList<>();
    private FloatingActionButton fabAddPic;
    private ShowPicGridView gridView;
    private GridViewAdapter adapter;
    private String title;
    private Notice notice = new Notice();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_class_help_publish);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        title = getIntent().getStringExtra("title");
        setTitle(title);
        setBackArrow();
        init();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            selectedUri = Matisse.obtainResult(data);
            adapter.setData(selectedUri);
            Log.d("selectURI", "已选择的图片: "+selectedUri);

        }
    }
    private void init() {
        et_content = findViewById(R.id.et_help_content);
        et_content.setHint(getIntent().getStringExtra("hint"));

        publishBtn = findViewById(R.id.btn_publish);
        publishBtn.setOnClickListener(this);
        fabAddPic = findViewById(R.id.fab_add_pic);
        fabAddPic.setOnClickListener(this);
        gridView = findViewById(R.id.gv_show_select_pic);
        adapter = new GridViewAdapter(this,selectedUri);
        gridView.setAdapter(adapter);
        //点击查看大图
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getBaseContext(),"你点击了图"+position+"",Toast.LENGTH_SHORT).show();
            }
        });
        adapter.setOnItemDeleteClickListener(new GridViewAdapter.onItemDeleteListener() {
            @Override
            public void onDeleteClick(int position) {
                selectedUri.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_publish:
                if(et_content.getText().toString().equals("")){
                    toast("请输入文字内容...");
                    break;
                }
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("提示");
                progressDialog.setMessage("发布中...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                //发布通知并上传到bmob后台
                notice.setName(Constant.curUser.getUserName());
                notice.setContent(et_content.getText().toString());
                notice.setTime(Constant.getCurTime());
                notice.setDepart(Constant.curUser.getDepart());
                if (title.equals("在线提问")){
                    notice.setTag("student_questions");
                }else{
                    notice.setTag("teacher_notices");
                }
                notice.setComments(new ArrayList<CommentItem>());
                notice.setHeadUrl(Constant.curUser.getHeadUrl());
                //带有图片的上传图片
                if (selectedUri.size() != 0){
                    notice.setHasPic(true);
                    final String[] filePaths = new String[selectedUri.size()];
                    for (int i=0;i<selectedUri.size();i++) {
                        filePaths[i] = PathGetter.getPath(this,selectedUri.get(i));
                    }
                    new Thread(){
                        @Override
                        public void run(){
                            BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                                @Override
                                public void onSuccess(List<BmobFile> list, List<String> urls) {
                                    if (urls.size() == filePaths.length){
                                        //文件上传完成 保存上传的文件的url
                                        Log.d("upload", "文件url:"+urls);
                                        notice.setPicUrls(urls);
                                        notice.save(new SaveListener<String>() {
                                            @Override
                                            public void done(String s, BmobException e) {
                                                if (e==null){
                                                    String id = notice.getObjectId();
                                                    notice.setValue("id",id);
                                                    notice.update(id, new UpdateListener() {
                                                        @Override
                                                        public void done(BmobException e) {
                                                        }
                                                    });
                                                    Toast.makeText(getBaseContext(),"发布成功！",Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }else{
                                                    Toast.makeText(getBaseContext(),"发布失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                                                }
                                                progressDialog.dismiss();
                                            }
                                        });
                                    }
                                }
                                @Override
                                public void onProgress(int i, int i1, int i2, int i3) {
                                }
                                @Override
                                public void onError(int i, String s) {
                                    Log.d("upload", "onError: "+s);
                                }
                            });
                        }
                    }.start();

                }else {
                    notice.setPicUrls(new ArrayList<String>());
                }
                if (notice.isHasPic()){
                    //上传图片的同时 保存了--所以直接退出
                    break;
                }else {
                    notice.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e==null){
                                String id = notice.getObjectId();
                                notice.setValue("id",id);
                                notice.update(id, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                    }
                                });
                                Toast.makeText(getBaseContext(),"发布成功！",Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(getBaseContext(),"发布失败！"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                }


                break;
            case R.id.fab_add_pic:
                //发布帮助 上传数据到服务器 更新recyclerView
                if (selectedUri.size() == 9) {
                    //最多只能添加9张图片
                    Toast.makeText(this, "最多只能添加9张图片哦~", Toast.LENGTH_SHORT).show();
                } else {
                    //TODO 测试图片选择
                    Matisse.from(PublishActivity.this)
                            .choose(MimeType.allOf())
                            .countable(true)        //是否显示数字
                            .maxSelectable(9)
//                        .capture(true)          //是否可以拍照
//                        .captureStrategy(       //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
//                                new CaptureStrategy(true, "com.zhihu.matisse.sample.fileprovider"))
                            .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .forResult(REQUEST_CODE_CHOOSE);
                }
                break;
        }
    }
}
