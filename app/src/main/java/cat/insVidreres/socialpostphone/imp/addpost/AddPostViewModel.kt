import android.net.Uri
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.entity.Post
import kotlinx.coroutines.launch

class AddPostViewModel : ViewModel() {
//    private var imagePost: Uri? = null
//    fun setImageUri(uri: Uri?) {
//        imagePost = uri
//    }



    var byteArrayList: MutableList<ByteArray> = mutableListOf()




    private var _categories = MutableLiveData<MutableList<String>>()
    val categories : LiveData<MutableList<String>> = _categories

    fun addPost(
        token: String,
        id: String,
        email: String,
        date: String,
        description: String,
        category: MutableList<String>,
        callback: (Boolean) -> Unit
    ) {

        val post = Post(
            id,
            email,
            date,
            description,
            mutableListOf(),
            category,
            mutableListOf(),
            mutableListOf()
        )


        viewModelScope.launch {
            Repository.createPost(token, post, byteArrayList,
                onSuccess = {
                    callback(it)
                },
                onFailure = {
                    callback(it)
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

    fun generarCodigoAleatorio(): String {
        val caracteres = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val codigo = StringBuilder(20)

        repeat(20) {
            val caracterAleatorio = caracteres.random()
            codigo.append(caracterAleatorio)
        }

        return codigo.toString()
    }
}