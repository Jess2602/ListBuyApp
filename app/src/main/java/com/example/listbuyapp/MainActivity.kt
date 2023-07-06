package com.example.listbuyapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.listbuyapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

enum class ProviderType {
    BASIC,
    GOOGLE
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val bundle = intent.extras
        val email = bundle?.getString("email")
        val uid = bundle?.getString("uid")
        val provider = bundle?.getString("provider")


        val prefs =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("uid", uid)
        prefs.putString("provider", provider)
        prefs.apply()

        // Configurar Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        drawerLayout = binding.drawerLayout
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        binding.navView.setNavigationItemSelectedListener { item ->
            item.isChecked = true

            when (item.itemId) {
                R.id.home -> replaceFragment(homeFragment(), item.title.toString())
                R.id.history -> replaceFragment(historyFragment(), item.title.toString())
                R.id.categories -> replaceFragment(categoriesFragment(), item.title.toString())
                R.id.logOut -> {
                    val prefs = getSharedPreferences(
                        getString(R.string.prefs_file),
                        Context.MODE_PRIVATE
                    ).edit()
                    prefs.clear()
                    prefs.apply()
                    FirebaseAuth.getInstance().signOut()
                    onBackPressed()
                }
            }
            true
        }

        val headerView: View = binding.navView.getHeaderView(0)
        val emailc: TextView = headerView.findViewById(R.id.emailTextView)
        val namec: TextView = headerView.findViewById(R.id.nameTextView)

        val name = firebaseUser?.displayName.takeIf { it?.isNotEmpty() == true } ?: "User"
        namec.text = name
        emailc.text = email

        replaceFragment(homeFragment(), "Home")
    }

    private fun replaceFragment(fragment: Fragment, title: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
        drawerLayout.closeDrawer(binding.navView)
        setTitle(title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
