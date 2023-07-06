package com.example.listbuyapp

import android.content.Context
import android.content.Intent
import android.net.Uri.Builder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.listbuyapp.databinding.ActivityLoginBinding
import com.example.listbuyapp.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



        setUp()
        session()

    }

    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val uid = prefs.getString("uid", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null&& uid != null) {
            showHome(email, uid ,ProviderType.valueOf(provider))
        }
    }

    private fun setUp() {
        binding.SingUp.setOnClickListener {
            if (binding.TextFieldEmail.text.toString()
                    .isNotEmpty() && binding.TextFieldPass.text.toString().isNotEmpty()
            ) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.TextFieldEmail.text.toString(),
                    binding.TextFieldPass.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "",it.result?.user?.uid ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }

            } else {
                showAlertEmpty()
            }
        }
        binding.SingIn.setOnClickListener {
            if (binding.TextFieldEmail.text.toString()
                    .isNotEmpty() && binding.TextFieldPass.text.toString().isNotEmpty()
            ) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.TextFieldEmail.text.toString(),
                    binding.TextFieldPass.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(it.result?.user?.email ?: "",it.result?.user?.uid ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }

            } else {
                showAlertEmpty()
            }
        }
        binding.SingGoogle.setOnClickListener {
            val googleconf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleconf)

            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)


        }
    }

    private fun showHome(email: String,uid: String, provider: ProviderType) {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("uid", uid)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setTitle("Error")
        builder.setMessage("Se ha Producido un error autenticando el Usuario")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.botonbase))
    }

    private fun showAlertEx(error: String) {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setTitle("Error")
        builder.setMessage(error)
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.botonbase))
    }

    private fun showAlertEmpty() {
        val builder = AlertDialog.Builder(this, R.style.AlertDialogCustom)
        builder.setTitle("Error")
        builder.setMessage("Los Campos están Vacíos")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.botonbase))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener { signInTask ->
                            if (signInTask.isSuccessful) {
                                val firebaseUser = FirebaseAuth.getInstance().currentUser
                                val uid = firebaseUser?.uid ?: ""
                                showHome(account.email ?: "", uid, ProviderType.GOOGLE)
                            } else {
                                showAlert()
                            }
                        }

                }
            } catch (e: ApiException) {
              showAlert()
            }
        }
    }

}