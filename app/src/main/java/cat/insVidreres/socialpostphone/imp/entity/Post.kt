package cat.insVidreres.socialpostphone.imp.entity

data class Post(
    var id: String,
    var email: String,
    var createdAT: String,
    var description: String,
    var images: MutableList<String>,
    var categories: MutableList<String>,
    var likes: MutableList<String> = mutableListOf(),
    var comments: MutableList<Comment> = mutableListOf()
)