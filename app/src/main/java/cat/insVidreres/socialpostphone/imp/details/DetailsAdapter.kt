package cat.insVidreres.socialpostphone.imp.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.databinding.UserProfilePostBinding
import cat.insVidreres.socialpostphone.imp.entity.Comment
import cat.insVidreres.socialpostphone.imp.entity.Post
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailsAdapter(
    val context: Context,
    var dataset: List<Comment>,
    var idToken: String,
    var email: String,
    val likeItemClickListener: (Comment, Boolean) -> Unit
) :
    RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>() {

    inner class DetailsViewHolder(var binding: UserProfilePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment, position: Int) {
            var likedAlready = false

            if (comment.likes.contains(email)) {
                println("email | $email")
                println("comment likes | ${comment.likes}")
                println("comment has email? | ${comment.likes.contains(email)}")
                binding.postLikeButtonDrawable.setBackgroundResource(R.drawable.heart_filled)
                likedAlready = true
            }

            binding.postLikesAmountTV.text = comment.likes.size.toString()
//            binding.postCommentAmountTV.text = comment.comments.size.toString()

            binding.profilePostBodyTV.text = comment.desc
            if (!comment.commentAT.isNullOrEmpty()) {
                binding.profilePostDateTV.text = formatDate(comment.commentAT)
            }

            Repository.getUserDetails(idToken, comment.email,
                onSuccess = { user ->
                    println("user of comment | $user")
                    binding.profilePostUserNameTV.text = user.firstName
                    Glide.with(binding.profilePostUserIV.context).load(user.img).into(binding.profilePostUserIV)

                },
                onFailure = { error ->
                    println("Error loading user inside adapter: $error")
                }
            )

            if (position == dataset.size - 1) {
                binding.bottomLinePost.visibility = View.VISIBLE
            } else {
                binding.bottomLinePost.visibility = View.GONE
            }

            binding.postLikeButtonDrawable.setOnClickListener {
                likeItemClickListener(comment, likedAlready)

                if (!likedAlready) {
                    binding.postLikeButtonDrawable.setBackgroundResource(R.drawable.heart_filled)
                    binding.postLikesAmountTV.text = (binding.postLikesAmountTV.text.toString().toInt() + 1).toString()
                    likedAlready = true
                } else {
                    binding.postLikeButtonDrawable.setBackgroundResource(R.drawable.heart_empty)
                    binding.postLikesAmountTV.text = (binding.postLikesAmountTV.text.toString().toInt() - 1).toString()
                    likedAlready = false
                }
            }
        }
    }

    private fun formatDate(timestamp: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = sdf.parse(timestamp)
        val now = Calendar.getInstance().time
        val diff = now.time - date.time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "${days}d"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "${seconds}s"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailsViewHolder {
        val binding = UserProfilePostBinding.inflate(LayoutInflater.from(context), parent, false)
        return DetailsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: DetailsViewHolder, position: Int) {
        val comment = dataset[position]
        holder.bind(comment, position)
    }
}