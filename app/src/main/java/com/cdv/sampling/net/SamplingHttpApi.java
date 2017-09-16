package com.cdv.sampling.net;

import com.cdv.sampling.bean.BaseBean;
import com.cdv.sampling.bean.ClientUnit;
import com.cdv.sampling.bean.FormBean;
import com.cdv.sampling.bean.NameList;
import com.cdv.sampling.bean.ShouYaoCanLiuSample;
import com.cdv.sampling.bean.UserBean;
import com.cdv.sampling.bean.VersionBean;
import com.cdv.sampling.bean.YangPinHeShi;
import com.cdv.sampling.bean.ZhiLiangChouYang;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface SamplingHttpApi {

    @FormUrlEncoded
    @POST("/api/Account/Login")
    Observable<BaseBean<UserBean>> login(@Field("account") String account, @Field("password") String password);

    @GET("/api/User/GetAll")
    Observable<BaseBean<List<UserBean>>> getAllUser();

    @POST("/api/User/Create")
    Observable<BaseBean<UserBean>> createUser(@Body UserBean userBean);

    @POST("/api/Account/Logout")
    Observable<Object> logOut();

    @FormUrlEncoded
    @POST("/api/User/Edit/{id}")
    Observable<BaseBean<UserBean>> modify(@Path("id") String id, @Field("UserName") String UserName, @Field("Telephone") String Telephone, @Field("Password") String Password);

    @POST("/api/User/Delete/{id}")
    Observable<BaseBean<UserBean>> deleteUser(@Path("id") String id);

    @FormUrlEncoded
    @POST("/api/ClientUnit/Search")
    Observable<BaseBean<List<ClientUnit>>> searchClientUnit(@Field("Name") String Name, @Field("Code") String Code, @Field("ShortName") String ShortName);

    @POST("/api/ClientUnit/Edit")
    Observable<BaseBean<VersionBean>> modifyClientUnit(@Body ClientUnit clientUnit);

    @POST("/api/SampleSourceType/Create")
    Observable<BaseBean<String>> createSampleSource(@Query("name") String name);

    @POST("/api/SampleSourceType/Edit/{id}")
    Observable<BaseBean<String>> modifySampleSource(@Query("id") String Id, @Path("name") String name);

    @POST("/api/SampleName/Create")
    Observable<BaseBean<String>> createSampleName(@Query("name") String name);

    @POST("/api/SampleName/Edit/{id}")
    Observable<BaseBean<String>> modifySampleName(@Path("id") String Id, @Query("name") String name);

    @DELETE("/api/ClientUnit/Delete/{id}")
    Observable<BaseBean<String>> deleteClientUnit(@Path("id") int id);

    @POST("/api/SysService/SendMail/{id}")
    Observable<BaseBean<String>> sendEmailFormJianCeDan(@Path("id") String id, @Query("email") String email);

    @Multipart
    @POST("/api/File/Upload")
    Observable<BaseBean<String>> uploadFiles(@PartMap() Map<String,RequestBody> mapFileAndName, @Query("fileType") int fileType);

    @Multipart
    @POST("/api/File/Upload")
    Call<BaseBean<String>> uploadFilesSync(@PartMap() Map<String,RequestBody> mapFileAndName, @Query("fileType") int fileType);

    @POST("/api/ShouYao/Create")
    Observable<BaseBean<FormBean<ShouYaoCanLiuSample>>> uploadShouYaoForm(@Body FormBean<ShouYaoCanLiuSample> formBean);

    @POST("/api/ZhiLiang/Create")
    Observable<BaseBean<FormBean<ZhiLiangChouYang>>> uploadZhiLiangChouYangForm(@Body FormBean<ZhiLiangChouYang> formBean);

    @POST("/api/YangPin/Create")
    Observable<BaseBean<FormBean<YangPinHeShi>>> uploadYangPinHeShi(@Body FormBean<YangPinHeShi> formBean);

    @GET("/api/ShouYaoJinHuoType/Search")
    Observable<BaseBean<List<NameList>>> searchShouYaoJinHuoType();

    @GET("/api/ZhiLiangJinHuoType/Search")
    Observable<BaseBean<List<NameList>>> searchZhiLiangJinHuoType();

    @GET("/api/ZhiLiangSampleType/Search")
    Observable<BaseBean<List<NameList>>> searchZhiLiangSampleType();

    @GET("/api/ShouYaoSampleType/Search")
    Observable<BaseBean<List<NameList>>> searchShouYaoSampleType();

    @GET("/api/SampleName/Search")
    Observable<BaseBean<List<NameList>>> searchSampleName();

    @POST("/api/ClientUnit/Edit")
    Observable<BaseBean<List<ClientUnit>>> modifyClientUnitList(@Body List<ClientUnit> list);

    @GET("/api/ClientUnit/GetLatest")
    Observable<BaseBean<List<ClientUnit>>> getUpdateClientUnitList(@Query("lastModifyTime") String lastModifyTime);

    @FormUrlEncoded
    @POST("/api/Account/ChangeInfo")
    Observable<BaseBean<UserBean>> changeInfo(@Field("ID") String ID, @Field("Password") String Password, @Field("Telephone") String Telephone);
}
