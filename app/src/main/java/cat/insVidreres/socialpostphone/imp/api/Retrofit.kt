package cat.insVidreres.socialpostphone.imp.api

import android.util.Log
import cat.insVidreres.socialpostphone.imp.entity.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofit {
    companion object {
        private const val BASE_URL = "http://192.168.56.2:8080/"

        fun loginUser(user: User, onSuccess: (Boolean) -> Unit, onFailure: () -> Unit) {

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
                    onSuccess(response.isSuccessful)
                }

                override fun onFailure(call: Call<JsonResponse>, t: Throwable) {
                    onFailure()
                }
            })
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
}