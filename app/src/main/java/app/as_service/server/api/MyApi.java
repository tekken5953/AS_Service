package app.as_service.server.api;

import java.util.List;

import app.as_service.dao.AdapterModel;
import app.as_service.dao.ApiModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MyApi {
    // 회원가입 API
    @POST("register/")
    Call<ApiModel.ReturnPost> postSignUp(@Body ApiModel.Member item);

    // 로그인 API
    @POST("login/")
    Call<ApiModel.AccessToken> postUsers(@Body ApiModel.Login item);

    // 장치추가 API
    @POST("device/")
    Call<ApiModel.ReturnPost> postAddDevice(@Header("Authorization") String token, @Body ApiModel.AddDevice item);

    // 장치리스트 검색
    @GET("device/")
    Call<List<AdapterModel.GetDeviceList>> getDeviceList(@Header("Authorization") String token);

    // 내 정보 확인
    @GET("user/")
    Call<ApiModel.GetMyInfo> getMyInfo(@Header("Authorization") String token);

    // 내 비밀번호 수정
    @PUT("user/")
    Call<ApiModel.ReturnPost> putMyPassword(@Header("Authorization") String token, @Body ApiModel.PutMyPassword item);

    // 내 이메일 수정
    @PUT("user/")
    Call<ApiModel.ReturnPost> putMyEmail(@Header("Authorization") String token,@Body ApiModel.PutMyEmail item);

    // 데이터 정보 불러오기
    @GET("analytics/{device}")
    Call<ApiModel.GetData> getData(@Path("device") String device, @Header("Authorization") String token);
}
