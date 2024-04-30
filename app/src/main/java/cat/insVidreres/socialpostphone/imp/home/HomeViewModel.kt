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

    private var _categories = MutableLiveData<MutableList<String>>()
    val categories : LiveData<MutableList<String>> = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val temp = mutableListOf<User>()

    fun loadPosts(idToken: String) {
        _isLoading.value = true
        viewModelScope.launch {
            _posts.value = mutableListOf<Post>()
            Repository.loadPosts(idToken,
                onComplete = {
                    _posts.value = Repository.postsList
                    _isLoading.value = false
                    println("postsFrom viewmodel | ${_posts.value}")
                },
                onFailure = { error ->
                    _isLoading.value = false
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

    fun loadPostsWithCategory(idToken: String, categories: List<String>) {
        _isLoading.value = true
        _posts.value = mutableListOf<Post>()
        viewModelScope.launch {
            Repository.loadPostsWithCategory(idToken, categories,
                onComplete = {
                    _posts.value = Repository.postsList
                    _isLoading.value = false
                    println("posts with categories in viewmodel | ${_posts.value}")
                },
                onError = { error ->
                    println("error getting posts with category | $error")
                    _isLoading.value = false
                })
        }
    }

    fun loadCategories(idToken: String) {
        _categories.value = mutableListOf<String>()
        viewModelScope.launch {
            Repository.getCategories(idToken,
                onSuccess = {
                    _categories.value = Repository.categoriesList
                    println("categories in viewmodel | ${_categories.value}")
                },
                onFailure = { error ->
                    println("error getting categories | $error")
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
}