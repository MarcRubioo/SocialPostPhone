package cat.insVidreres.socialpostphone.imp.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private var _user = MutableLiveData<User>()
    val user : LiveData<User> = _user

    private var _userPosts = MutableLiveData<MutableList<Post>>()
    val userPost : LiveData<MutableList<Post>> = _userPosts


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

    fun loadUserPosts(idToken: String, email: String) {
        _userPosts.value = mutableListOf<Post>()
        viewModelScope.launch {
            Repository.loadUserPosts(idToken, email,
                onSuccess = {
                    _userPosts.value = Repository.userPostsList
                    println("UsersPostList | ${Repository.userPostsList}")
                    println("getting users posts WORKS")
                },
                onFailure = { error ->
                    println("Error getting users posts | $error")
                })
        }
    }
}