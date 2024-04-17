package cat.insVidreres.socialpostphone.imp.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.PUT

interface PostService {
    @GET("posts")
    fun getPosts(@Header("idToken") token: String): Call<JsonResponse>

    @POST("posts")
    fun uploadPost(@Header("idToken") token: String, @Body post: POST): Call<JsonResponse>

    @PUT
    fun uploadComment(@Header("idToken") token: String, @Body)
}