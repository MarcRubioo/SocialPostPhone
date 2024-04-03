package cat.insVidreres.socialpostphone.imp.login

import android.content.Intent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cat.insVidreres.socialpostphone.imp.databinding.ActivityLoginBinding
import cat.insVidreres.socialpostphone.imp.mainActivity.MainActivity
import cat.insVidreres.socialpostphone.imp.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: ViewModelLogin by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setup()
        setupObservers()

        binding.textViewCrearCompte.setOnClickListener{
            goRegister()
        }

    }

    private fun setupObservers() {
        viewModel.loginSuccess.observe(this, Observer { success ->
            if (success) {
                Toast.makeText(this, "Inicio de sesión correcto!", Toast.LENGTH_SHORT).show()
                goMain()
            }
        })

        viewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        })
    }

    private fun setup() {
        binding.buttonToMain.setOnClickListener{
            if (binding.editTextTextLoginEmail.text.isNotBlank() && binding.editTextLoginPassword.text.isNotBlank()) {
                viewModel.loginUser(
                    binding.editTextTextLoginEmail.text.toString(),
                    binding.editTextLoginPassword.text.toString()
                )
            } else {
                Toast.makeText(this, "Llena todos los campos ", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun goMain() {
        val intentToMain = Intent(this, MainActivity::class.java)
        startActivity(intentToMain)
    }

    private fun goRegister() {
        val intentToRegister = Intent(this, RegisterActivity::class.java)
        startActivity(intentToRegister)
    }


}