package com.example.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.R
import com.example.firebase.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainBinding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(mainBinding.root)

//        //intent on forgot id
//        mainBinding.forgetId.setOnClickListener {
//            var intent = Intent(
//                this@MainActivity,
//                Verification::class.java
//            )
//            startActivity(intent)
//        }

        mainBinding.loginId.setOnClickListener {
            var email: String = mainBinding.emailId.text.toString()
            var password: String = mainBinding.passwordId.text.toString()

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "login succeed", Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        it.exception?.message, Toast.LENGTH_LONG
                    ).show()

                    //navigate to dashboard
                    var intent =Intent(this@MainActivity, DashBoardActivity::class.java)
                    startActivity(intent)
                }

            }

            mainBinding.registerId.setOnClickListener {
                var email: String = mainBinding.emailId.text.toString()
                var password: String = mainBinding.passwordId.text.toString()

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Registration succeed", Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            it.exception?.message, Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }
}