package cat.insVidreres.socialpostphone.imp.login

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.insVidreres.socialpostphone.imp.api.Repository


class ViewModelLogin: ViewModel() {

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean>
        get() = _loginSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage


    fun loginUser(context: Context, email: String, password: String) {
        Repository.loginUser(context, email, password, { success ->
            _loginSuccess.postValue(success)
            _errorMessage.postValue("Inicio correcto")
        }, {
            _errorMessage.postValue("Error al iniciar sesiónnn")
        })
    }


}