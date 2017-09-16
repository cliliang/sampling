package com.cdv.sampling.repository;

import android.app.Application;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.cdv.sampling.SamplingApplication;
import com.cdv.sampling.bean.UserBean;
import com.cdv.sampling.constants.IntentConstants;
import com.cdv.sampling.constants.StorageConstants;
import com.cdv.sampling.net.HttpService;
import com.cdv.sampling.net.OperatorRequestMap;
import com.cdv.sampling.storage.SamplingStorage;
import com.cdv.sampling.utils.AppUtils;

import java.util.List;

import rx.Observable;

/**
 * Created by apple on 16/3/17.
 */
public class UserRepository {

    private static final String KEY_TOKEN = "KEY_TOKEN";

    private Application application;
    private UserBean currentUser;

    private String token;

    private UserRepository() {
        application = SamplingApplication.getInstance();
    }

    public static UserRepository getInstance() {
        return UserRepositoryHandler.INSTANCE;
    }

    private static class UserRepositoryHandler {
        public static final UserRepository INSTANCE = new UserRepository();
    }

    public UserBean getCurrentUser() {
        if (currentUser == null){
            currentUser = SamplingStorage.getInstance().getObjectForKey(StorageConstants.KEY_CURRENT_USER, UserBean.class);
        }
        return currentUser;
    }

    public static boolean isAdmin(){
        return getInstance().getCurrentUser() != null && UserBean.ROLE_ADMIN.equals(getInstance().getCurrentUser().getRole());
    }

    public static boolean isLogin(){
        return UserRepository.getInstance().getCurrentUser() != null;
    }

    public String getToken() {
        if (TextUtils.isEmpty(token)){
            token = SamplingStorage.getInstance().getStringValue(KEY_TOKEN, null);
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        SamplingStorage.getInstance().storeStringValue(KEY_TOKEN, token);
    }

    public static void logOut(){
        getInstance().setToken(null);
        SamplingStorage.getInstance().setObjectForKey(StorageConstants.KEY_CURRENT_USER, null);
        getInstance().currentUser = null;
    }

    public static void setUser(UserBean user){
        UserRepository.getInstance().currentUser = user;
        SamplingStorage.getInstance().setObjectForKey(StorageConstants.KEY_CURRENT_USER, user);
        AppUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocalBroadcastManager.getInstance(SamplingApplication.getInstance()).sendBroadcast(new Intent(IntentConstants.ACTION_LOGIN_USER_CHANGED));
            }
        });
    }

    public static Observable<List<UserBean>> searchUserList(String keyword){
        if (TextUtils.isEmpty(keyword)){

        }
        return HttpService.getApi().getAllUser().lift(new OperatorRequestMap<List<UserBean>>());
    }

    public static String getUserFileDir(){
        if (isLogin()){
            return getInstance().getCurrentUser().getID();
        }else{
            return "0";
        }
    }
}