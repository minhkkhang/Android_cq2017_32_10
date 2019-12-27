package com.ygaps.travelapp;

import com.ygaps.travelapp.pojo.AddStopPointRequest;
import com.ygaps.travelapp.pojo.CreateTourObj;
import com.ygaps.travelapp.pojo.FireBaseTokenRequest;
import com.ygaps.travelapp.pojo.ListReviewRequest;
import com.ygaps.travelapp.pojo.ListTourResponse;
import com.ygaps.travelapp.pojo.LoginRequest;
import com.ygaps.travelapp.pojo.LoginResponse;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.ReviewRequest;
import com.ygaps.travelapp.pojo.SignUpRequest;
import com.ygaps.travelapp.pojo.SignUpResponse;
import com.ygaps.travelapp.pojo.StopPointListRequest;
import com.ygaps.travelapp.pojo.StopPointListResponse;
import com.ygaps.travelapp.pojo.StopPointViewObject;
import com.ygaps.travelapp.pojo.Tour;

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
    @GET("tour/comment-list")
    Call<ListReviewRequest> getTourComments(@Header("Authorization")String token,
                                            @Query("tourId") Integer id,
                                            @Query("pageIndex")Integer page,
                                            @Query("pageSize")Integer pageSize);
    @POST("tour/comment")
    Call<Message> sendTourComment(@Header("Authorization")String token,
                                  @Body ReviewRequest request);
    @POST("tour/add/member")
    Call<Message> inviteMember(@Header("Authorization")String token,
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
    @POST("user/notification/put-token")
    Call<Message> registerFirebaseToken(@Header("Authorization")String token,
                                        @Body FireBaseTokenRequest request);
    @POST("tour/clone")
    Call<Tour> cloneTour(@Header("Authorization")String token,
                         @Body ReviewRequest request);
    @POST("tour/update-tour")
    Call<CreateTourObj> updateTour(@Header("Authorization")String token,
                          @Body CreateTourObj tour);
}
