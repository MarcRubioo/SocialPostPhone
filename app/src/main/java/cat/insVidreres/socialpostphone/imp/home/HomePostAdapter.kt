package cat.insVidreres.socialpostphone.imp.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.api.Repository
import cat.insVidreres.socialpostphone.imp.databinding.FragmentHomeBinding
import cat.insVidreres.socialpostphone.imp.databinding.UserProfilePostBinding
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomePostAdapter(
    val context: Context,
    var dataset: List<Post>,
    var idToken: String,
    var email: String,
    val itemOnClickListener: (Post) -> Unit,
    val likeItemClickListener: (Post, Boolean) -> Unit
) :
    RecyclerView.Adapter<HomePostAdapter.HomePostViewHolder>() {

    inner class HomePostViewHolder(var binding: UserProfilePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post, position: Int) {
            var likedAlready = false

            println("does likes contain email? | ${post.likes.contains(email)}")
            if (post.likes.contains(email)) {
                binding.postLikeButtonDrawable.setBackgroundResource(R.drawable.heart_filled)
                likedAlready = true
            }


            binding.imageContainer.removeAllViews()
            val imageCount = post.images.size
            println("imageAmount | $imageCount")
            val isOddImageCount = imageCount % 2 != 0

            post.images.forEachIndexed { index, imageUrl ->
                val imageView = ImageView(binding.root.context)
                val params = when {
                    imageCount == 1 -> LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        2f
                    )
                    isOddImageCount -> LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                    )
                    index == 0 && isOddImageCount -> LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        2f
                    )
                    else -> LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1f
                    )
                }
                params.height = 220.dpToPx(binding.root.context) // Set maximum height for images
                params.marginEnd = if (index < imageCount - 1) 8.dpToPx(binding.root.context) else 0
                imageView.layoutParams = params
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .apply(RequestOptions().centerCrop())
                    .into(imageView)
                binding.imageContainer.addView(imageView)
            }



            binding.profilePostBodyTV.text = post.description
            binding.profilePostDateTV.text = formatDate(post.createdAT)

            binding.postLikesAmountTV.text = post.likes.size.toString()
            binding.postCommentAmountTV.text = post.comments.size.toString()


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
                itemOnClickListener.invoke(post)
            }

            binding.postLikeButtonDrawable.setOnClickListener {
                println("Before")
                likeItemClickListener.invoke(post, likedAlready)
                println("After")

                if (!likedAlready) {
                    //Insert call viewModel insertLike()
                    binding.postLikeButtonDrawable.setBackgroundResource(R.drawable.heart_filled)
                    binding.postLikesAmountTV.text = (binding.postLikesAmountTV.text.toString().toInt() + 1).toString()
                    likedAlready = true

                } else if (likedAlready) {
                    //Delete call viewModel deleteLike()
                    binding.postLikeButtonDrawable.setBackgroundResource(R.drawable.heart_empty)
                    binding.postLikesAmountTV.text = (binding.postLikesAmountTV.text.toString().toInt() - 1).toString()
                    likedAlready = false
                }
            }
        }
    }

    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
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