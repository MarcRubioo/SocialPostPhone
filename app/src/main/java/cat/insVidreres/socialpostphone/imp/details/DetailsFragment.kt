package cat.insVidreres.socialpostphone.imp.details

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.FragmentDetailsBinding
import cat.insVidreres.socialpostphone.imp.mainActivity.UsersSharedViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DetailsFragment : Fragment() {

    private lateinit var binding: FragmentDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()
    private val usersSharedViewModel : UsersSharedViewModel by activityViewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var email: String
    private lateinit var idToken: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(inflater)

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

        var commentsRecycler = binding.postCommentsRecyclerView
        commentsRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        usersSharedViewModel.post.observe(viewLifecycleOwner) { post ->
            if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
                viewModel.loadUserDetails(post, idToken) { userDetailsForPost ->
                    binding.detailsPostUserNameTV.text = userDetailsForPost.firstName
                    Glide.with(binding.detailsPostUserIV.context).load(userDetailsForPost.img).into(binding.detailsPostUserIV)
                }
                binding.detailsPostBodyTV.text = post.description
                binding.profilePostDateTV.text = formatDate(post.createdAT)
                binding.detailsPostCommentAmountTV.text = post.comments.size.toString()
                binding.detailsPostLikesAmountTV.text = post.likes.size.toString()

                var likedAlready = false

                if (post.likes.contains(email)) {
                    println("Details liked already if? | " + email)
                    binding.detailsPostLikesButtonDrawable.setBackgroundResource(R.drawable.heart_filled)
                    likedAlready = true
                } else {
                    println("Details liked already else? | " + email)
                    binding.detailsPostLikesButtonDrawable.setBackgroundResource(R.drawable.heart_empty)
                    likedAlready = false
                }


                binding.detailsPostLikesButtonDrawable.setOnClickListener {
                    if (!likedAlready) {
                        viewModel.insertPostLike(idToken, email, post)
                        binding.detailsPostLikesButtonDrawable.setBackgroundResource(R.drawable.heart_filled)
                        binding.detailsPostLikesAmountTV.text = (binding.detailsPostLikesAmountTV.text.toString().toInt() + 1).toString()
                        likedAlready = true
                    } else {
                        viewModel.deletePostLike(idToken, email, post)
                        binding.detailsPostLikesButtonDrawable.setBackgroundResource(R.drawable.heart_empty)
                        binding.detailsPostLikesAmountTV.text = (binding.detailsPostLikesAmountTV.text.toString().toInt() - 1).toString()
                        likedAlready = false
                    }
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



                viewModel.loadComments(post)

                viewModel.comments.observe(viewLifecycleOwner) { commentsList ->

                    println("comments received? | ${commentsList}")
                    val sortedPostsList = commentsList.sortedByDescending { parseDate(it.commentAT) }
                    val adapter = DetailsAdapter(requireContext(), sortedPostsList, idToken, email,
                        likeItemClickListener = { selectedComment, LikedAlready ->
                            if (LikedAlready) {
                                println("comment already liked | $LikedAlready")
                                viewModel.deleteCommentLike(idToken, email, post, selectedComment)
                            } else {
                                println("comment not liked | $LikedAlready")
                                viewModel.insertCommentLike(idToken, email, post, selectedComment)
                            }
                        })
                    commentsRecycler.adapter = adapter
                }
            }
        }


        return binding.root
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

    private fun parseDate(createdAT: String): Date {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return sdf.parse(createdAT)
    }
}