package cat.insVidreres.socialpostphone.imp.api

import android.content.Context
import android.util.Log
import cat.insVidreres.socialpostphone.imp.entity.Comment
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User
import cat.insVidreres.socialpostphone.imp.profile.UserTypeAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class Repository {
    companion object {
        private const val BASE_URL = "http://169.254.180.117:8080/api/"
        var userToken: String = ""
        var usersList = mutableListOf<User>()
        lateinit var selectedUser: User
        var userPostsList = mutableListOf<Post>()
        var postsList = mutableListOf<Post>()

        fun loginUser(
            context: Context,
            email: String,
            password: String,
            onSuccess: (Boolean) -> Unit,
            onFailure: () -> Unit
        ) {

            GlobalScope.launch(Dispatchers.IO) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                                ?.addOnCompleteListener { tokenTask ->
                                    if (tokenTask.isSuccessful) {
                                        val idToken = tokenTask.result.token
                                        userToken = idToken.toString()
                                        println("Social  |  $idToken")

                                        if (idToken != null) {
                                            val retrofit = Retrofit.Builder()
                                                .baseUrl(BASE_URL)
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build()

                                            val userService =
                                                retrofit.create(UserService::class.java)

                                            userService.login(idToken)
                                                .enqueue(object : Callback<JsonResponse> {
                                                    override fun onResponse(
                                                        call: Call<JsonResponse>,
                                                        response: Response<JsonResponse>
                                                    ) {
                                                        if (response.isSuccessful) {
                                                            val jsonResponse = response.body()
                                                            val userList = jsonResponse?.data
                                                            if (userList != null) {
                                                                val sharedPreferences =
                                                                    context.getSharedPreferences(
                                                                        "UserPreferences",
                                                                        Context.MODE_PRIVATE
                                                                    )
                                                                val editor =
                                                                    sharedPreferences.edit()
                                                                editor.remove("idToken")
                                                                editor.remove("email")
                                                                editor.apply()

                                                                editor.putString("idToken", idToken)
                                                                editor.putString("email", email)
                                                                editor.apply()

                                                                println(response.body())
                                                                onSuccess(userList.isNotEmpty())
                                                            }
                                                        }
                                                    }

                                                    override fun onFailure(
                                                        call: Call<JsonResponse>,
                                                        t: Throwable
                                                    ) {
                                                        onFailure()
                                                    }
                                                })
                                        } else {
                                            onFailure()
                                        }
                                    }
                                }
                        } else {
                            onFailure()
                        }
                    }
            }

        }


        fun registerUser(user: User, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit) {

            GlobalScope.launch(Dispatchers.IO) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()


                val userService = retrofit.create(UserService::class.java)

                userService.register(user).enqueue(object : Callback<JsonResponse> {
                    override fun onResponse(
                        call: Call<JsonResponse>,
                        response: Response<JsonResponse>
                    ) {
                        if (response.isSuccessful) {
                            val jsonResponse = response.body()
                            val userList = jsonResponse?.data
                            if (userList != null) {
                                onSuccess(userList.isNotEmpty())
                                println("Social  |  $jsonResponse")
                            }
                        }
                    }

                    override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                        Log.i("Sign In Http", "Error signing in | ${t.message}")
                        onFailure()
                    }
                })
            }
        }

