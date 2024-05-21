package cat.insVidreres.socialpostphone.imp.userDetails

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import cat.insVidreres.socialpostphone.imp.databinding.FragmentUserDetailsBinding
import cat.insVidreres.socialpostphone.imp.entity.User
import cat.insVidreres.socialpostphone.imp.login.LoginActivity
import com.bumptech.glide.Glide


class UserDetailsFragment : Fragment() {
    private lateinit var binding: FragmentUserDetailsBinding
    private val viewModel: UserDetailsViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var email: String
    private lateinit var idToken: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserDetailsBinding.inflate(inflater)

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

        if (!idToken.isNullOrEmpty() && !email.isNullOrEmpty()) {
            var userReceived: User? = null;
            viewModel.loadUser(idToken, email)


            viewModel.user.observe(viewLifecycleOwner) { user ->
                binding.textName.text = user.firstName
                binding.textEmail.text = user.email
                binding.textAge.text = user.age.toString()
                Glide.with(binding.profileFragmentUserIV.context).load(user.img).into(binding.profileFragmentUserIV)


            }
        }

        binding.cerrarSesion.setOnClickListener {
            viewModel.logoutUser{ success ->
                if (success) {
                    goLogin()
                }
            }
        }


        return binding.root
    }

    private fun goLogin() {
        val homeIntent = Intent(requireContext(), LoginActivity::class.java).apply {}
        startActivity(homeIntent)
    }

}