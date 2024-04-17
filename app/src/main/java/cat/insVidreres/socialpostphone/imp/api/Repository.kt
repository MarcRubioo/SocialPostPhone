package cat.insVidreres.socialpostphone.imp.api

import android.util.Log
import cat.insVidreres.socialpostphone.imp.entity.User
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Repository {
    companion object {
        private const val BASE_URL = "http://169.254.180.117:8080/api/"


/*        fun loginUser(user: User, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit) {

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val userService = retrofit.create(UserService::class.java)

            userService.login(user).enqueue(object : Callback<JsonResponse> {
                override fun onResponse(
                    call: Call<JsonResponse>,
                    response: Response<JsonResponse>
                ) {
                    if (response.isSuccessful) {
                        val jsonResponse = response.body()
                        val userList = jsonResponse?.data
                        if (userList != null) {
                            println(userList)
                            onSuccess(userList.isNotEmpty())
                        }
                    }
                }

                *//*                {
                                    onSuccess(response.isSuccessful)
                                }*//*

                override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                    onFailure()
                }
            })
        }*/

        fun loginUser(
            email: String,
            password: String,
            onSuccess: (Boolean) -> Unit,
            onFailure: () -> Unit
        ) {

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                            ?.addOnCompleteListener { tokenTask ->
                                if (tokenTask.isSuccessful) {
                                    val idToken = tokenTask.result.token
                                    println("Social  |  $idToken")

                                    if (idToken != null) {
                                        val retrofit = Retrofit.Builder()
                                            .baseUrl(BASE_URL)
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .build()

                                        val userService = retrofit.create(UserService::class.java)

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


        fun registerUser(user: User, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit) {

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


        fun loadPosts(selectedItems: List<String>, onComplete: () -> Unit, onError: (error: String) -> Unit) {

        }
    }
}