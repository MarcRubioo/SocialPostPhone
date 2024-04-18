package cat.insVidreres.socialpostphone.imp.api

import android.content.Context
import android.util.Log
import cat.insVidreres.socialpostphone.imp.entity.User
import com.google.firebase.auth.FirebaseAuth
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
                        Log.i("marc", "bnuhgbt7ghgbfgvbgvhbg")
                        onFailure()
                    }
                })
            }
        }


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
                                    if (userList != null) {
                                        selectedUser = userList[0] as User
                                        println("Social  |  $jsonResponse")
                                        onSuccess()
                                    }
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
            selectedItems: List<String>,
            onComplete: () -> Unit,
            onError: (error: String) -> Unit
        ) {

            GlobalScope.launch(Dispatchers.IO) {

            }
        }
    }
}