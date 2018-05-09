package com.test.campushelper.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.test.campushelper.R;
import com.test.campushelper.model.Advice;
import com.test.campushelper.model.Attender;
import com.test.campushelper.model.CommentItem;
import com.test.campushelper.model.Match;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PublishAdviceActivity extends BaseActivity {

    private EditText et_title,et_content;
    private Advice advice = new Advice();
    private Match match = new Match();
    private MenuItem menuItem;
    private EditChangedListener editChangedListener = new EditChangedListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_publish_advice);
        Bmob.initialize(this, Constant.BMOB_APPKEY);
        setTitle(getIntent().getStringExtra("title"));
        setBackArrow();
        setToolBarMenu(R.menu.advice_menu);
        init();
    }

    private void init() {
        et_title = findViewById(R.id.et_advice_title);
        et_content = findViewById(R.id.et_advice_content);
        et_title.addTextChangedListener(editChangedListener);
        et_content.addTextChangedListener(editChangedListener);
    }
    class EditChangedListener implements TextWatcher{
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
                //发布建议
                if (getIntent().getStringExtra("title").equals("发布竞赛活动")){
                    match.setTitle(et_title.getText().toString());
                    match.setContent(et_content.getText().toString());
                    match.setTag("tag");
                    match.setTagName(Constant.curUser.getUserName());
                    match.setTime(Constant.getCurTime());
                    match.setAtteners(new ArrayList<Attender>());
                    match.setHeadUrl(Constant.curUser.getHeadUrl());
                    match.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                String id = match.getObjectId();
                                match.setId(id);
                                match.update(id, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            toast("发布成功！");
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });

                }else{
                    advice.setTitle(et_title.getText().toString());
                    advice.setContent(et_content.getText().toString());
                    advice.setTime(Constant.getCurTime());
                    advice.setTagName(Constant.curUser.getUserName());
                    advice.setComments(new ArrayList<CommentItem>());
                    advice.setTag("tag");
                    advice.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                String id = advice.getObjectId();
                                advice.setId(id);
                                advice.update(id, new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            toast("发布成功！");
                                            finish();
                                        }
                                    }
                                });
                            }
                        }
                    });
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
