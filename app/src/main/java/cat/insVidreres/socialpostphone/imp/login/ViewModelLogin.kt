package cat.insVidreres.socialpostphone.imp.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.insVidreres.socialpostphone.imp.api.Retrofit
import cat.insVidreres.socialpostphone.imp.entity.User


class ViewModelLogin: ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun loginUser(email: String, password: String) {
        val user = User(null,email, password,null,null,null)


        Retrofit.loginUser(user, { success ->
            _loginSuccess.postValue(success)
            _errorMessage.postValue("Inicio correcto")
        }, {
            _errorMessage.postValue("Error al iniciar sesiónnn")
        })
    }
}