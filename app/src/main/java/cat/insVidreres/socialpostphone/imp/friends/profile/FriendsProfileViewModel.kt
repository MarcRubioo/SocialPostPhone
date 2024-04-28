package cat.insVidreres.socialpostphone.imp.friends.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.entity.Post
import kotlinx.coroutines.launch

class FriendsProfileViewModel : ViewModel() {

    private var _userPosts = MutableLiveData<MutableList<Post>>()
    val userPost : LiveData<MutableList<Post>> = _userPosts


    fun loadUserPosts(idToken: String, email: String) {
        _userPosts.value = mutableListOf<Post>()
        viewModelScope.launch {
            Repository.loadUserPosts(idToken, email,
                onSuccess = {
                    _userPosts.value = Repository.userPostsList
                    println("getting users posts WORKS")
                },
                onFailure = { error ->
                    println("Error getting users posts | $error")
                })
        }
    }
}