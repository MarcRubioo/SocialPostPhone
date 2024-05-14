package cat.insVidreres.socialpostphone.imp.api

import cat.insVidreres.socialpostphone.imp.addpost.ImagePostByte
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
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
    @POST("postsAndroid")
    fun createPost(@Header("idToken") token: String, @Body body: ImagePostByte): Call<JsonResponse>

    @PUT("addLikePost/{idPost}")
    fun likePost(@Header("idToken") token: String, @Path("idPost") idPost: String, @Body email: String): Call<JsonResponse>

    @DELETE("deleteLikePost/{idPost}")
    fun deleteLikePost(@Header("idToken") token: String, @Path("idPost") idPost: String, @Query("email") email: String): Call<JsonResponse>

    @PUT("addLikePostComment/{idPost}/{idComment}")
    fun likeComment(@Header("idToken") token: String, @Body email: String, @Path("idPost") idPost: String, @Path("idComment") idComment: String): Call<JsonResponse>


    @DELETE("deleteLikePostComment/{idPost}/{idComment}")
    fun deleteCommentLike(@Header("idToken") token: String, @Path("idPost") idPost: String, @Path("idComment") idComment: String, @Query("email") email: String): Call<JsonResponse>


}