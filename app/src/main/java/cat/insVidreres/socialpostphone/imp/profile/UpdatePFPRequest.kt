package cat.insVidreres.socialpostphone.imp.profile

data class UpdatePFPRequest(
    var email: String,
    var imgData: ByteArray
)
