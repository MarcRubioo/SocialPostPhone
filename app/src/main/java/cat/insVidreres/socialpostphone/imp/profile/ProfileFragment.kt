package cat.insVidreres.socialpostphone.imp.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.FragmentProfileBinding
import cat.insVidreres.socialpostphone.imp.entity.User
import cat.insVidreres.socialpostphone.imp.posts.PostsAdapter
import com.bumptech.glide.Glide


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)

        val sharedPreferences =
            requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val idToken = sharedPreferences.getString("idToken", "")
        val email = sharedPreferences.getString("email", "")

        val userPostRecycler = binding.profileUserPostsRecyclerView
        userPostRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
            var userReceived: User? = null;
            viewModel.loadUser(idToken, email)

            viewModel.user.observe(viewLifecycleOwner) { user ->
                binding.profileUserNameTV.text = user.firstName
                Glide.with(binding.profileFragmentUserIV.context).load(user.img).into(binding.profileFragmentUserIV)
                //TODO make user data class accept 2 arrays of string (followers, following) of user emails
//                binding.profileFollowerAmount.text = user.followers.size.toString()
//                binding.profileFollowingAmount.text = user.following.size.toString()
                userReceived = user

            }

            viewModel.loadUserPosts(idToken, email)

            viewModel.userPost.observe(viewLifecycleOwner) { postsList ->
                println("user received from userPost observer? | ${userReceived}")
                var adapter = userReceived?.let { PostsAdapter(requireContext(), postsList, it) }
                userPostRecycler.adapter = adapter
            }
        }

        return binding.root
    }

}