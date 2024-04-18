package cat.insVidreres.socialpostphone.imp.api

import cat.insVidreres.socialpostphone.imp.entity.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


interface UserService {
 /*   @POST("users/login")
    fun login(@Body user: User): Call<JsonResponse>*/

    @POST("users/login")
    fun login(@Header("idToken") token: String): Call<JsonResponse>

    @POST("users")
    fun register(@Body user: User): Call<JsonResponse>

    @GET("user")
    fun getUserDetails(@Header("idToken") token: String, @Query("email") email: String): Call<JsonResponse>
}