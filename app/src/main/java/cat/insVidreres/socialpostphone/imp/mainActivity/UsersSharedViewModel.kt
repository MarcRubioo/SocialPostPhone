package cat.insVidreres.socialpostphone.imp.mainActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User

class UsersSharedViewModel : ViewModel() {

    private var _post = MutableLiveData<Post>()
    val post : LiveData<Post> = _post

    private var _user = MutableLiveData<User>()
    val user : LiveData<User> = _user

    fun sendPost(post: Post) {
        _post.value = post
    }

    fun sendUser(user: User) {
        _user.value = user
    }
}