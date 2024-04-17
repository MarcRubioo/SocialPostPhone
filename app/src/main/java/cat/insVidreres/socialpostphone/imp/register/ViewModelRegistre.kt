package cat.insVidreres.socialpostphone.imp.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.entity.User

class ViewModelRegistre: ViewModel() {
    private val _registerSuccess = MutableLiveData<Boolean>()
    val registerSucces: LiveData<Boolean>
        get() = _registerSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun registerUser(email: String, password: String, name: String, img:String ) {
        val user = User(null,email, password, name, null,null, null, img )


        Repository.registerUser(user, { success ->
            _registerSuccess.postValue(success)
            _errorMessage.postValue("Resgistro correcto")
        }, {
            _errorMessage.postValue("Problemas en el registro")
        })
    }
}