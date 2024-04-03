package cat.insVidreres.socialpostphone.imp.api

import cat.insVidreres.socialpostphone.imp.entity.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("users/login")
    fun login(@Body user: User): Call<JsonResponse>

    @POST("users")
    fun register(@Body user: User): Call<JsonResponse>
}