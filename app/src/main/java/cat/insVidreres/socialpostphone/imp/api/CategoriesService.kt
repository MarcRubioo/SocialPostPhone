package cat.insVidreres.socialpostphone.imp.api

import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface CategoriesService {

    @GET("categories")
    fun getCategories(@Header("idToken") token: String): Call<JsonResponse>

    @POST("categories")
    fun uploadCategory(@Header("idToken") token: String): Call<JsonResponse>

    @DELETE("categories")
    fun deleteCategory(@Header("idToken") token: String): Call<JsonResponse>
}