package com.example.ui.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.R
import com.example.firebase.databinding.ActivityVerificationBinding
import com.google.firebase.auth.FirebaseAuth

class Verification : AppCompatActivity() {
    lateinit var verificationBinding: ActivityVerificationBinding

    var auth:FirebaseAuth=FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        verificationBinding=ActivityVerificationBinding.inflate(layoutInflater)

        setContentView(verificationBinding.root)

        verificationBinding.verifyID.setOnClickListener {
            var email:String=verificationBinding.emailIDverify.text.toString()

            auth.sendPasswordResetEmail(email).
            addOnCompleteListener{
                if(it.isSuccessful){
                    Toast.makeText(applicationContext,
                        "Registration succeed", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(applicationContext,
                        it.exception?.message, Toast.LENGTH_LONG).show()
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