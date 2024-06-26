package cat.insVidreres.socialpostphone.imp.api

import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Email
import cat.insVidreres.socialpostphone.imp.entity.User
import cat.insVidreres.socialpostphone.imp.profile.UpdatePFPRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Query
import java.util.Objects


interface UserService {
 /*   @POST("users/login")
    fun login(@Body user: User): Call<JsonResponse>*/

    @POST("users/login")
    fun login(@Header("idToken") token: String, @Body user: User): Call<JsonResponse>

    @POST("users")
    fun register(@Body user: User): Call<JsonResponse>

    @GET("user")
    fun getUserDetails(@Header("idToken") token: String, @Query("email") email: String): Call<JsonResponse>

    @GET("user/friends")
    fun getUserFriends(@Header("idToken") token: String, @Query("email") email: String): Call<JsonResponse>

    @GET("users")
    fun getAllUsers(@Header("idToken") token: String): Call<JsonResponse>

    @PUT("user/pfp")
    fun updateUserPFP(@Header("idToken") token: String, @Body body: UpdatePFPRequest): Call<JsonResponse>

    @POST("user/friends")
    fun addFriendToUser(@Header("idToken") token: String, @Query("email") email: String, @Body user: User): Call<JsonResponse>
    @DELETE("user/friends")
    fun deleteFriendToUser(@Header("idToken") token: String, @Query("email") email: String, @Query("friendEmail") friendEmail: String): Call<JsonResponse>
    @POST("user/follow")
    fun addFollower(@Header("idToken") token: String, @Query("email") email: String, @Body user: User): Call<JsonResponse>
    @DELETE("user/follow")
    fun deleteFollower(@Header("idToken") token: String, @Query("email") email: String, @Query("userEmail") userEmail: String): Call<JsonResponse>

}