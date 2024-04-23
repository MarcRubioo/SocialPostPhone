package cat.insVidreres.socialpostphone.imp.posts

import android.content.Context
import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cat.insVidreres.socialpostphone.imp.databinding.UserProfilePostBinding
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User
import com.bumptech.glide.Glide

class PostsAdapter(
    val context: Context,
    var dataset: MutableList<Post>,
    var user: User
) :
    RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    inner class PostViewHolder(var binding: UserProfilePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(post: Post) {
                binding.profilePostUserNameTV.text = user.firstName
                Glide.with(binding.profilePostUserIV.context).load(user.img).into(binding.profilePostUserIV)
                binding.profilePostBodyTV.text = post.description
                binding.profilePostDateTV.text = post.createdAT
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
}