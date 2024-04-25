package cat.insVidreres.socialpostphone.imp.mainActivity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cat.insVidreres.socialpostphone.imp.entity.Post

class UsersSharedViewModel : ViewModel() {

    private var _post = MutableLiveData<Post>()
    val post : LiveData<Post> = _post


    fun sendPost(post: Post) {
        _post.value = post
    }
}