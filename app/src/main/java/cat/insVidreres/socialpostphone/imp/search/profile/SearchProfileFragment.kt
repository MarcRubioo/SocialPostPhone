package cat.insVidreres.socialpostphone.imp.search.profile

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
import cat.insVidreres.socialpostphone.imp.databinding.FragmentSearchProfileBinding
import cat.insVidreres.socialpostphone.imp.entity.User
import cat.insVidreres.socialpostphone.imp.mainActivity.UsersSharedViewModel
import cat.insVidreres.socialpostphone.imp.posts.PostsAdapter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class SearchProfileFragment : Fragment() {
    private lateinit var binding: FragmentSearchProfileBinding
    private val viewModel: SearchProfileViewModel by viewModels()
    private val usersSharedViewModel: UsersSharedViewModel by activityViewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var email: String
    private lateinit var idToken: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchProfileBinding.inflate(inflater)


        val toolbar = binding.toolbar
        toolbar.setNavigationOnClickListener { findNavController().navigateUp() }


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

        var userReceived: User? = null

        var alreadyFriends = false
        var alreadyFollowed = false

        println("user received? | ${usersSharedViewModel.user.value}")


        usersSharedViewModel.user.observe(viewLifecycleOwner) { user ->
            if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
                userReceived = user

                binding.profileFriendNameTV.text = user.firstName
                binding.profileFriendFollowerAmount.text = user.followersList.size.toString() + " seguidores"
                binding.profileFriendFollowingAmount.text = user.followingList.size.toString() + " siguiendo"
                Glide.with(binding.profileFragmentFriendIV.context).load(user.img)
                    .into(binding.profileFragmentFriendIV)
                viewModel.loadUserPosts(idToken, user.email)


//                if (user.friendsList.any { it.email == email }) {
//                    binding.userFriendImageDrawable.setBackgroundResource(R.drawable.friend_already_added_drawable)
//                    alreadyFriends = true
//                } else {
//                    binding.userFriendImageDrawable.setBackgroundResource(R.drawable.add_friend_drawable)
//                    alreadyFriends = false
//                }
//
//                if (user.followersList.any { it.email == email }) {
//                    binding.userFollowButton.text = "Following"
//                    alreadyFollowed = true
//                } else {
//                    binding.userFollowButton.text = "Follow"
//                    alreadyFollowed = false
//                }


//                binding.userFriendImageDrawable.setOnClickListener {
//                    if (!alreadyFriends) {
//                        viewModel.addUserToFriends(idToken, email, user) { userToAdd ->
//                            //TODO add the user from user.friendsList
//                            user.friendsList.add(userToAdd)
//
//                            binding.userFriendImageDrawable.setBackgroundResource(R.drawable.friend_already_added_drawable)
//                            alreadyFriends = true
//                        }
//                    } else {
//                        viewModel.deleteUserFriend(idToken, email, user.email) { userToRemove ->
//                            //TODO remove the user from user.friendsList
//                            user.friendsList.remove(userToRemove)
//
//                            binding.userFriendImageDrawable.setBackgroundResource(R.drawable.add_friend_drawable)
//                            alreadyFriends = false
//                        }
//                    }
//                }
//
//                binding.userFollowButton.setOnClickListener {
//                    if (!alreadyFollowed) {
//                        //TODO add the user from user.followersList
//                        viewModel.addFollowerToUser(idToken, email, user) { userToAdd ->
//                            user.followersList.add(userToAdd)
//
//                            binding.userFollowButton.text = "Following"
//                            alreadyFollowed = true
//                        }
//                    } else {
//                        //TODO remove the user from user.followersList
//                        viewModel.deleteFollowerToUser(idToken, email, user.email) { userToRemove ->
//                            user.followersList.remove(userToRemove)
//
//                            binding.userFollowButton.text = "Follow"
//                            alreadyFollowed = false
//                        }
//                    }
//                }
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