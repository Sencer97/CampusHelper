package com.test.campushelper.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.chrisbanes.photoview.PhotoView;
import com.test.campushelper.R;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

public class BigImagePagerActivity extends AppCompatActivity {
    private TextView tv_guide;          //底部导航文字
    private String guide;
    private ViewPager viewPager;
    private List<String> picUrls;       //图片链接
    private int curIndex;               //当前图片索引

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image_pager);
        init();
    }

    private void init() {
        //透明状态栏
        StatusBarCompat.translucentStatusBar(this);
        viewPager = findViewById(R.id.vp_big_images);
        tv_guide = findViewById(R.id.tv_guide);

        curIndex = getIntent().getIntExtra("position",0);
        picUrls = getIntent().getStringArrayListExtra("picUrls");

        ImageAdapter adapter = new ImageAdapter(this);
        adapter.setDatas(picUrls);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                //更改文字导航
                tv_guide.setText((position+1)+"/"+picUrls.size());
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setCurrentItem(curIndex);
        tv_guide.setText((curIndex+1)+"/"+picUrls.size());
    }
    private  class ImageAdapter extends PagerAdapter {

        private List<String> datas = new ArrayList<String>();
        private LayoutInflater inflater;
        private Context context;

        public void setDatas(List<String> datas) {
            if(datas != null )
                this.datas = datas;
        }

        public ImageAdapter(Context context){
            this.context = context;
            this.inflater = LayoutInflater.from(context);

        }

        @Override
        public int getCount() {
            if(datas == null) return 0;
            return datas.size();
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.item_image_page, container, false);
            if(view != null){
                final PhotoView imageView = view.findViewById(R.id.ziv_iamge);
                //单击图片退出
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BigImagePagerActivity.this.finish();
                        BigImagePagerActivity.this.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    }
                });
                imageView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //长按图片保存
                        AlertDialog.Builder builder = new AlertDialog.Builder(BigImagePagerActivity.this);
                        builder.setTitle("图片");
                        builder.setNegativeButton("取消",null);
                        String[] strings = new String[]{"保存图片到本地","分享图片"};
                        builder.setItems(strings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        //保存图片
                                        imageView.setDrawingCacheEnabled(true);
                                        Bitmap bitmap = Bitmap.createBitmap(imageView.getDrawingCache());
                                        imageView.setDrawingCacheEnabled(false);
                                        String path = Constant.saveImageToGallery(BigImagePagerActivity.this,bitmap);
                                        Toast.makeText(getBaseContext(),"文件已存至："+path,Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        //分享
                                        Constant.share(context,"分享到","来自「校园帮」的分享："+datas.get(position));
                                        break;
                                }
                            }
                        });
                        builder.show();
                        return true;
                    }
                });

                final String imgurl = datas.get(position);

                Glide.with(context).load(imgurl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_image_loading)
                        .error(R.drawable.ic_empty_picture)
                        .thumbnail(0.1f)
                        .into(imageView);

                container.addView(view, 0);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }


    /**
     * 监听返回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            this.overridePendingTransition(R.anim.fade_in,
                    R.anim.fade_out);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
