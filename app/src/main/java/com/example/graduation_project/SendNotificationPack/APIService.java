package com.example.graduation_project.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAYLJkEFY:APA91bFvXxyuswmveQeY4IS2iTJkFRqXSOHp2zClmmQA3hACPZG5cTBPMET1Pe1zbzD8rrXYc1WWNDUR6PIm0up-0XN9VQCmOgnTclkn38zlVTKQ3twqzyD_5goUbr6EHcaenHT1GeGZ"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifiction(@Body NotificationSender body);
}
