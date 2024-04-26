package cat.insVidreres.socialpostphone.imp.details

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.databinding.UserProfilePostBinding
import cat.insVidreres.socialpostphone.imp.entity.Comment
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailsAdapter(
    val context: Context,
    var dataset: List<Comment>,
    var idToken: String
) :
    RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder>() {

    inner class DetailsViewHolder(var binding: UserProfilePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment, position: Int) {
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