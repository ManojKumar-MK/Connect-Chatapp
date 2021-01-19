package developermk.chatapp.Notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAm15APxg:APA91bEEfx26iq7-pWXVuZa_oFjpOztdamopguKolQOsIuNb9BLEzfndYXoTC73qw36tzp1oZCymsFNgUw3nAlp7niR3Y9H6drwdkIjL04rkt6k--2PbSdrwdh1SnDcZ-art7OrMBG4k"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
