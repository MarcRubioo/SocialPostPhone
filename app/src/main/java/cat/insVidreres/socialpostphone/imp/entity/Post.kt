package cat.insVidreres.socialpostphone.imp.entity

data class Post(
    var id: String,
    var email: String,
    var createdAt: String,
    var images: MutableList<String>,
    var description: String,
    var category: MutableList<String>,
    var likes: MutableList<String>,
    var comments: MutableList<Comment>

)