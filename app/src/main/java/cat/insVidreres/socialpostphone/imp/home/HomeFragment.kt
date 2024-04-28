package cat.insVidreres.socialpostphone.imp.home

import android.content.Context
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.FragmentHomeBinding
import cat.insVidreres.socialpostphone.imp.entity.Post
import cat.insVidreres.socialpostphone.imp.entity.User
import cat.insVidreres.socialpostphone.imp.mainActivity.UsersSharedViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val usersSharedViewModel: UsersSharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        binding.homeNoUserPostsWarningTV.visibility = View.GONE
        val sharedPreferences =
            requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val idToken = sharedPreferences.getString("idToken", "")
        val email = sharedPreferences.getString("email", "")

        val postRecycler = binding.homePostsRV
        postRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        var adapter = HomePostAdapter(requireContext(), emptyList(), idToken!!) { selectedPost ->
            if (idToken != null) {
                viewModel.loadUsers(idToken, selectedPost.email)
            }
        }


        viewModel.loadCategories(idToken)
        viewModel.categories.observe(viewLifecycleOwner) { categoriesList ->
            setupPopupMenu(categoriesList, idToken)
            println("categoriesList observed | $categoriesList")
            postRecycler.adapter?.notifyDataSetChanged()
        }

        postRecycler.adapter = adapter


        if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
            viewModel.loadPosts(idToken)
            viewModel.posts.observe(viewLifecycleOwner) { postsList ->
                if (!viewModel.isLoading.value!! && postsList.isEmpty()) {
                    binding.homeNoUserPostsWarningTV.visibility = View.VISIBLE
                    postRecycler.adapter?.notifyDataSetChanged()
                } else if (postsList.isNotEmpty()) {
                    val updatedPostsList = mutableListOf<Post>()
                    val totalPosts = postsList.size
                    var loadedPostsCount = 0
                    postsList.forEach { post ->
                        viewModel.loadUserForPost(idToken, post) { updatedPost, user ->
                            updatedPostsList.add(updatedPost)
                            loadedPostsCount++
                            if (loadedPostsCount == totalPosts) {
                                updateRecyclerView(updatedPostsList, idToken)
                            }
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun updateRecyclerView(postsList: List<Post>, idToken: String) {
        val sortedPostsList = postsList.sortedByDescending { parseDate(it.createdAT) }
        println("sortedList | $sortedPostsList")

        val adapter = HomePostAdapter(requireContext(), sortedPostsList, idToken) { selectedPost ->
            usersSharedViewModel.sendPost(selectedPost)
            findNavController().navigate(R.id.detailsFragment)
        }
        binding.homePostsRV.adapter = adapter
    }

    private fun parseDate(createdAT: String): Date {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        return sdf.parse(createdAT)
    }

    private fun setupPopupMenu(categoriesList: List<String>, idToken: String) {
        binding.menuIcon.setOnClickListener {
            showPopupMenu(binding.menuIcon, categoriesList, idToken)
        }
    }

    private fun showPopupMenu(menuAnchor: View, categoriesList: List<String>, idToken: String) {
        val popupMenu = PopupMenu(requireContext(), menuAnchor)
        categoriesList.forEach { category ->
            popupMenu.menu.add(category).apply {
                setOnMenuItemClickListener {
                    // Handle category item click
                    val categoryName = it.title.toString().split(",")[0]
                    val categoriesSelected = mutableListOf<String>()
                    categoriesSelected.add(categoryName)
                    Toast.makeText(requireContext(), categoryName, Toast.LENGTH_SHORT).show()
                    viewModel.loadPostsWithCategory(idToken, categoriesSelected)
                    binding.homeNoUserPostsWarningTV.text = "Loading..."
                    val handler = Handler(Looper.getMainLooper())

                    viewModel.posts.observe(viewLifecycleOwner) { postsList ->
                        if (postsList.isEmpty()) {
                            binding.homeNoUserPostsWarningTV.visibility = View.VISIBLE
                            binding.homePostsRV.visibility = View.GONE

                            //Here
                            handler.postDelayed({
                                binding.homeNoUserPostsWarningTV.text =
                                    "No posts found for $categoryName category"
                            }, 2000)
                        } else {

                            handler.postDelayed({
                                binding.homeNoUserPostsWarningTV.text =
                                    "Loading..."

                                binding.homeNoUserPostsWarningTV.visibility = View.VISIBLE
                                binding.homePostsRV.visibility = View.VISIBLE
                                val updatedPostsList = mutableListOf<Post>()
                                val totalPosts = postsList.size
                                var loadedPostsCount = 0
                                postsList.forEach { post ->
                                    viewModel.loadUserForPost(idToken, post) { updatedPost, user ->
                                        updatedPostsList.add(updatedPost)
                                        loadedPostsCount++
                                        if (loadedPostsCount == totalPosts) {
                                            updateRecyclerView(updatedPostsList, idToken)
                                        }
                                    }
                                }

                                binding.homeNoUserPostsWarningTV.visibility = View.GONE

                            }, 1500)
                        }
                    }
                    true
                }
            }
        }
        popupMenu.show()
    }


}