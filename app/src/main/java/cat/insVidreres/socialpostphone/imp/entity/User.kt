package cat.insVidreres.socialpostphone.imp.entity
data class User (
    var id: String? = null,
    var email: String,
    var password: String,
    var firstName: String? = null,
    var lastName: String? = null,
    var age: Int? = null,
    var phoneNumber: String? = null,
    var img: String
)

