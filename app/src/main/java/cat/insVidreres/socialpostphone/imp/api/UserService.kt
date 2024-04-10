package cat.insVidreres.socialpostphone.imp.api

import cat.insVidreres.socialpostphone.imp.entity.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

interface UserService {
    @POST("users/login")
    fun login(@Body user: User): Call<JsonResponse>

    @POST("users")
    fun register(@Body user: User): Call<JsonResponse>


/*
    @GET("pokemon/ditto")
    suspend fun getPokemonProva(): Any

    object RetrofitServiceFactory{
        fun makeRetrofitService(): UserService{
            return Retrofit.Builder()
                .baseUrl("https://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(UserService::class.java)
        }
    }
*/

    @POST("users")
    suspend fun loginProva(@Body user: User): JsonResponse

    object RetrofitServiceFactory{
        fun makeRetrofitService(): UserService{
            return Retrofit.Builder()
                .baseUrl("http://192.168.56.2:8080/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(UserService::class.java)
        }
    }
}