//        fun getUserServiceWithCustomGson(): UserService {
//            val gson = GsonBuilder()
//                .registerTypeAdapter(User::class.java, UserTypeAdapter())
//                .create()
//
//
//            return retrofit.create(UserService::class.java)
//        }

        fun getUserDetails(
            idToken: String,
            email: String,
            onSuccess: () -> Unit,
            onFailure: (error: String) -> Unit
        ) {

            GlobalScope.launch(Dispatchers.IO) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val userService = retrofit.create(UserService::class.java)

                try {
                    userService.getUserDetails(idToken, email)
                        .enqueue(object : Callback<JsonResponse> {
                            override fun onResponse(
                                call: Call<JsonResponse>,
                                response: Response<JsonResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val jsonResponse = response.body()
                                    val userList = jsonResponse?.data

                                    if (userList != null && userList.isNotEmpty()) {
                                        val userJson = userList[0] as? Map<*, *>

                                        if (userJson != null) {
                                            val user = User(
                                                id = userJson["id"] as? String,
                                                email = userJson["email"] as String,
                                                password = userJson["password"] as String,
                                                firstName = userJson["firstName"] as? String,
                                                lastName = userJson["lastName"] as? String,
                                                age = (userJson["age"] as? Double)?.toInt(),
                                                phoneNumber = userJson["phoneNumber"] as? String,
                                                img = userJson["img"] as String
                                            )

                                            selectedUser = user
                                            onSuccess()
                                        } else {
                                            onFailure("Error parsing user data")
                                        }
                                    } else {
                                        onFailure("User list is empty or null")
                                    }
                                } else {
                                    onFailure("Response unsuccessful: ${response.code()}")
                                }
                            }

                            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                Log.d("userDetails error", "error: ${t.message.toString()}")
                                println("error: ${t.message.toString()}")
                                onFailure(t.message.toString())
                            }
                        })
                } catch (e: Exception) {
                    println("Error  |  ${e.message}")
                }
            }
        }

        fun loadPostsWithCategory(
            idToken: String,
            selectedItems: List<String>,
            onComplete: () -> Unit,
            onError: (error: String) -> Unit
        ) {

            GlobalScope.launch(Dispatchers.IO) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val postService = retrofit.create(PostService::class.java)

                try {
                    var finalString = ""

                    selectedItems.forEach { item ->
                        finalString += "$item,"
                    }

                    if (finalString != "") {
                        postService.getPostsWithCategory(idToken, finalString)
                            .enqueue(object : Callback<JsonResponse> {
                                override fun onResponse(
                                    call: Call<JsonResponse>,
                                    response: Response<JsonResponse>
                                ) {
                                    if (response.isSuccessful) {
                                        val jsonResponse = response.body()
                                        val posts = jsonResponse?.data
                                        if (posts != null) {
                                            postsList = posts as MutableList<Post>
                                            println("Social  |  $jsonResponse")
                                            onComplete()
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                    Log.d("userDetails error", "error: ${t.message.toString()}")
                                    println("error: ${t.message.toString()}")
                                    onError(t.message.toString())
                                }
                            })
                    } else {
                        println("ERROR GETTING CATEGORIES | concatenation ${finalString}")
                        println("ERROR GETTING CATEGORIES | array ${selectedItems}")
                    }

                } catch (e: Exception) {
                    println("Error | ${e.message}")
                    e.printStackTrace()
                }
            }
        }

        fun loadUserPosts(
            idToken: String,
            email: String,
            onSuccess: () -> Unit,
            onFailure: (error: String) -> Unit
        ) {
            GlobalScope.launch(Dispatchers.IO) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val postService = retrofit.create(PostService::class.java)

                try {
                    postService.getUserPosts(idToken, email)
                        .enqueue(object: Callback<JsonResponse> {
                            override fun onResponse(
                                call: Call<JsonResponse>,
                                response: Response<JsonResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val jsonResponse = response.body()
                                    val postsFromServer = jsonResponse?.data

                                    if (postsFromServer != null && postsFromServer.isNotEmpty()) {
                                        var posts = postsFromServer as MutableList<Map<*, *>>
                                        println("POSTS AS LIST OF MAPS | $posts")

                                        if (posts != null) {
                                            posts.forEach { item ->
                                                println("item ${posts.indexOf(item)} | $item")
                                                if (item["id"] == null) {
                                                    (item as MutableMap<String, Any?>)["id"] = ""
                                                }

                                                val post = Post(
                                                    item["id"] as String,
                                                    item["email"] as String,
                                                    item["createdAT"] as String,
                                                    item["description"] as String,
                                                    item["images"] as MutableList<String>,
                                                    item["categories"] as MutableList<String>,
                                                    item["likes"] as MutableList<String>,
                                                    item["comments"] as MutableList<Comment>
                                                )
                                                userPostsList.add(post)
                                                onSuccess()
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                Log.d("userPosts error", "error: ${t.message.toString()}")
                                println("error: ${t.message.toString()}")
                                onFailure(t.message.toString())
                            }
                        })
                } catch (e: Exception) {
                    println("Error | ${e.message}")
                    e.printStackTrace()
                    onFailure("Error | ${e.message}")
                }
            }
        }


        fun loadUserFriends(
            idToken: String,
            email: String,
            onSuccess: () -> Unit,
            onFailure: (error: String) -> Unit) {

            GlobalScope.launch(Dispatchers.IO) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
        }
    }
}