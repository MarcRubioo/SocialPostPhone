package cat.insVidreres.socialpostphone.imp.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.ActivityRegisterBinding
import cat.insVidreres.socialpostphone.imp.login.LoginActivity


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: ViewModelRegistre by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
        setupObservers()

    }

    private fun setup() {
        binding.buttonLogin.setOnClickListener {

            if (binding.editTextTextRegisterName.text.isNotBlank() && binding.editTextRegisterEmail.text.isNotBlank() && binding.editTextRegisterPassword.text.isNotBlank() ) {
                viewModel.registerUser(
                    binding.editTextRegisterEmail.text.toString(),
                    binding.editTextRegisterPassword.text.toString(),
                    binding.editTextTextRegisterName.text.toString(),

                )
            } else {
                Toast.makeText(this, "Llena todos los campos ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.registerSucces.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Registro Correcto", Toast.LENGTH_SHORT).show()
                goLogin()
            }
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun goLogin() {
        val intentToLogin = Intent(this, LoginActivity::class.java)
        startActivity(intentToLogin)
    }

}