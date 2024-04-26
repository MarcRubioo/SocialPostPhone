package cat.insVidreres.socialpostphone.imp.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.entity.Comment
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User
import kotlinx.coroutines.launch

class DetailsViewModel : ViewModel() {

    private var _comments = MutableLiveData<MutableList<Comment>>()
    val comments : LiveData<MutableList<Comment>> = _comments


    fun loadComments(post: Post) {
        _comments.value = post.comments
    }

    fun loadUserDetails(post: Post, idToken: String, onComplete: (User) -> Unit) {
        viewModelScope.launch {
            Repository.getUserDetails(idToken, post.email,
                onSuccess = { user ->
                    onComplete(user)
                },
                onFailure = { error ->
                    println("error getting user details")
                })
        }
    }
}