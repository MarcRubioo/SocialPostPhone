package cat.insVidreres.socialpostphone.imp.entity

data class Comment (
    var email: String,
    var desc: String,
    var commentAT: String,
    var likes: MutableList<String>
)