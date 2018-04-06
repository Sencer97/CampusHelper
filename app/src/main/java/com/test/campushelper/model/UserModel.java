package com.test.campushelper.model;


import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.test.campushelper.activity.MainActivity;
import com.test.campushelper.listener.QueryUserListener;
import com.test.campushelper.listener.UpdateCacheListener;
import com.test.campushelper.utils.Constant;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.BmobListener1;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static cn.bmob.v3.BmobUser.getCurrentUser;

public class UserModel extends BaseModel {
    private static final String TAG = "UserModel";

    private static UserModel ourInstance = new UserModel();

    public static UserModel getInstance() {
        return ourInstance;
    }

    private UserModel() {
    }

    /**
     * TODO 注册
     * @param userName
     * @param pwd
     * @param confirmPwd
     * @param et_user
     * @param et_password
     * @param et_confirm_pwd
     * @param listener
     */
    public void register(final String userName,final String pwd,final String confirmPwd,
                         EditText et_user,EditText et_password,EditText et_confirm_pwd,
                         final LogInListener listener) {
        if (TextUtils.isEmpty(userName)) {
            et_user.setError("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            et_password.setError("密码不能为空");
            return;
        }
        if (!pwd.equals(confirmPwd)) {
            et_confirm_pwd.setError("密码不一致！");
            et_confirm_pwd.setText("");
            return;
        }
            final User user = new User();
            user.setUsername(userName);
            user.setPassword(pwd);
            user.signUp(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        Log.d(TAG, "注册成功~");
                        String userID = user.getObjectId();
                        user.setValue("id", userID);
                        user.update(userID, new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                            }
                        });
                        //创建账户的同时 创建个人信息数据对象
                        final UserData userData = new UserData("",userName,"","","","",""
                                ,"","","",new ArrayList<Friend>());
                        userData.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if(e == null){
                                    String userDataID = userData.getObjectId();
                                    userData.setValue("id",userDataID);
                                    userData.update(userDataID, new UpdateListener() {
                                        @Override
                                        public void done(BmobException e) {
                                        }
                                    });
                                }
                            }
                        });
                        listener.done(null, null);
                    } else {
                        Log.d(TAG, "done: ");
                        listener.done(null, e);
                    }
                }
            });

        }


    /**
     * TODO 登录
     *
     * @param username
     * @param password
     * @param listener
     */
    public void login(final String  username, String password, EditText et_user,EditText et_password,final LogInListener listener) {

        if(username.equals("")){
            et_user.setError("用户名不能为空");
        }
        if (password.equals("")){
            et_password.setError("密码不能为空");
        }
            final User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.login(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        listener.done(getCurrentUser(), null);    //"登录成功！2s后返回主界面"
                    } else {
                        listener.done(user, e);
                    }
                }
            });
    }

    /**
     * TODO  退出登录
     */
    public void logout() {
        BmobUser.logOut();
    }

    /**
     * TODO  获取当前用户
     *
     * @return
     */
    public User getCurrentUser() {
        return BmobUser.getCurrentUser(User.class);
    }
    /**
     * TODO 用户管理：2.6、查询指定用户信息
     *
     * @param objectId
     * @param listener
     */
    public void queryUserInfo(String objectId, final QueryUserListener listener) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("objectId", objectId);
        query.findObjects(
                new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {

                            if (list != null && list.size() > 0) {
                                listener.done(list.get(0), null);
                            } else {
                                listener.done(null, new BmobException(000, "查无此人"));
                            }
                        } else {
                            listener.done(null, e);
                        }
                    }
                });
    }
    /**
     * 更新用户资料和会话资料
     *
     * @param event
     * @param listener
     */
    public void updateUserInfo(MessageEvent event, final UpdateCacheListener listener) {
        final BmobIMConversation conversation = event.getConversation();
        final BmobIMUserInfo info = event.getFromUserInfo();
        final BmobIMMessage msg = event.getMessage();
        String username = info.getName();
        String avatar = info.getAvatar();
        String title = conversation.getConversationTitle();
        String icon = conversation.getConversationIcon();
        //SDK内部将新会话的会话标题用objectId表示，因此需要比对用户名和私聊会话标题，后续会根据会话类型进行判断
        if (!username.equals(title) || (avatar != null && !avatar.equals(icon))) {
            UserModel.getInstance().queryUserInfo(info.getUserId(), new QueryUserListener() {
                @Override
                public void done(User s, BmobException e) {
                    if (e == null) {
                        String name = s.getUsername();
                        String avatar = s.getAvatar();
                        conversation.setConversationIcon(avatar);
                        conversation.setConversationTitle(name);
                        info.setName(name);
                        info.setAvatar(avatar);
                        //TODO 用户管理：2.7、更新用户资料，用于在会话页面、聊天页面以及个人信息页面显示
                        BmobIM.getInstance().updateUserInfo(info);
                        //TODO 会话：4.7、更新会话资料-如果消息是暂态消息，则不更新会话资料
                        if (!msg.isTransient()) {
                            BmobIM.getInstance().updateConversation(conversation);
                        }
                    } else {
                    }
                    listener.done(null);
                }
            });
        } else {
            listener.done(null);
        }
    }



}
