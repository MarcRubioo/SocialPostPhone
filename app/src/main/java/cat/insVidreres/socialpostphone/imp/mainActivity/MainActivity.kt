package cat.insVidreres.socialpostphone.imp.mainActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import cat.insVidreres.socialpostphone.imp.R
import cat.insVidreres.socialpostphone.imp.databinding.ActivityMainBinding
import cat.insVidreres.socialpostphone.imp.databinding.ActivityRegisterBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.Container) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNavigationView
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }
}