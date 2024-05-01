package cat.insVidreres.socialpostphone.imp.friends.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.FragmentFriendsProfileBinding
import cat.insVidreres.socialpostphone.imp.entity.User
import cat.insVidreres.socialpostphone.imp.mainActivity.UsersSharedViewModel
import cat.insVidreres.socialpostphone.imp.posts.PostsAdapter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FriendsProfileFragment : Fragment() {
    private lateinit var binding: FragmentFriendsProfileBinding
    private val viewModel: FriendsProfileViewModel by viewModels()
    private val usersSharedViewModel: UsersSharedViewModel by activityViewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var email: String
    private lateinit var idToken: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFriendsProfileBinding.inflate(inflater)

        //Handle top back arrow
        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        //Handle hardware back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        sharedPreferences =
            requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        idToken = sharedPreferences.getString("idToken", "").toString()
        email = sharedPreferences.getString("email", "").toString()

        val friendPostRecycler = binding.profileFriendPostsRecyclerView
        friendPostRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        var userReceived: User? = null;

        println("user received? | ${usersSharedViewModel.user.value}")


        usersSharedViewModel.user.observe(viewLifecycleOwner) { user ->
            if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
                userReceived = user

                binding.profileFriendNameTV.text = user.firstName
                Glide.with(binding.profileFragmentFriendIV.context).load(user.img)
                    .into(binding.profileFragmentFriendIV)
                viewModel.loadUserPosts(idToken, user.email)

            }
        }

        viewModel.userPost.observe(viewLifecycleOwner) { postsList ->
            val sortedPostsList = postsList.sortedByDescending { parseDate(it.createdAT) }
            println("user received from userPost observer? | ${sortedPostsList}")
            var adapter = userReceived?.let { user ->
                PostsAdapter(requireContext(), sortedPostsList, user, email,
                    itemOnClickListener = { selectedPost ->
                        usersSharedViewModel.sendPost(selectedPost)
                        findNavController().navigate(R.id.detailsFragment)

                    },
                    likeItemClickListener = { postClicked, likedAlready ->
                        if (likedAlready) {
                            println("Already liked? insert | $likedAlready")
                            viewModel.deletePostLike(idToken, email, postClicked)
                        } else {
                            println("Already liked? delete  | $likedAlready")
                            viewModel.insertPostLike(idToken, email, postClicked)
                        }
                    })

            }
            friendPostRecycler.adapter = adapter
        }

        return binding.root
    }

    private fun parseDate(createdAT: String): Date {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return sdf.parse(createdAT)
    }

}