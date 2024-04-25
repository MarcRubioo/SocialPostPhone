package cat.insVidreres.socialpostphone.imp.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var _posts = MutableLiveData<MutableList<Post>>()
    val posts : LiveData<MutableList<Post>> = _posts

    private var _usersList = MutableLiveData<MutableList<User>>()
    val usersList : LiveData<MutableList<User>> = _usersList

    val temp = mutableListOf<User>()

    fun loadPosts(idToken: String) {
        viewModelScope.launch {
            _posts.value = mutableListOf<Post>()
            Repository.loadPosts(idToken,
                onComplete = {
                    _posts.value = Repository.postsList
                    println("postsFrom viewmodel | ${_posts.value}")
                },
                onFailure = { error ->
                    println("Error inside on failure viewmodel | $error")
                })
        }
    }

    fun loadUsers(idToken: String, email: String) {
        _usersList.value = mutableListOf<User>()
        viewModelScope.launch {
            Repository.getUserDetails(idToken, email,
                onSuccess = {
                    temp.add(Repository.selectedUser)
                    _usersList.value = temp.toMutableList()
                    println("usersFrom viewmodel | ${_usersList.value}")

                },
                onFailure = { error ->
                    println("Error inside on failure viewmodel oadUsers | $error")
                })
        }
    }

    private val usersMap = mutableMapOf<String, User>()

    // Function to load user information for a post
    fun loadUserForPost(idToken: String, post: Post, onComplete: (Post, User) -> Unit) {
        viewModelScope.launch {
            Repository.getUserDetails(idToken, post.email,
                onSuccess = { user ->
                    onComplete(post, user)
                },
                onFailure = { error ->
                    println("Error loading user: $error")
                }
            )
        }
    }
}