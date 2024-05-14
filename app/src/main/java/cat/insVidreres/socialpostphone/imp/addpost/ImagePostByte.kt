package cat.insVidreres.socialpostphone.imp.addpost

import cat.insVidreres.socialpostphone.imp.entity.Post
data class ImagePostByte(
    var post: Post,
    var imgData: List<ByteArray>
)
