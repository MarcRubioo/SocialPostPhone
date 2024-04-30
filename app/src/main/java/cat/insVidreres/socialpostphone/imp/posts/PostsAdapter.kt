package cat.insVidreres.socialpostphone.imp.posts

import android.content.Context
import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.UserProfilePostBinding
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PostsAdapter(
    val context: Context,
    var dataset: List<Post>,
    var user: User,
    val itemOnClickListener: (Post) -> Unit,
    val likeItemClickListener: (Post, Boolean) -> Unit
) :
    RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    inner class PostViewHolder(var binding: UserProfilePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(post: Post) {
                var likedAlready = false

                if (post.likes.contains(user.email)) {
                    binding.postLikeButtonDrawable.setBackgroundResource(R.drawable.heart_filled)
                    likedAlready = true
                }

                binding.profilePostUserNameTV.text = user.firstName
                Glide.with(binding.profilePostUserIV.context).load(user.img).into(binding.profilePostUserIV)
                binding.profilePostBodyTV.text = post.description
                binding.profilePostDateTV.text = formatDate(post.createdAT)

                binding.postLikesAmountTV.text = post.likes.size.toString()
                binding.postCommentAmountTV.text = post.comments.size.toString()

                binding.root.setOnClickListener {
                    itemOnClickListener(post)
                }


                binding.postLikeButtonDrawable.setOnClickListener {
                    likeItemClickListener(post, likedAlready)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = UserProfilePostBinding.inflate(LayoutInflater.from(context), parent, false)
        return PostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = dataset[position]
        holder.bind(post)
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

}