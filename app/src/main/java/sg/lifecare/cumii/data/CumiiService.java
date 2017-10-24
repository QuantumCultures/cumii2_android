package sg.lifecare.cumii.data;


import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import sg.lifecare.cumii.BuildConfig;
import sg.lifecare.cumii.CumiiConfig;
import sg.lifecare.cumii.data.server.data.AcknowledgeData;
import sg.lifecare.cumii.data.server.data.AlertRuleData;
import sg.lifecare.cumii.data.server.response.AcknowledgeResponse;
import sg.lifecare.cumii.data.server.response.ActivityDataResponse;
import sg.lifecare.cumii.data.server.response.ActivityStatisticResponse;
import sg.lifecare.cumii.data.server.response.AggregatedActivityResponse;
import sg.lifecare.cumii.data.server.response.AssistsedEntityResponse;
import sg.lifecare.cumii.data.server.response.BathroomStatisticResponse;
import sg.lifecare.cumii.data.server.response.ConnectedDeviceResponse;
import sg.lifecare.cumii.data.server.response.EditRuleResponse;
import sg.lifecare.cumii.data.server.response.EnergyConsumptionResponse;
import sg.lifecare.cumii.data.server.response.EntityDetailResponse;
import sg.lifecare.cumii.data.server.response.LoginResponse;
import sg.lifecare.cumii.data.server.response.LogoutResponse;
import sg.lifecare.cumii.data.server.response.RelatedAlertMessageResponse;
import sg.lifecare.cumii.data.server.response.ResetPasswordResponse;
import sg.lifecare.cumii.data.server.response.SleepMedianResponse;
import sg.lifecare.cumii.data.server.response.WakeupMedianResponse;
import sg.lifecare.cumii.data.server.response.WaterConsumptionResponse;
import sg.lifecare.cumii.util.CookieUtils;

/**
 * Server APIs
 */
public interface CumiiService {

    /**
     * Factory class that sets up new service
     */
    class Factory {

        public static CumiiService makeLifecareService(Context context) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(BuildConfig.DEBUG ?
                    HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(CookieUtils.getCookieJar(context))
                    .addInterceptor(new HttpInterceptor())
                    .addNetworkInterceptor(new StethoInterceptor())
                    .addInterceptor(logging)
                    .build();

            Gson gson = new GsonBuilder()
                    .serializeNulls()
                    .registerTypeAdapter(Date.class,
                            (JsonDeserializer<Date>) (jsonElement, type, jsonDeserializationContext) -> {
                                if (jsonElement == null) {
                                    return null;
                                }

                                DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                return formatter.parseDateTime(jsonElement.getAsString()).toDate();
                            })
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
                    .create();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CumiiConfig.SERVER_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            return retrofit.create(CumiiService.class);

        }
    }

    @FormUrlEncoded
    @POST("mlifecare/authentication/appLogin")
    Observable<LoginResponse> login(@Field("AuthenticationString") String username,
            @Field("Password") String password,
            @Field("Token") String fcm,
            @Field("DeviceId") String deviceId,
            @Field("DeviceType") String deviceType);


    @FormUrlEncoded
    @POST("mlifecare/authentication/appLogout")
    Observable<LogoutResponse> logout(@Field("DeviceId") String deviceId);

    @FormUrlEncoded
    @POST("mlifecare/authentication/forgotPasswordRequest")
    Observable<ResetPasswordResponse> resetPassword(@Field("AuthenticationString") String email);

    @GET("mlifecare/entity/getEntityDetail")
    Observable<EntityDetailResponse> getEntityDetail(@Query("EntityId") String entityId);

    @GET("mlifecare/entityRelationship/getAssisted")
    Observable<AssistsedEntityResponse> getAsisteds(@Query("CaregiverId") String entityId);

    @GET("/mlifecare/message/getRelatedAlertMessages")
    Observable<RelatedAlertMessageResponse> getRelatedAlertMessages(@Query("EntityId")String entityId,
            @Query("PageSize")int pageSize,
            @Query("SkipSize")int skipSize);

    @GET("/matthings/device/getConnectedGateways")
    Observable<ConnectedDeviceResponse> getConnectedGateways(@Query("EntityId")String entityId);

    @GET("/matthings/device/getConnectedSmartDevices")
    Observable<ConnectedDeviceResponse> getConnectedSmartDevices(@Query("EntityId")String entityId);

    @GET("/matthings/device/getConnectedSmartDevices?ProductClass=56332d8be4b0292685f753c0")
    Observable<ConnectedDeviceResponse> getConnectedCameras(@Query("EntityId")String entityId,
            @Query("GatewayId")String gatewayId);

    @GET("/mlifecare/event/getActivityData")
    Observable<ActivityDataResponse> getActivitiesData(@Query("EntityId")String entityId,
            @Query("PageSize")int pageSize,
            @Query("SkipSize")int skipSize);

    @GET("/mlifecare/event/getAggregatedActivityCount")
    Observable<AggregatedActivityResponse> getAggregatedActivity(@Query("EntityId") String entityId,
            @Query("StartDateTime") String start,
            @Query("EndDateTime") String end/*, @Query("SortHourly") boolean sortHourly*/);

    @GET("/mlifecare/informativeData/getMedianWakeupAnalytics")
    Observable<WakeupMedianResponse> getMedianWakeup(@Query("EntityId") String entityId,
            @Query("Day") String day);

    @GET("/mlifecare/informativeData/getMedianSleepAnalytics")
    Observable<SleepMedianResponse> getMedianSleep(@Query("EntityId") String entityId,
            @Query("Day") String day);

    @GET("/mlifecare/informativeData/getActivityStatisticalAnalytics")
    Observable<ActivityStatisticResponse> getActivityStatistic(@Query("EntityId") String entityId,
            @Query("Day") String day);

    @GET("/mlifecare/informativeData/getBathroomStatisticalAnalytics")
    Observable<BathroomStatisticResponse> getBathroomStatistic(@Query("EntityId") String entityId,
            @Query("Day") String day);

    @GET("/matthings/event/getEnergyConsumptionAnalytics")
    Observable<EnergyConsumptionResponse> getEnergyConsumption(@Query("EntityId") String entityId,
            @Query("Day") String day);

    @GET("/matthings/event/getWaterConsumptionAnalytics")
    Observable<WaterConsumptionResponse> getWaterConsumption(@Query("EntityId") String entityId,
            @Query("Day") String day);

    @POST("/mlifecare/rule/acknowledgeRule")
    Observable<AcknowledgeResponse> postAcknowledge(@Body AcknowledgeData post);

    @POST("/mlifecare/rule/editRule")
    Observable<EditRuleResponse> postEditAlertRule(@Body AlertRuleData post);

}
