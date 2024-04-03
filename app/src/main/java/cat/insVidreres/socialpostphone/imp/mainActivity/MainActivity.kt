package cat.insVidreres.socialpostphone.imp.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.ActivityMainBinding
import cat.insVidreres.socialpostphone.imp.databinding.ActivityRegisterBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}