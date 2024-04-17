package cat.insVidreres.socialpostphone.imp.entity
//TODO -> Update so that it has a field named img
data class User (
    var id: String? = null,
    var email: String,
    var password: String,
    var firstName: String? = null,
    var lastName: String? = null,
    var age: Int? = null,
    var phoneNumber: String? = null,
    var img: String = "https://firebasestorage.googleapis.com/v0/b/social-post-m13.appspot.com/o/placeholder_pfp.jpg?alt=media&token=4cf013bf-1afd-4c5a-8a4e-7248b5016feb"
)
