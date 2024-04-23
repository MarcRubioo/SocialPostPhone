package cat.insVidreres.socialpostphone.imp.friends

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.entity.User
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {

    private var _friendsList = MutableLiveData<MutableList<User>>()
    val friendsList : LiveData<MutableList<User>> = _friendsList


    fun loadFriends(idToken: String, email: String) {
        _friendsList.value = mutableListOf<User>()
        viewModelScope.launch {
            Repository.loadUserFriends(idToken, email,
                onSuccess = {
                    _friendsList.value = Repository.friendsList
                },
                onFailure = { error ->
                    println("Error getting friends | $error")
                })
        }
    }
}