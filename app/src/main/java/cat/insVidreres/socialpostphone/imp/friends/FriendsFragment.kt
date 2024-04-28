package cat.insVidreres.socialpostphone.imp.friends

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.FragmentFriendsBinding
import cat.insVidreres.socialpostphone.imp.mainActivity.UsersSharedViewModel


class FriendsFragment : Fragment() {

    private lateinit var binding: FragmentFriendsBinding
    private val viewModel: FriendsViewModel by viewModels()
    private val usersSharedViewModel: UsersSharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFriendsBinding.inflate(inflater)

        val sharedPreferences =
            requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val idToken = sharedPreferences.getString("idToken", "")
        val email = sharedPreferences.getString("email", "")

        val friendsRecycler = binding.friendsFragmentFriendsRV
        friendsRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val friendsAdapter = FriendsAdapter(requireContext(), emptyList()) { selectedUser ->
            Toast.makeText(
                requireContext(),
                "Friend Selected: " + selectedUser.firstName,
                Toast.LENGTH_SHORT
            ).show()

            usersSharedViewModel.sendUser(selectedUser)
            println("friend selected | $selectedUser")
            findNavController().navigate(R.id.friendsProfileFragment)

        }

        if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
            viewModel.loadFriends(idToken, email)
            viewModel.friendsList.observe(viewLifecycleOwner) { friendsList ->
                friendsAdapter.dataset = friendsList
                friendsRecycler.adapter = friendsAdapter
            }
        }

        return binding.root
    }

}