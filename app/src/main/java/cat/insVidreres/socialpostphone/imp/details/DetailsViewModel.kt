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

    fun insertPostLike(idToken: String, email: String, post: Post) {
        viewModelScope.launch {
            Repository.insertLikeToPost(idToken, post, email,
                onComplete = {
                    println("inserted like :)")
                },
                onFailure = { error ->
                    println("error inserting like into post | $error")
                })
        }
    }


    fun deletePostLike(idToken: String, email: String, post: Post) {
        viewModelScope.launch {
            Repository.deleteLikeToPost(idToken, post, email,
                onComplete = {
                    println("deleted like :-)")
                },
                onFailure = { error ->
                    println("error deleting like into post | $error")
                })
        }
    }

    fun insertCommentLike(idToken: String, email: String, post: Post, comment: Comment) {
        viewModelScope.launch {
            Repository.insertLikeToComment(idToken, post, email, comment,
                onComplete = {
                    println("inserted like :)")
                },
                onFailure = { error ->
                    println("error inserting like into post | $error")
                })
        }
    }


    fun deleteCommentLike(idToken: String, email: String, post: Post, comment: Comment) {
        viewModelScope.launch {
            Repository.deleteLikeToComment(idToken, post, email, comment,
                onComplete = {
                    println("deleted like :-)")
                },
                onFailure = { error ->
                    println("error deleting like into post | $error")
                })
        }
    }
}