package cat.insVidreres.socialpostphone.imp.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.databinding.FragmentHomeBinding
import cat.insVidreres.socialpostphone.imp.databinding.UserProfilePostBinding
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomePostAdapter(
    val context: Context,
    var dataset: List<Post>,
    var idToken: String,
    val itemOnClickListener: (Post) -> Unit
) :
    RecyclerView.Adapter<HomePostAdapter.HomePostViewHolder>() {

    inner class HomePostViewHolder(var binding: UserProfilePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, position: Int) {

            binding.profilePostBodyTV.text = post.description
            binding.profilePostDateTV.text = formatDate(post.createdAT)

            binding.postLikesAmountTV.text = post.likes.size.toString()
            binding.postCommentAmountTV.text = post.comments.size.toString()

            //TODO check if user has already liked. If liked change drawable and insert into Firebase

            Repository.getUserDetails(idToken, post.email,
                onSuccess = { user ->
                    binding.profilePostUserNameTV.text = user.firstName
                    Glide.with(binding.profilePostUserIV.context).load(user.img).into(binding.profilePostUserIV)

                },
                onFailure = { error ->
                    println("Error loading user: $error")
                }
            )

            if (position == dataset.size - 1) {
                binding.bottomLinePost.visibility = View.VISIBLE
            } else {
                binding.bottomLinePost.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                itemOnClickListener(post)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePostViewHolder {
        val binding = UserProfilePostBinding.inflate(LayoutInflater.from(context), parent, false)
        return HomePostViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: HomePostViewHolder, position: Int) {
        val post = dataset[position]
        holder.bind(post, position)
    }
}