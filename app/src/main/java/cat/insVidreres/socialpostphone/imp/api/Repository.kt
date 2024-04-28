package cat.insVidreres.socialpostphone.imp.api

import android.content.Context
import android.net.Uri
import android.util.Log
import cat.insVidreres.socialpostphone.imp.entity.Comment
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User
import cat.insVidreres.socialpostphone.imp.profile.UpdatePFPRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Objects

class Repository {
    companion object {
        private const val BASE_URL = "http://169.254.180.117:8080/api/"
        var userToken: String = ""
        var usersList = mutableListOf<User>()
        lateinit var selectedUser: User
        var userPostsList = mutableListOf<Post>()
        var postsList = mutableListOf<Post>()
        var friendsList = mutableListOf<User>()
        var categoriesList = mutableListOf<String>()
        lateinit var serverResponse: JsonResponse
        var userImage : String = ""

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
            onSuccess: (User) -> Unit,
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
                                                img = userJson["img"] as String,
                                                friendsList = userJson["friends"] as MutableList<User>
                                            )

                                            selectedUser = user
                                            onSuccess(user)
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

        fun loadPosts(
            idToken: String,
            onComplete: () -> Unit,
            onFailure: (error: String) -> Unit
        ) {

            GlobalScope.launch(Dispatchers.IO) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val postService = retrofit.create(PostService::class.java)

                try {
                    postService.getPosts(idToken)
                        .enqueue(object : Callback<JsonResponse> {
                            override fun onResponse(
                                call: Call<JsonResponse>,
                                response: Response<JsonResponse>
                            ) {
                                if (response.isSuccessful) {
                                    postsList.clear()
                                    val jsonResponse = response.body()
                                    val postsFromServer = jsonResponse?.data

                                    if (postsFromServer != null && postsFromServer.isNotEmpty()) {
                                        var posts = postsFromServer as MutableList<Map<*, *>>
                                        println("POSTS AS LIST OF MAPS | $posts")

                                        if (posts != null) {


                                            posts.forEach { post ->
                                                val finalComments = mutableListOf<Comment>()
                                                try {
                                                    val comments = post["comments"] as? List<Map<String, Any>> // Assuming comments is a list of maps
                                                    comments?.forEach { commentMap ->
                                                        val email = commentMap["email"] as? String ?: ""
                                                        val desc = commentMap["comment"] as? String ?: ""
                                                        val commentAT = commentMap["commentAt"] as? String ?: ""
                                                        val likes = commentMap["likes"] as? MutableList<String> ?: mutableListOf()

                                                        val comment = Comment(email, desc, commentAT, likes)
                                                        finalComments.add(comment)
                                                    }
                                                } catch (e: Exception) {
                                                    println("error parsing the comments | ${e.message}")
                                                }


                                                val finalPost = Post(
                                                    post["id"] as String,
                                                    post["email"] as String,
                                                    post["createdAT"] as String,
                                                    post["description"] as String,
                                                    post["images"] as MutableList<String>,
                                                    post["categories"] as MutableList<String>,
                                                    post["likes"] as MutableList<String>,
                                                    finalComments
                                                )

                                                postsList.add(finalPost)
                                            }
                                            println("final post list | ${postsList}")
                                            onComplete()
                                        }
                                    }
                                }
                            }

                            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                println("Error | ${t.message}")
                                onFailure("Error | ${t.message}")
                                t.printStackTrace()
                            }
                        })
                } catch (e: Exception) {
                    println("Error | ${e.message}")
                    onFailure("Error | ${e.message}")
                    e.printStackTrace()
                }
            }
        }


        fun updateUserProfilePicture(
            idToken: String,
            email: String,
            imgData: ByteArray?,
            onComplete: () -> Unit,
            onFailure: (error: String) -> Unit
        ) {
            GlobalScope.launch(Dispatchers.IO) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val userService = retrofit.create(UserService::class.java)

                imgData?.let { data ->

                    val request = UpdatePFPRequest(
                        email,
                        data
                    )
                    userService.updateUserPFP(idToken, request)
                        .enqueue(object : Callback<JsonResponse>{
                            override fun onResponse(
                                call: Call<JsonResponse>,
                                response: Response<JsonResponse>
                            ) {
                                if (response.isSuccessful) {
                                    val jsonResponse = response.body()
                                    if (jsonResponse != null) {
                                        userImage = jsonResponse.data[0] as String
                                        onComplete()
                                    }
                                } else {
                                    println("An error has occurred | ${response.code()} | ${response.errorBody()}")
                                    onFailure("An error has occurred | ${response.code()} | ${response.errorBody()}")
                                }
                            }

                            override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                                Log.d("pfp update error", "error: ${t.message.toString()}")
                                println("error: ${t.message.toString()}")
                                onFailure(t.message.toString())
                            }
                        })
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
                                        postsList.clear()
                                        val jsonResponse = response.body()
                                        val postsFromServer = jsonResponse?.data
                                        if (postsFromServer != null && postsFromServer.isNotEmpty()) {
                                            var posts = postsFromServer as MutableList<Map<*, *>>
                                            println("POSTS AS LIST OF MAPS | $posts")

                                            if (posts != null) {
                                                posts.forEach { item ->
                                                    val finalComments = mutableListOf<Comment>()
                                                    try {
                                                        val comments = item["comments"] as? List<Map<String, Any>> // Assuming comments is a list of maps
                                                        comments?.forEach { commentMap ->
                                                            val email = commentMap["email"] as? String ?: ""
                                                            val desc = commentMap["comment"] as? String ?: ""
                                                            val commentAT = commentMap["commentAt"] as? String ?: ""
                                                            val likes = commentMap["likes"] as? MutableList<String> ?: mutableListOf()

                                                            val comment = Comment(email, desc, commentAT, likes)
                                                            finalComments.add(comment)
                                                        }
                                                    } catch (e: Exception) {
                                                        println("error parsing the comments | ${e.message}")
                                                    }

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
                                                        finalComments
                                                    )
                                                    postsList.add(post)
                                                }
                                                onComplete()
                                            }
                                        } else {
                                            postsList.clear()
                                            println("No posts for ${selectedItems}")
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
                    onError("Error | ${e.message}")
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

                                        if (posts != null) {
                                            userPostsList.clear()
                                            posts.forEach { item ->

                                                val finalComments = mutableListOf<Comment>()
                                                try {
                                                    val comments = item["comments"] as? List<Map<String, Any>> // Assuming comments is a list of maps
                                                    comments?.forEach { commentMap ->
                                                        val email = commentMap["email"] as? String ?: ""
                                                        val desc = commentMap["comment"] as? String ?: ""
                                                        val commentAT = commentMap["commentAt"] as? String ?: ""
                                                        val likes = commentMap["likes"] as? MutableList<String> ?: mutableListOf()

                                                        val comment = Comment(email, desc, commentAT, likes)
                                                        finalComments.add(comment)
                                                    }
                                                } catch (e: Exception) {
                                                    println("error parsing the comments | ${e.message}")
                                                }

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
                                                    finalComments
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

                val userService = retrofit.create(UserService::class.java)

                userService.getUserFriends(idToken, email)
                    .enqueue(object : Callback<JsonResponse> {
                        override fun onResponse(
                            call: Call<JsonResponse>,
                            response: Response<JsonResponse>
                        ) {
                            if (response.isSuccessful) {
                                val jsonResponse = response.body()
                                val userFromServer = jsonResponse?.data

                                if (userFromServer != null && userFromServer.isNotEmpty()) {
                                    var user = userFromServer as MutableList<Map<*, *>>

                                    if (user != null) {
                                        user.forEach { user ->
                                            val friendsArray = user["friends"] as MutableList<Map<*, *>>
                                            println("friends array ? | $friendsArray")

                                            if (friendsArray != null && friendsArray.isNotEmpty()) {
                                                friendsList.clear()
                                                println("user $user")
                                                friendsArray.forEach { friend ->
                                                    val finalFriend = User(
                                                        id = friend["id"] as? String,
                                                        email = friend["email"] as String,
                                                        password = friend["password"] as String,
                                                        firstName = friend["firstName"] as? String,
                                                        lastName = friend["lastName"] as? String,
                                                        age = (friend["age"] as? Double)?.toInt(),
                                                        phoneNumber = friend["phoneNumber"] as? String,
                                                        img = friend["img"] as String,
                                                        friendsList = friend["friends"] as MutableList<User>
                                                    )
                                                    friendsList.add(finalFriend)
                                                }
                                                onSuccess()
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                            println("Error | ${t.message}")
                            t.printStackTrace()
                            onFailure("Error | ${t.message}")
                        }
                    })
            }
        }


        fun createPost(
            idToken: String,
            post: Post,
            onSuccess: () -> Unit,
            onFailure: (error: String) -> Unit
        ) {

            GlobalScope.launch(Dispatchers.IO) {

            }

        }

        fun getCategories(
            idToken: String,
            onSuccess: () -> Unit,
            onFailure: (error: String) -> Unit
        ) {

            GlobalScope.launch(Dispatchers.IO) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val userService = retrofit.create(CategoriesService::class.java)

                userService.getCategories(idToken)
                    .enqueue(object : Callback<JsonResponse> {
                        override fun onResponse(
                            call: Call<JsonResponse>,
                            response: Response<JsonResponse>
                        ) {
                            if (response.isSuccessful) {
                                categoriesList.clear()
                                val jsonResponse = response.body()
                                val categoriesFromServer = jsonResponse?.data

                                if (categoriesFromServer != null && categoriesFromServer.isNotEmpty()) {
                                    var dataToShow = categoriesFromServer as MutableList<String>
                                    if (dataToShow != null) {
                                        println("dataToShow | $dataToShow")
                                        var categories = dataToShow as List<String>
                                        println("categories parsed correctly? | $categories")
                                        categories.forEach { category ->
                                            categoriesList.add(category)
                                        }

                                        onSuccess()
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                            println("call was not successfull | ${t.message}")
                            onFailure("Error calling categories | ${t.message}")
                        }
                    })
            }
        }
    }
}