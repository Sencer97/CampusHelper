package com.test.campushelper.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.test.campushelper.R;
import com.test.campushelper.adapter.GridViewAdapter;
import com.test.campushelper.model.News;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.view.ShowPicGridView;

import java.util.ArrayList;

public class NewsDetailActivity extends BaseActivity {

    private TextView tv_title,tv_time,tv_content,tv_author;
    private ShowPicGridView gridView;                   //图片列表和适配器
    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_news_detail);
        setTitle("新闻公告详情");
        setBackArrow();
        init();
    }

    private void init() {
        tv_title = findViewById(R.id.tv_news_detail_title);
        tv_time = findViewById(R.id.tv_news_detail_time);
        tv_content = findViewById(R.id.tv_new_detail_content);
        tv_author = findViewById(R.id.tv_news_detail_author);

        news = Constant.curNews;
        tv_title.setText(news.getTitle());
        tv_time.setText(news.getTime());
        tv_content.setText(news.getContent());
        tv_author.setText(news.getName());

        gridView = findViewById(R.id.gv_news_detail_pic);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO 查看大图
                Intent bigImageIntent = new Intent(NewsDetailActivity.this, BigImagePagerActivity.class);
                bigImageIntent.putStringArrayListExtra("picUrls",(ArrayList<String>) news.getPicUrls());
                bigImageIntent.putExtra("position",position);
                startActivity(bigImageIntent);
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

            }
        });
        if (news.isHasPic()){
            gridView.setVisibility(View.VISIBLE);
            gridView.setAdapter(new GridViewAdapter(this,news.getPicUrls(),true));
        }else{
            gridView.setVisibility(View.GONE);
        }


    }

}
