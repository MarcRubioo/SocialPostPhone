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

    fun registerUser(email: String, password: String, name: String ) {
        val user = User(
            null,
            email,
            password,
            name,
            null,
            null,
            null,
            "https://firebasestorage.googleapis.com/v0/b/social-post-m13.appspot.com/o/placeholder_pfp.jpg?alt=media&token=4cf013bf-1afd-4c5a-8a4e-7248b5016feb"
            )


        Repository.registerUser(user, { success ->
            _registerSuccess.postValue(success)
            _errorMessage.postValue("Resgistro correcto")
        }, {
            _errorMessage.postValue("Problemas en el registro")
        })
    }
}