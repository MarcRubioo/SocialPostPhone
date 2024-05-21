package cat.insVidreres.socialpostphone.imp.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cat.insVidreres.socialpostphone.imp.databinding.FriendsItemFriendsFragmentBinding
import cat.insVidreres.socialpostphone.imp.entity.User
import com.bumptech.glide.Glide

class SearchAdapter(
    val context: Context,
    var dataset: List<User>,
    val itemOnClickListener: (User) -> Unit
) :
    RecyclerView.Adapter<SearchAdapter.FriendsViewHolder>() {

    inner class FriendsViewHolder(var binding: FriendsItemFriendsFragmentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.friendsUserNameTV.text = user.firstName
            Glide.with(binding.friendPostUserIV.context).load(user.img).into(binding.friendPostUserIV)

            binding.friendsUsersTopContainer.setOnClickListener {
                itemOnClickListener.invoke(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsViewHolder {
        val binding =
            FriendsItemFriendsFragmentBinding.inflate(LayoutInflater.from(context), parent, false)
        return FriendsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: FriendsViewHolder, position: Int) {
        val user = dataset[position]
        holder.bind(user)
    }
}