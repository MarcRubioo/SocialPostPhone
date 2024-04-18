package cat.insVidreres.socialpostphone.imp.profile

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.FragmentProfileBinding


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

        if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
            viewModel.loadUser(idToken, email)

//            viewModel.user.observe(viewLifecycleOwner) { user ->
//                binding.userName = user.firstName
//                binding.email = user.email
//                binding.phone = user.phoneNumber
//                binding.img = user.img
//                binding.age = user.age
//            }
        }

        return binding.root
    }

}