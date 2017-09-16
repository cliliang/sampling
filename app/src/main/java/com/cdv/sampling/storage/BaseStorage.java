package com.cdv.sampling.storage;

import android.app.Application;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Set;


/**    
 * @Description: 数据存储中心基类
 * @date 20141014
 */
public abstract class BaseStorage {

	protected SharedPreferences mSharedPreference = null;
	protected Application mApplication;
	protected String mStorageFileName = "";
	
	public BaseStorage(Application application) {
		this.mApplication = application;
		init();
	}

	protected void init(){
		mStorageFileName = getStorageFileName();
		mSharedPreference = mApplication.getSharedPreferences(mStorageFileName, 0 );
	}

	
	/**
	 * 根据key值取对应的SharedPreference存储文件，不存在则自动新建
	 * 可以规范缓存文件命名：cache_game_jid_subname
	 */
	public abstract String getStorageFileName();

	public boolean storeBooleanValue(String key, boolean value) {
		return mSharedPreference.edit().putBoolean(key, value).commit();		
	}
	
	public boolean storeIntValue(String key, int value) {
		return mSharedPreference.edit().putInt(key, value).commit();		
	}

	public boolean storeLongValue(String key, long value) {
		return mSharedPreference.edit().putLong(key, value).commit();
	}

	public boolean storeStringValue(String key, String value) {
		return mSharedPreference.edit().putString(key, value).commit();
	}

	public boolean storeSetValue(String key, Set<String> value){
		return mSharedPreference.edit().putStringSet(key,value).commit();
	}
	
	public String getStringValue(String key, String defValue){
		if(mSharedPreference != null){
			return mSharedPreference.getString(key, defValue);
		}else{
			return defValue;
		}
	}
	
	public Boolean getBooleanValue(String key, Boolean defValue){
		if(mSharedPreference != null){
			return mSharedPreference.getBoolean(key, defValue);
		}else{
			return defValue;
		}
	}
	
	public int getIntValue(String key, int defValue){
		if(mSharedPreference != null){
			return mSharedPreference.getInt(key, defValue);
		}else{
			return defValue;
		}
	}

	public long getLongValue(String key, long defValue){
		if(mSharedPreference != null){
			return mSharedPreference.getLong(key, defValue);
		}else{
			return defValue;
		}
	}

	public Set<String> getSetValue(String key,Set<String> defValue){
		if(mSharedPreference != null){
			return mSharedPreference.getStringSet(key,defValue);
		}else{
			return defValue;
		}
	}

	public boolean clearAll(){
		return mSharedPreference.edit().clear().commit();		
	}


	public <T> void setObjectForKey(String key, T obj){
		if (obj == null){
			storeStringValue(key, null);
		}else{
			Gson gson = new Gson();
			String data = gson.toJson(obj);
			storeStringValue(key, data);
		}
	}

	public <T> T getObjectForKey(String key, Class<T> clazz){
		String data = getStringValue(key, null);
		if (!TextUtils.isEmpty(data)){
			Gson gson = new Gson();
			return gson.fromJson(data, clazz);
		}else {
			return null;
		}
	}

	public <T> List<T> getObjectListForKey(String key, TypeToken<List<T>> typeToken){
		String data = getStringValue(key, null);
		if (!TextUtils.isEmpty(data)){
			Gson gson = new Gson();
			return gson.fromJson(data, typeToken.getType());
		}else {
			return null;
		}
	}

	public void remove(String key){
		if (TextUtils.isEmpty(key)){
			return;
		}
		mSharedPreference.edit().remove(key).commit();
	}
}
