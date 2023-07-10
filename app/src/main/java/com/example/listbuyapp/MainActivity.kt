package com.example.listbuyapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.listbuyapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

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
                R.id.categories -> replaceFragment(categoriesFragment(), item.title.toString())
                R.id.history -> replaceFragment(historyFragment(), item.title.toString())
                R.id.logOut -> {


                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle("Desea cerrar sesión?")
                    alertDialogBuilder.setMessage("Cerrar")
                    alertDialogBuilder.setPositiveButton("Si") { dialog, _ ->
                        dialog.dismiss()
                        val prefs = getSharedPreferences(
                            getString(R.string.prefs_file),
                            Context.MODE_PRIVATE
                        ).edit()
                        prefs.clear()
                        prefs.apply()
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    }
                    alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }

                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()

                    val positiveButton =
                        alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                    positiveButton.setTextColor(ContextCompat.getColor(this, R.color.botonbase))
                    val negativeButton =
                        alertDialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE)
                    negativeButton.setTextColor(ContextCompat.getColor(this, R.color.botonbase))

                    alertDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)
                }
            }
            true
        }

        val headerView: View = binding.navView.getHeaderView(0)
        val emailc: TextView = headerView.findViewById(R.id.emailTextView)
        val namec: TextView = headerView.findViewById(R.id.nameTextView)

        if (firebaseUser != null && firebaseUser.photoUrl != null) {
            val photoUrl: String = firebaseUser.photoUrl.toString()
            val imageView: CircleImageView = headerView.findViewById(R.id.userImageView)

            Picasso.get().load(photoUrl).into(imageView)
        }

        val name = firebaseUser?.displayName.takeIf { it?.isNotEmpty() == true } ?: "!Hola¡ Usuario"
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
