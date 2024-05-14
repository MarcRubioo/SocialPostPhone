package cat.insVidreres.socialpostphone.imp.addpost

import AddPostViewModel
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.viewModels
import cat.insVidreres.socialpostphone.imp.databinding.FragmentAddPostBinding
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AddPostFragment : Fragment() {
    private lateinit var binding: FragmentAddPostBinding
    private val viewModel: AddPostViewModel by viewModels()
    private lateinit var auth: FirebaseAuth

    private var imageUri: Uri? = null

//    private val resultLauncher = registerForActivityResult(
//        ActivityResultContracts.GetContent()){
//        viewModel.setImageUri(it)
//        imageUri = it
//        binding.imageSelector.setImageURI(it)
//    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            val inputStream = context?.contentResolver?.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            bytes?.let {
                viewModel.byteArrayList.add(it)
            }
        }
        binding.imageSelector.setImageURI(uris[0])
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddPostBinding.inflate(inflater)


        val sharedPreferences =
            requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val idToken = sharedPreferences.getString("idToken", "")

        auth = FirebaseAuth.getInstance()
        var userLog = auth.currentUser

        binding.imageSelector.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        if (idToken != null) {
            viewModel.loadCategories(idToken)
        }

        //Cargar categorias
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.chipGroupCategories.removeAllViews()
            categories.forEach { category ->
                val chip = Chip(requireContext())
                chip.text = category
                chip.isCheckable = true
                binding.chipGroupCategories.addView(chip)
            }
        }

        binding.btnAddPost.setOnClickListener {
            if (binding.editTextDescription.text.isNotBlank()) {
                if (userLog != null) {

                    if (idToken != null) {

                        val selectedCategories = mutableListOf<String>()

                        for (i in 0 until binding.chipGroupCategories.childCount) {
                            val chip = binding.chipGroupCategories.getChildAt(i) as Chip

                            if (chip.isChecked) {

                                selectedCategories.add(chip.text.toString())
                            }
                        }


                        viewModel.addPost(
                            idToken,
                            viewModel.generarCodigoAleatorio(),
                            userLog.email.toString(),
                            currentDate,
                            binding.editTextDescription.text.toString(),
                            selectedCategories
                        ) { success ->
                            if (success) {
                                Toast.makeText(requireContext(), "Post Creado!", Toast.LENGTH_SHORT)
                                    .show()
                                binding.editTextDescription.text.clear()
                                viewModel.byteArrayList.clear()

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Problemas al crear el Post",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                }
            } else {
                Toast.makeText(
                    binding.root.context,
                    "Problemas al crear el Pots ",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return binding.root
    }
}