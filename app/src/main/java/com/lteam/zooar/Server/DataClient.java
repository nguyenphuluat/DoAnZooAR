package com.lteam.zooar.Server;

import com.lteam.zooar.Model.Animal;
import com.lteam.zooar.Model.User;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface DataClient {
    //get model
    @FormUrlEncoded
    @POST("php/getmodel.php")
    Call<ArrayList<Animal>> getModel(@Field("level") String level);

    //get model move
    @FormUrlEncoded
    @POST("php/getModelMove.php")
    Call<ArrayList<Animal>> getModelMove(@Field("level") String level);

    //get all user
    @GET("php/getAllUser.php")
    Call<ArrayList<User>> getAllUser();

    //sort model
    @FormUrlEncoded
    @POST("php/sort.php")
    Call<ArrayList<Animal>> sortModel(@Field("kieu") String kieu,@Field("iduser") String idUser);

    // update view
    @FormUrlEncoded
    @POST("php/model.php")
    Call<String> model(@Field("iduser") String iduser,@Field("idmodel") String idmodel);

    // login. trả về 1 User nếu đúng tài khoản mật khẩu . fail nếu sai
    @FormUrlEncoded
    @POST("php/login.php")
    Call<User> login(@Field("username") String username,@Field("password")String password);

    // đổi mật khẩu
    @FormUrlEncoded
    @POST("php/changepassword.php")
    Call<String> changePass(@Field("password") String pass,@Field("iduser") String iduser);

    // đổi anh dai dien
    @FormUrlEncoded
    @POST("php/changeAvatar.php")
    Call<String> changeAvatar(@Field("path") String path,@Field("iduser") String iduser);

    // đăng kí tài khoản
    @FormUrlEncoded
    @POST("php/regist.php")
    Call<String> regist(@Field("username") String username
            ,@Field("password") String password
            ,@Field("urlavatar") String urlAvatar
            ,@Field("displayname") String displayname
            ,@Field("email") String email
            ,@Field("phone") String phone);

    // ban user
    @FormUrlEncoded
    @POST("php/ban.php")
    Call<String> banUser(@Field("iduser") String idUser
            ,@Field("status") int status);

    // ban user
    @FormUrlEncoded
    @POST("php/banModel.php")
    Call<String> banModel(@Field("idmodel") String idModel
            ,@Field("status") int status);

    //up hình ảnh lên server
    @Multipart
    @POST("php/photoupload.php")
    Call<String> uploadImageAvatar(@Part MultipartBody.Part photo);

    // xóa ảnh qua url
    @FormUrlEncoded
    @POST("php/unlink.php")
    Call<String> unlink(@Field("link") String link);

    // quên mật khẩu
    @FormUrlEncoded
    @POST("php/forgotpassword.php")
    Call<String> getPassword(@Field("username") String username
            ,@Field("email") String email);
}
