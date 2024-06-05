package com.example.firebase

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityAddProductBinding
import com.example.firebase.databinding.ActivityUpdateProductBinding
import com.example.firebase.model.ProductModel
import com.google.firebase.database.FirebaseDatabase

class UpdateProductActivity : AppCompatActivity() {
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref= firebaseDatabase.reference.child("products")
    lateinit var updateProductBinding: ActivityUpdateProductBinding
    var id=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updateProductBinding=ActivityUpdateProductBinding.inflate(layoutInflater)

        setContentView(updateProductBinding.root)

        var product:ProductModel?=intent.getParcelableExtra("product")
        id=product?.id.toString()


        updateProductBinding.productNameUpdateId.setText(product?.name)
        updateProductBinding.productPriceUpdateId.setText(product?.price).toString()
        updateProductBinding.productListUpdateId.setText(product?.list)

        updateProductBinding.UpdateButtonId.setOnClickListener {
            var updatedName: String = updateProductBinding.productNameUpdateId.text.toString()
            var updatedPrice: Int =
                updateProductBinding.productPriceUpdateId.text.toString().toInt()
            var updatedlist: String = updateProductBinding.productListUpdateId.text.toString()


            var data = mutableMapOf<String, Any>()
            data["name"] = updatedName
            data["price"] = updatedPrice
            data["list"] = updatedlist

            ref.child(id).updateChildren(data).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(applicationContext, "Data upated", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(
                        applicationContext, it.exception?.message, Toast.LENGTH_LONG
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