package com.example.a1712390_1712518;

import com.example.a1712390_1712518.pojo.AddStopPointRequest;
import com.example.a1712390_1712518.pojo.CreateTourObj;
import com.example.a1712390_1712518.pojo.ListReviewRequest;
import com.example.a1712390_1712518.pojo.ListTourResponse;
import com.example.a1712390_1712518.pojo.LoginRequest;
import com.example.a1712390_1712518.pojo.LoginResponse;
import com.example.a1712390_1712518.pojo.Message;
import com.example.a1712390_1712518.pojo.ReviewRequest;
import com.example.a1712390_1712518.pojo.SignUpRequest;
import com.example.a1712390_1712518.pojo.SignUpResponse;
import com.example.a1712390_1712518.pojo.StopPointListRequest;
import com.example.a1712390_1712518.pojo.StopPointListResponse;
import com.example.a1712390_1712518.pojo.StopPointObj;
import com.example.a1712390_1712518.pojo.StopPointViewObject;
import com.example.a1712390_1712518.pojo.Tour;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserService {
    @POST("user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);
    @POST("user/register")
    Call<SignUpResponse> signup(@Body SignUpRequest signUpRequest);
    @GET("tour/list")
    Call<ListTourResponse> getListTour(@Header("Authorization") String token,
                                       @Query("rowPerPage") Integer row,
                                       @Query("pageNum") Integer pageNum,
                                       @Query("orderBy")String ord,
                                       @Query("isDesc")Boolean isDesc);
    @POST("tour/create")
    Call<CreateTourObj> CreateTour(@Header("Authorization") String token,
                                   @Body CreateTourObj createTourObj);

    @GET("tour/info")
    Call<Tour> getTourInfo(@Header("Authorization") String token,@Query("tourId") Integer id);

    @POST("tour/suggested-destination-list")
    Call<StopPointListResponse> getStopPointsInArea(@Header("Authorization") String token,
                                                    @Body StopPointListRequest request);
    @POST("tour/set-stop-points")
    Call<Message> addStopPointsToTour(@Header("Authorization")String token,
                                                 @Body AddStopPointRequest request);
    @GET("tour/get/service-detail")
    Call<StopPointViewObject> getServiceDetail(@Header("Authorization")String token,
                                        @Query("serviceId")Integer Id);
    @POST("tour/add/feedback-service")
    Call<Message> sendServiceReview(@Header("Authorization")String token,
                                    @Body ReviewRequest request);
    @POST("tour/add/review")
    Call<Message> sendTourReview(@Header("Authorization")String token,
                                 @Body ReviewRequest request);
    @GET("tour/get/feedback-service")
    Call<ListReviewRequest> getServiceReviews(@Header("Authorization")String token,
                                              @Query("serviceId") Integer id,
                                              @Query("pageIndex")Integer page,
                                              @Query("pageSize")Integer pageSize);
    @GET("tour/get/review-list")
    Call<ListReviewRequest> getTourReviews(@Header("Authorization")String token,
                                              @Query("tourId") Integer id,
                                              @Query("pageIndex")Integer page,
                                              @Query("pageSize")Integer pageSize);
}
