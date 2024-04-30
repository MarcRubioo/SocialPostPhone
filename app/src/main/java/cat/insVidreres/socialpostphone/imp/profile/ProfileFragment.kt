package cat.insVidreres.socialpostphone.imp.profile

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.FragmentProfileBinding
import cat.insVidreres.socialpostphone.imp.entity.User
import cat.insVidreres.socialpostphone.imp.mainActivity.UsersSharedViewModel
import cat.insVidreres.socialpostphone.imp.posts.PostsAdapter
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private val usersSharedViewModel: UsersSharedViewModel by activityViewModels()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var email: String
    private lateinit var idToken: String

    private var imageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater)

        sharedPreferences =
            requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        idToken = sharedPreferences.getString("idToken", "").toString()
        email = sharedPreferences.getString("email", "").toString()

        val userPostRecycler = binding.profileUserPostsRecyclerView
        userPostRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
            var userReceived: User? = null;
            viewModel.loadUser(idToken, email)
            viewModel.loadUserPosts(idToken, email)


            viewModel.user.observe(viewLifecycleOwner) { user ->
                binding.profileUserNameTV.text = user.firstName
                Glide.with(binding.profileFragmentUserIV.context).load(user.img).into(binding.profileFragmentUserIV)
                //TODO make user data class accept 2 arrays of string (followers, following) of user emails
//                binding.profileFollowerAmount.text = user.followers.size.toString()
//                binding.profileFollowingAmount.text = user.following.size.toString()
                userReceived = user
            }

            viewModel.userImg.observe(viewLifecycleOwner) { url ->
                Glide.with(binding.profileFragmentUserIV.context).load(url).into(binding.profileFragmentUserIV)
            }

            viewModel.userPost.observe(viewLifecycleOwner) { postsList ->
                val sortedPostsList = postsList.sortedByDescending { parseDate(it.createdAT) }
                println("user received from userPost observer? | ${sortedPostsList}")
                var adapter = userReceived?.let { user ->
                    PostsAdapter(requireContext(), sortedPostsList, user,
                        itemOnClickListener = { selectedPost ->
                            usersSharedViewModel.sendPost(selectedPost)
                            findNavController().navigate(R.id.detailsFragment)

                        },
                        likeItemClickListener = { postClicked, likedAlready ->
                            println("post clicked | ${postClicked.id}")

                            if (likedAlready) {
                                println("Already liked? insert | $likedAlready")
                                viewModel.deletePostLike(idToken, email, postClicked)
                            } else {
                                println("Already liked? delete  | $likedAlready")
                                viewModel.insertPostLike(idToken, email, postClicked)
                            }
                        })
                }
                userPostRecycler.adapter = adapter
            }

            binding.profileFragmentUserIV.setOnClickListener {
                resultLauncher.launch("image/*")
            }
        }

        return binding.root
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()){ uri ->

        idToken = sharedPreferences.getString("idToken", "").toString()
        email = sharedPreferences.getString("email", "").toString()

        imageUri = uri
        binding.profileFragmentUserIV.setImageURI(uri)

        if (idToken.isNotEmpty() && email.isNotEmpty()) {
            val inputStream = uri?.let { requireContext().contentResolver.openInputStream(it) }
            val imageData = inputStream?.readBytes()

            if (imageData != null) {
                viewModel.updateUserPFP(idToken, email, imageData)
            } else {
                println("imageData was null! |");
            }
        }
    }


    private fun parseDate(createdAT: String): Date {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return sdf.parse(createdAT)
    }

}