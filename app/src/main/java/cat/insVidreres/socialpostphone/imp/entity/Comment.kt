package cat.insVidreres.socialpostphone.imp.entity

data class Comment (
    var email: String,
    var desc: String,
    var commentAt: String,
    var likes: MutableList<String>
)