package com.example.carbazar;

import com.example.carbazar.Models.common;
import com.example.carbazar.Models.contactModel;
import com.example.carbazar.Models.contactSellerModel;
import com.example.carbazar.Models.followModel;
import com.example.carbazar.Models.reportModel;
import com.example.carbazar.Models.saveUnsave;
import com.example.carbazar.Models.unfollowModel;
import com.example.carbazar.Models.user;
import com.google.gson.JsonObject;
import com.mongodb.util.JSON;

import org.json.JSONArray;
import org.json.JSONObject;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IMyService {

    @POST("signup")
    Observable<Object> signupUser(@Body user body);

    @POST("signin")
    Observable<Object> signinUser(@Body user body);

    @PUT("forgot-password")
    Observable<Object> forgotPassword(@Body user body);

    @GET("user/{accountId}")
    Observable<Object> profile(@Header("Authorization") String authKey,
                               @Path("accountId") String accountId);

    @FormUrlEncoded
    @PUT("user/{accountId}")
    Observable<Object> updatePassword(@Header("Authorization") String authKey,
                               @Path("accountId") String accountId,
                                  @Field(value="password",encoded = false) String password);

    @FormUrlEncoded
    @PUT("user/{accountId}")
    Observable<Object> updateProfile(@Header("Authorization") String authKey,
                                      @Path("accountId") String accountId,
                                      @Field(value="name",encoded = false) String name,
                                      @Field(value="password",encoded = false) String password,
                                      @Field(value="phone",encoded = false) String phone);

    @Multipart
    @PUT("user/{accountId}")
    Observable<ResponseBody> updatePhoto(@Header("Authorization") String authKey,
                                         @Path("accountId") String accountId,
                                         @Part MultipartBody.Part image);

    @GET("make/")
    Observable<Object> getMakes();

    @GET("model")
    Observable<Object> getModels(@Query("make") String make);

    @GET("sessionId")
    Observable<Object> getSessionId();

    @GET("ask/83c5ce1b-e823-4fd0-820b-070278b955bb")
    Observable<Object> getChatbotResponse(@Query("make") String make,
                                          @Query("sessionid") String sessionid);

    @GET("b_posts")
    Observable<Object> carBazarBuyersPosts();

    @GET("post/{postId}")
    Observable<Object> getSinglePost(@Path("postId") String postId);

    @GET("posts")
    Observable<Object> carBazarPosts();

    @PUT("savePost/{accountId}")
    Observable<Object> savePost(@Header("Authorization") String authKey,
                                @Path("accountId") String accountId,
                                @Body saveUnsave body);

    @PUT("unsavePost/{accountId}")
    Observable<Object> unsavePost(@Header("Authorization") String authKey,
                                @Path("accountId") String accountId,
                                @Body saveUnsave body);

    @GET("postsSaved/by/{accountId}")
    Observable<Object> getSavedAds(@Header("Authorization") String authKey,
                               @Path("accountId") String accountId);

    @PUT("contactSeller")
    Observable<Object> contactSeller(@Body contactSellerModel body);

    @PUT("user/follow")
    Observable<Object> follow(@Header("Authorization") String authKey,
                              @Body followModel body);

    @PUT("user/unfollow")
    Observable<Object> unfollow(@Header("Authorization") String authKey,
                                @Body unfollowModel body);

    @POST("userAdsNoSetting")
    Observable<Object> userAdsNoSetting(@Body saveUnsave body);

    @PUT("contact-us")
    Observable<Object> contactMessage(@Body contactModel body);

    @Multipart
    @POST("post/new/{accountId}")
    Observable<Object> createPost(@Header("Authorization") String authKey,
                                  @Path("accountId") String accountId,
                                  @Part MultipartBody.Part photo1,
                                  @Part MultipartBody.Part photo2,
                                  @Part MultipartBody.Part photo3,
                                  @Part("title") RequestBody title,
                                  @Part("city") RequestBody city,
                                  @Part("make") RequestBody make,
                                  @Part("model") RequestBody model,
                                  @Part("registration_city") RequestBody registration_city,
                                  @Part("registration_year") RequestBody registration_year,
                                  @Part("mileage") RequestBody mileage,
                                  @Part("exterior_color") RequestBody exterior_color,
                                  @Part("price") RequestBody price,
                                  @Part("body") RequestBody body,
                                  @Part("engine_type") RequestBody engine_type,
                                  @Part("transmission") RequestBody transmission,
                                  @Part("assembly") RequestBody assembly,
                                  @Part("engine_capacity") RequestBody engine_capacity,
                                  @Part("photos") RequestBody photos,
                                  @Part("videos") RequestBody videos,
                                  @Part("condition") RequestBody condition,
                                  @Part MultipartBody.Part video1);

    @PUT("publishPost/{postId}")
    Observable<Object> publishPost(@Header("Authorization") String authKey,
                                   @Path("postId") String postId);

    @Multipart
    @PUT("post/{postId}")
    Observable<Object> updatePost(@Header("Authorization") String authKey,
                                  @Path("postId") String postId,
                                  @Part MultipartBody.Part photo1,
                                  @Part MultipartBody.Part photo2,
                                  @Part MultipartBody.Part photo3,
                                  @Part("title") RequestBody title,
                                  @Part("city") RequestBody city,
                                  @Part("make") RequestBody make,
                                  @Part("model") RequestBody model,
                                  @Part("registration_city") RequestBody registration_city,
                                  @Part("registration_year") RequestBody registration_year,
                                  @Part("mileage") RequestBody mileage,
                                  @Part("exterior_color") RequestBody exterior_color,
                                  @Part("price") RequestBody price,
                                  @Part("body") RequestBody body,
                                  @Part("engine_type") RequestBody engine_type,
                                  @Part("transmission") RequestBody transmission,
                                  @Part("assembly") RequestBody assembly,
                                  @Part("engine_capacity") RequestBody engine_capacity,
                                  @Part("photos") RequestBody photos,
                                  @Part("videos") RequestBody videos,
                                  @Part("condition") RequestBody condition,
                                  @Part MultipartBody.Part video1);



    @Multipart
    @POST("post/newBuyer/{accountId}")
    Observable<Object> createBuyersPost(@Header("Authorization") String authKey,
                                  @Path("accountId") String accountId,
                                  @Part MultipartBody.Part photo1,
                                  @Part("title") RequestBody title,
                                  @Part("city") RequestBody city,
                                  @Part("make") RequestBody make,
                                  @Part("model") RequestBody model,
                                  @Part("registration_year") RequestBody registration_year,
                                  @Part("mileage") RequestBody mileage,
                                  @Part("price") RequestBody price,
                                  @Part("engine_capacity") RequestBody engine_capacity,
                                  @Part("photos") RequestBody photos,
                                  @Part("condition") RequestBody condition);

    @DELETE("post/{postId}")
    Observable<Object> deletePost(@Header("Authorization") String authKey,
                                  @Path("postId") String postId);

    @POST("report")
    Observable<Object> reportUser(@Header("Authorization") String authKey,
                                  @Body reportModel body);

    @GET("searchCarBazaar")
    Observable<Object> searchCarBazaar(@Query("q") String query);

    @GET("CarBazaarFilters")
    Observable<Object> CarBazaarFilters(@Query("min") String price1,
                                        @Query("max") String price2,
                                        @Query("yearmin") String yearmin,
                                        @Query("yearmax") String yearmax,
                                        @Query("KMDriven") String KMDriven,
                                        @Query("engine_capacity") String engineCapacity,
                                        @Query("location") String location,
                                        @Query("make") String make,
                                        @Query("model") String model,
                                        @Query("regCity") String regCity,
                                        @Query("engineType") String engineType,
                                        @Query("condition") String condition,
                                        @Query("transmission") String transmission,
                                        @Query("color") String color,
                                        @Query("query") String query);

    //to Flask API

    @GET("/")
    Observable<Object> OLXHome();

    @GET("search")
    Observable<Object> OLXSearch(@Query("query") String query);

    @GET("olxFilters")
    Observable<Object> olxFilters(@Query("price") String price,
                                  @Query("year") String year,
                                  @Query("KMDriven") String KMDriven,
                                  @Query("engineCapacity") String engineCapacity,
                                  @Query("location") String location,
                                  @Query("make") String make,
                                  @Query("model") String model,
                                  @Query("regCity") String regCity,
                                  @Query("engineType") String engineType,
                                  @Query("condition") String condition,
                                  @Query("transmission") String transmission,
                                  @Query("color") String color,
                                  @Query("query") String query);


    @GET("/pakWheelsHome")
    Observable<Object> PakWheelsHome();

    @GET("pakWheelsSearch")
    Observable<Object> PakWheelsSearch(@Query("query") String query);

    @GET("pakWheelsFilters")
    Observable<Object> pakWheelsFilters(@Query("price") String price,
                                  @Query("year") String year,
                                  @Query("KMDriven") String KMDriven,
                                  @Query("engineCapacity") String engineCapacity,
                                  @Query("location") String location,
                                  @Query("make") String make,
                                  @Query("model") String model,
                                  @Query("regCity") String regCity,
                                  @Query("engineType") String engineType,
                                  @Query("condition") String condition,
                                  @Query("transmission") String transmission,
                                  @Query("color") String color,
                                  @Query("query") String query);


    @GET("/pkMotorsHome")
    Observable<Object> PKMotorsHome();

    @GET("pkMotorsSearch")
    Observable<Object> PKMotorsSearch(@Query("query") String query);

    @GET("pkMotorsFilters")
    Observable<Object> pkMotorsFilters(@Query("price") String price,
                                  @Query("year") String year,
                                  @Query("KMDriven") String KMDriven,
                                  @Query("engineCapacity") String engineCapacity,
                                  @Query("location") String location,
                                  @Query("make") String make,
                                  @Query("model") String model,
                                  @Query("regCity") String regCity,
                                  @Query("engineType") String engineType,
                                  @Query("condition") String condition,
                                  @Query("transmission") String transmission,
                                  @Query("color") String color,
                                  @Query("query") String query);

}
