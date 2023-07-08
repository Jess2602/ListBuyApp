package com.example.listbuyapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.listbuyapp.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    private val GOOGLE_SIGN_IN = 100
    lateinit var binding: ActivityLoginBinding
    private val db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.TextFieldEmail.doOnTextChanged { text, _, _, _ ->
            val email = text.toString().trim()

            if (email.isNotEmpty() && !isValidEmail(email)) {
                binding.TextFieldEmail.error = "Email inválido"
            } else {
                binding.TextFieldEmail.error = null
            }
        }
        setUp()
        session()

    }
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
    private fun session() {
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val uid = prefs.getString("uid", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null && uid != null) {
            showHome(email, uid, ProviderType.valueOf(provider))
        }
    }

    private fun setUp() {
        binding.SingUp.setOnClickListener {
            if (binding.TextFieldEmail.text.toString()
                    .isNotEmpty() && binding.TextFieldPass.text.toString().isNotEmpty()
            ) {
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Registrando Usuario..")
                progressDialog.setCancelable(false)
                progressDialog.show()
                progressDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)

                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    binding.TextFieldEmail.text.toString(),
                    binding.TextFieldPass.text.toString()
                ).addOnCompleteListener { task ->
                    progressDialog.dismiss()

                    if (task.isSuccessful) {
                        val user = task.result?.user

                        val usersData = hashMapOf(
                            ("name" to user?.email ?: "") as Pair<Any, Any>,
                            ("email" to user?.uid ?: "") as Pair<Any, Any>,
                            "provider" to ProviderType.BASIC,
                        )

                        db.collection("Users").document(user?.email ?: "")
                            .set(usersData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    applicationContext,
                                    "Cuenta Creada",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    applicationContext,
                                    "No se Guardaron los Datos Correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        showHome(user?.email ?: "", user?.uid ?: "", ProviderType.BASIC)
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
                val progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Iniciando sesión..")
                progressDialog.setCancelable(false)
                progressDialog.show()
                progressDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                    binding.TextFieldEmail.text.toString(),
                    binding.TextFieldPass.text.toString()
                ).addOnCompleteListener { task ->
                    progressDialog.dismiss()

                    if (task.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Bienvenido Usuario",
                            Toast.LENGTH_SHORT
                        ).show()

                        val user = task.result?.user

                        showHome(user?.email ?: "", user?.uid ?: "", ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            } else {
                showAlertEmpty()
            }
        }

        binding.SingGoogle.setOnClickListener {
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)

            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)
        }
    }


    private fun showHome(email: String, uid: String, provider: ProviderType) {
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("uid", uid)
            putExtra("provider", provider.name)
        }
        startActivity(homeIntent)
        finish()
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha Producido un error autenticando el Usuario")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.botonbase))
        dialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)
    }

    private fun showAlertEmpty() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Los Campos están Vacíos")
        builder.setPositiveButton("Aceptar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(this, R.color.botonbase))
        dialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val progressDialog = ProgressDialog(this)
                    progressDialog.setMessage("Iniciando sesión con Google...")
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    progressDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)

                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener { signInTask ->
                            progressDialog.dismiss()

                            if (signInTask.isSuccessful) {
                                val firebaseUser = FirebaseAuth.getInstance().currentUser
                                val uid = firebaseUser?.uid ?: ""

                                val userRef = db.collection("Users").document(account.email ?: "")
                                userRef.get().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val document = task.result
                                        if (document != null && document.exists()) {
                                            Toast.makeText(
                                                applicationContext,
                                                "Bienvenido Usuario",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            val usersData = hashMapOf(
                                                ("name" to account.email ?: "") as Pair<Any, Any>,
                                                "uid" to uid,
                                                "provider" to ProviderType.BASIC,
                                            )

                                            db.collection("Users").document(account.email ?: "")
                                                .set(usersData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "Cuenta Creada",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                .addOnFailureListener {
                                                    Toast.makeText(
                                                        applicationContext,
                                                        "No se Guardaron los Datos Correctamente",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                        }
                                    }
                                }

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