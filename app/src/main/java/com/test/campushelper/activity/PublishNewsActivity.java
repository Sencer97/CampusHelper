package com.test.campushelper.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.test.campushelper.R;
import com.test.campushelper.adapter.GridViewAdapter;
import com.test.campushelper.model.CommentItem;
import com.test.campushelper.model.News;
import com.test.campushelper.model.RecruitInfo;
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

public class PublishNewsActivity extends BaseActivity {
    private EditText et_title,et_content;
    private static final int REQUEST_CODE_CHOOSE = 23;
    private List<Uri> selectedUri = new ArrayList<>();
    private FloatingActionButton fabAddPic;
    private ShowPicGridView gridView;
    private GridViewAdapter adapter;
    private String title;
    private News news = new News();
    private RecruitInfo recruitInfo = new RecruitInfo();
    private MenuItem menuItem;
    private EditChangedListener editChangedListener = new EditChangedListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_publish_news);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        title = getIntent().getStringExtra("title");         //发布新闻公告 ， 发布招聘内推
        setTitle(title);
        setBackArrow();
        setToolBarMenu(R.menu.advice_menu);
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
    class EditChangedListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(et_title.getText().toString()) || !TextUtils.isEmpty(et_content.getText().toString())){
                menuItem.setVisible(true);
            }else{
                menuItem.setVisible(false);
            }
        }
    }
    private void init() {
        et_title = findViewById(R.id.et_news_title);
        et_content = findViewById(R.id.et_news_content);

        et_title.addTextChangedListener(editChangedListener);
        et_content.addTextChangedListener(editChangedListener);

        gridView = findViewById(R.id.gv_show_select_pic);
        adapter = new GridViewAdapter(this,selectedUri);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
        adapter.setOnItemDeleteClickListener(new GridViewAdapter.onItemDeleteListener() {
            @Override
            public void onDeleteClick(int position) {
                selectedUri.remove(position);
                adapter.notifyDataSetChanged();
            }
        });


        fabAddPic = findViewById(R.id.fab_add_news_pic);
        fabAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发布帮助 上传数据到服务器 更新recyclerView
                if (selectedUri.size() == 9) {
                    //最多只能添加9张图片
                    toast("最多只能添加9张图片哦~");
                } else {
                    //TODO 测试图片选择
                    Matisse.from(PublishNewsActivity.this)
                            .choose(MimeType.allOf())
                            .countable(true)        //是否显示数字
                            .maxSelectable(9)
                            .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                            .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                            .thumbnailScale(0.85f)
                            .imageEngine(new GlideEngine())
                            .forResult(REQUEST_CODE_CHOOSE);
                }
            }
        });
    }

    /** 创建菜单 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.advice_menu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_advice_ok:
                //发布新闻公告
                if(TextUtils.isEmpty(et_title.getText().toString())){
                   et_title.setError("请输入标题");
                   break;
                }
                if(TextUtils.isEmpty(et_content.getText().toString())){
                    et_title.setError("请输入内容");
                    break;
                }
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("提示");
                progressDialog.setMessage("发布中...");
                progressDialog.setCancelable(true);
                progressDialog.show();
                //发布通知并上传到bmob后台

                if(title.equals("发布新闻公告")){
                    news.setName(Constant.curUser.getUserName());
                    news.setContent(et_content.getText().toString());
                    news.setTime(Constant.getCurTime());
                    news.setDepart(Constant.curUser.getDepart());
                    news.setTag("news");
                    news.setTitle(et_title.getText().toString());
                    news.setHeadUrl(Constant.curUser.getHeadUrl());
                    //带有图片的上传图片
                    if (selectedUri.size() != 0){
                        news.setHasPic(true);
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
                                            news.setPicUrls(urls);
                                            news.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    if (e==null){
                                                        String id = news.getObjectId();
                                                        news.setValue("id",id);
                                                        news.update(id, new UpdateListener() {
                                                            @Override
                                                            public void done(BmobException e) {
                                                            }
                                                        });
                                                        toast("发布成功！");
                                                        finish();
                                                    }else{
                                                        toast("发布失败！"+e.getMessage());
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
                        news.setPicUrls(new ArrayList<String>());
                    }
                    if (news.isHasPic()){
                        //上传图片的同时 保存了--所以直接退出
                        break;
                    }else {
                        news.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e==null){
                                    String id = news.getObjectId();
                                    news.setValue("id",id);
                                    news.update(id, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                        }
                                    });
                                    toast("发布成功！");
                                    finish();
                                }else{
                                    toast("发布失败！");
                                }
                                progressDialog.dismiss();
                            }
                        });
                    }
                }else{
                    recruitInfo.setName(Constant.curUser.getUserName());
                    recruitInfo.setContent(et_content.getText().toString());
                    recruitInfo.setTime(Constant.getCurTime());
                    recruitInfo.setTag("recruit");
                    recruitInfo.setTitle(et_title.getText().toString());
                    recruitInfo.setHeadUrl(Constant.curUser.getHeadUrl());
                    recruitInfo.setComments(new ArrayList<CommentItem>());

                    //带有图片的上传图片
                    if (selectedUri.size() != 0){
                        recruitInfo.setHasPic(true);
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
                                            recruitInfo.setPicUrls(urls);
                                            recruitInfo.save(new SaveListener<String>() {
                                                @Override
                                                public void done(String s, BmobException e) {
                                                    if (e==null){
                                                        String id = recruitInfo.getObjectId();
                                                        recruitInfo.setValue("id",id);
                                                        recruitInfo.update(id, new UpdateListener() {
                                                            @Override
                                                            public void done(BmobException e) {
                                                            }
                                                        });
                                                        toast("发布成功！");
                                                        finish();
                                                    }else{
                                                        toast("发布失败！"+e.getMessage());
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
                        recruitInfo.setPicUrls(new ArrayList<String>());
                    }
                    if (recruitInfo.isHasPic()){
                        //上传图片的同时 保存了--所以直接退出
                        break;
                    }else {
                        recruitInfo.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e==null){
                                    String id = recruitInfo.getObjectId();
                                    recruitInfo.setValue("id",id);
                                    recruitInfo.update(id, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                        }
                                    });
                                    toast("发布成功！");
                                    finish();
                                }else{
                                    toast("发布失败！");
                                }
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_advice_ok).setVisible(false);
        menu.findItem(R.id.menu_more).setVisible(false);
        menuItem = menu.findItem(R.id.menu_advice_ok);
        return super.onPrepareOptionsMenu(menu);
    }

}
