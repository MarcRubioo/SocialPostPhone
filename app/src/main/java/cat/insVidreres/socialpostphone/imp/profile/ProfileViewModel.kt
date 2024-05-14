package cat.insVidreres.socialpostphone.imp.profile

import android.net.Uri
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

    private var _userImg = MutableLiveData<String>()
    val userImg : LiveData<String> = _userImg

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
                    println("getting users posts WORKS")
                },
                onFailure = { error ->
                    println("Error getting users posts | $error")
                })
        }
    }

    fun updateUserPFP(idToken: String, email: String, imgData: ByteArray?) {
        println("img perfil "+imgData)
        viewModelScope.launch {
            Repository.updateUserProfilePicture(idToken, email, imgData,
                onComplete = {
                    println("image updated!")
                    _userImg.value = Repository.userImage
                }, onFailure = { error ->
                    println("error updating the pfp | $error")
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