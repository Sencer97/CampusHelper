package com.test.campushelper.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.test.campushelper.R;
import com.test.campushelper.model.UserData;
import com.test.campushelper.utils.Constant;
import com.test.campushelper.utils.PathGetter;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserCenterActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "UserCenterActivity";
    private RelativeLayout rl_nickname,rl_sex,rl_birthday,rl_school,rl_depart,rl_major,rl_grade,rl_signature,rl_hobby;
    private TextView tv_nickname,tv_sex,tv_birthday,tv_school,tv_depart,tv_major,tv_grade,tv_signature,tv_hobby;
    private CircleImageView head;
    private String birthday;
    private Button exitBtn;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;
    private Bitmap mBitmap;
    private UserData curUser = Constant.curUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_user_center);
        setTitle("个人中心");
        setBackArrow();
        init();
        initData();
    }

    private void initData() {
        if (MainActivity.isLogin){
            new Thread(){
                @Override
                public void run(){
                    BmobQuery<UserData> query = new BmobQuery<UserData>();
                    query.addWhereEqualTo("userName",curUser.getUserName());
                    query.findObjects(new FindListener<UserData>() {
                        @Override
                        public void done(List<UserData> list, BmobException e) {
                            if(e==null){
//                                Glide.with(getBaseContext())
//                                        .load(curUser.getHeadUrl())
//                                        .placeholder(R.drawable.ic_image_loading)
//                                        .error(R.drawable.ic_empty_picture)
//                                        .crossFade()
//                                        .into(head);
                                curUser = list.get(0);
                                tv_nickname.setText(curUser.getUserName());
                                tv_sex.setText(curUser.getSex());
                                tv_birthday.setText(curUser.getBirthday());
                                tv_school.setText(curUser.getSchool());
                                tv_depart.setText(curUser.getDepart());
                                tv_major.setText(curUser.getMajor());
                                tv_grade.setText(curUser.getGrade());
                                tv_signature.setText(curUser.getSignature());
                                tv_hobby.setText(curUser.getHobby());
                            }
                        }
                    });
                }
            }.start();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
    private void init() {
        head = findViewById(R.id.user_head);
        //加载头像
        Glide.with(this)
                .load(curUser.getHeadUrl())
                .placeholder(R.drawable.ic_image_loading)
                .error(R.drawable.ic_empty_picture)
                .crossFade()
                .into(head);
        rl_nickname = findViewById(R.id.rl_nickname);
        rl_sex = findViewById(R.id.rl_sex);
        rl_birthday = findViewById(R.id.rl_birhday);
        rl_depart = findViewById(R.id.rl_department);
        rl_school = findViewById(R.id.rl_school);
        rl_major = findViewById(R.id.rl_major);
        rl_grade = findViewById(R.id.rl_grade);
        rl_signature = findViewById(R.id.rl_signature);
        rl_hobby = findViewById(R.id.rl_hobby);

        tv_nickname = findViewById(R.id.tv_nickname);
        tv_sex = findViewById(R.id.tv_sex);
        tv_birthday = findViewById(R.id.tv_birthday);
        tv_school = findViewById(R.id.tv_school);
        tv_depart = findViewById(R.id.tv_department);
        tv_major = findViewById(R.id.tv_major);
        tv_grade = findViewById(R.id.tv_grade);
        tv_signature = findViewById(R.id.tv_signature);
        tv_hobby = findViewById(R.id.tv_hobby);

        exitBtn = findViewById(R.id.btn_exit);

        rl_nickname.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_birthday.setOnClickListener(this);
        rl_depart.setOnClickListener(this);
        rl_school.setOnClickListener(this);
        rl_major.setOnClickListener(this);
        rl_grade.setOnClickListener(this);
        rl_signature.setOnClickListener(this);
        rl_hobby.setOnClickListener(this);
        head.setOnClickListener(this);
        exitBtn.setOnClickListener(this);

    }

    public void callEdit(final String key, String title , final TextView view){
        final EditText editText = new EditText(this);
        editText.setText(view.getText().toString());
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(title)
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = editText.getText().toString();
                        view.setText(value);
                        Log.d(TAG, "onClick: key--"+key+"  value--"+value);
                        updateUserData(key,value);
                    }
                })
                .setNegativeButton("取消",null)
                .show();
    }

    public void updateFeedback(String msg){
        Snackbar.make(head,msg,Snackbar.LENGTH_SHORT).show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UserCenterActivity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    cutImage(tempUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    cutImage(data.getData()); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     * @param uri 图片地址
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.i(TAG, "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * 保存裁剪之后的图片数据
     * @param data 剪切图片的intent
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
            //这里图片是方形的，可以用一个工具类处理成圆形（很多头像都是圆形，这种工具类网上很多不再详述）
            head.setImageBitmap(mBitmap);//显示图片
            //TODO 头像上传到服务器
            String filePath = PathGetter.getPath(this,tempUri);
            final BmobFile bmobFile = new BmobFile(new File(filePath));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        updateFeedback("头像修改成功~");
                        curUser.setValue("headUrl",bmobFile.getFileUrl());
                        curUser.setHeadUrl(bmobFile.getFileUrl());
                        curUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                Constant.curUser.setHeadUrl(bmobFile.getFileUrl());
                            }
                        });
                    }else {
                        Log.d(TAG, "done: 头像修改失败..."+e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * 更新数据库中的用户信息
     * @param value 属性值
     */
    public void updateUserData(final String key, final String value){

        BmobQuery<UserData> query = new BmobQuery<UserData>();
        query.addWhereEqualTo("userName",tv_nickname.getText());
        query.findObjects(new FindListener<UserData>() {
            @Override
            public void done(List<UserData> list, BmobException e) {
                if(e == null){
                    final UserData data = list.get(0);
                    data.setValue(key,value);
                    String id = data.getId();
                    data.update(id,new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                updateFeedback("修改成功~");

                            }else{
                                updateFeedback("修改失败~");
                            }
                        }
                    });
                }
            }
        });

    }
    @Override
    public void onClick(final View v) {
        switch (v.getId()){
            case R.id.user_head:
                //图片选择  拍照or相册
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择头像");
                builder.setNegativeButton("取消",null);
                String[] strings = new String[]{"选择本地图片","拍照"};
                builder.setItems(strings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case CHOOSE_PICTURE:
                                Intent openAlbumIntent = new Intent(Intent.ACTION_PICK);
                                openAlbumIntent.setType("image/*");
                                startActivityForResult(openAlbumIntent,CHOOSE_PICTURE);
                                break;
                            case TAKE_PICTURE:
                                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                tempUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"temp_head.jpg"));
                                // 将拍照所得的相片保存到SD卡根目录
                                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                                startActivityForResult(openCameraIntent,TAKE_PICTURE);
                                break;
                        }
                    }
                });
                builder.show();
                break;
            case R.id.rl_nickname:
                Snackbar.make(v,"用户名不能更改哦~",Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.rl_sex:
                //弹出性别选择对话框
                final String[] items = new String[] {"男","女"};
                AlertDialog dialog = new AlertDialog.Builder(this).setTitle("性别")
                        .setSingleChoiceItems(items,-1,new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    tv_sex.setText(items[which]);
                    updateUserData("sex",items[which]);
                }
            }).create();
                dialog.show();
                break;
            case R.id.rl_birhday:
                //弹出日期选择框
                Calendar c = Calendar.getInstance();
                //直接创建一个DatePickerDialog对话框实例，并显示出来
                new DatePickerDialog(this,
                    //绑定监听器
                    new DatePickerDialog.OnDateSetListener() {
                      @Override
                      public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                          birthday = year+"年"+(month+1)
                              +"月"+dayOfMonth+"日";
                          tv_birthday.setText(birthday);
                          updateUserData("birthday",birthday);
                      }
                }
                //设置初始日期
                ,c.get(Calendar.YEAR)
                ,c.get(Calendar.MONTH)
                ,c.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.rl_school:
                callEdit("school","学校",tv_school);
                break;
            case R.id.rl_department:
                callEdit("depart","学院",tv_depart);
                break;
            case R.id.rl_major:
                callEdit("major","专业",tv_major);
                break;
            case R.id.rl_grade:
                callEdit("grade","年级",tv_grade);
                break;
            case R.id.rl_signature:
                callEdit("signature","个性签名",tv_signature);
                break;
            case R.id.rl_hobby:
                callEdit("hobby","爱好",tv_hobby);
                break;
            case R.id.btn_exit:
                AlertDialog dialog1 = new AlertDialog.Builder(this).setTitle("退出登录")
                        .setPositiveButton("是的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MainActivity.isLogin = false;
                                //TODO 连接：退出登录需要断开与IM服务器的连接
                                BmobUser.logOut();
                                BmobIM.getInstance().disConnect();
                                finish();
                            }
                        })
                        .setNegativeButton("不",null)
                        .show();
                break;
        }
    }
}
