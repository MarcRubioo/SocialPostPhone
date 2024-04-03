package cat.insVidreres.socialpostphone.imp.api


data class JsonResponse (
    var responseNo: Int = 0,
    var date: String? = null,
    var message: String? = null,
    var data: List<Any> = ArrayList(),
)

