package cat.insVidreres.socialpostphone.imp.api

import cat.insVidreres.socialpostphone.imp.entity.Comment
import cat.insVidreres.socialpostphone.imp.entity.Post
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PostService {
    @GET("allposts")
    fun getPosts(@Header("idToken") token: String): Call<JsonResponse>

    @GET("posts")
    fun getPostsWithCategory(@Header("idToken") token: String, @Header("Categories") categories: String): Call<JsonResponse>

    @GET("userPosts")
    fun getUserPosts(@Header("idToken") token: String, @Query("email") email: String): Call<JsonResponse>

    @POST("posts")
    fun uploadPost(@Header("idToken") token: String, @Body post: Post): Call<JsonResponse>

    @POST("post")
    fun uploadComment(@Header("idToken") token: String, @Body post: Post): Call<JsonResponse>

    @PUT("addLikePost/{idPost}")
    fun likePost(@Header("idToken") token: String, @Path("idPost") idPost: String, @Body email: String): Call<JsonResponse>

    @DELETE("deleteLikePost/{idPost}")
    fun deleteLikePost(@Header("idToken") token: String, @Path("idPost") idPost: String, @Query("email") email: String): Call<JsonResponse>

    @PUT("post/{postId}")
    fun likeComment(@Header("idToken") token: String, @Body comment: Comment, @Path("postId") postId: String): Call<JsonResponse>


}