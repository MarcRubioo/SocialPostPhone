package cat.insVidreres.socialpostphone.imp.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.entity.User
import kotlinx.coroutines.launch
class ViewModelSearch: ViewModel() {

    private var _UserList = MutableLiveData<MutableList<User>>()
    val userList : LiveData<MutableList<User>> = _UserList

    fun loadUsers(idToken: String, email: String) {
        _UserList.value = mutableListOf<User>()
        viewModelScope.launch {
            Repository.loadAllUsers(idToken, email,
                onSuccess = {
                    _UserList.value = Repository.usersList
                },
                onFailure = { error ->
                    println("Error getting friends | $error")
                })
        }
    }
}