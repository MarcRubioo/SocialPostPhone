package cat.insVidreres.socialpostphone.imp.userDetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.entity.User
import kotlinx.coroutines.launch


class UserDetailsViewModel : ViewModel() {

    private var _user = MutableLiveData<User>()
    val user : LiveData<User> = _user

    fun loadUser(idToken: String, email: String) {
        viewModelScope.launch {
            Repository.getUserDetails(idToken, email,
                onSuccess = {
                    _user.value = Repository.selectedUser
                    println("EVERYTHING GOOD ON USER CREDENTIALS??")
                },
                onFailure = {
                    println("ERROR GETTING USER DATA | $it")
                });
        }
    }
    fun logoutUser(callback: (Boolean) -> Unit) {
        Repository.logoutUser { success ->
            if (success) {
                callback(success)
            }
            else{
                callback(success)
            }
        }
    }

}