package cat.insVidreres.socialpostphone.imp.search

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.FragmentSearchBinding
import cat.insVidreres.socialpostphone.imp.entity.User
import cat.insVidreres.socialpostphone.imp.mainActivity.UsersSharedViewModel

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: ViewModelSearch by viewModels()
    private val usersSharedViewModel: UsersSharedViewModel by activityViewModels()
    private lateinit var buscarAdapter: SearchAdapter
    private var originalUserList: List<User> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate el layout para este fragmento
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val idToken = sharedPreferences.getString("idToken", "")
        val email = sharedPreferences.getString("email", "")

        val buscarRecycler = binding.reciclerViewBuscar
        buscarRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val searchEditText = binding.searchEditText

        searchEditText.addTextChangedListener { text ->
            val filteredList = if (text.isNullOrEmpty()) {
                originalUserList
            } else {
                originalUserList.filter { user ->
                    user.firstName!!.contains(text.toString(), ignoreCase = true)
                }
            }
            buscarAdapter.dataset = filteredList
            buscarAdapter.notifyDataSetChanged()
        }

        buscarAdapter = SearchAdapter(requireContext(), emptyList()) { selectedUser ->
            Toast.makeText(
                requireContext(),
                "User Selected: " + selectedUser.firstName,
                Toast.LENGTH_SHORT
            ).show()

            usersSharedViewModel.sendUser(selectedUser)
            println("User selected | $selectedUser")
            binding.searchEditText.text.clear()
            findNavController().navigate(R.id.action_searchFragment_to_searchProfileFragment)
        }

        if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
            viewModel.loadUsers(idToken, email)
            viewModel.userList.observe(viewLifecycleOwner) { userList ->
                originalUserList = userList // Guardar la lista original
                buscarAdapter.dataset = userList
                buscarRecycler.adapter = buscarAdapter
            }
        }

        return binding.root
    }
}
