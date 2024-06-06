package com.example.firebase

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityAddProductBinding
import com.example.firebase.databinding.ActivityUpdateProductBinding
import com.example.firebase.model.ProductModel
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class UpdateProductActivity : AppCompatActivity() {
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref= firebaseDatabase.reference.child("products")
    lateinit var updateProductBinding: ActivityUpdateProductBinding
    var id=""

    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    var imageUri : Uri? = null



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }



    fun registerActivityForResult(){
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {result ->

                val resultcode = result.resultCode
                val imageData = result.data
                if(resultcode == RESULT_OK && imageData != null){
                    imageUri = imageData.data

                    //imageuri stores the chosen image
                    imageUri?.let {
                        Picasso.get().load(it).into(updateProductBinding.imageUpdate)
                    }
                }

            })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updateProductBinding=ActivityUpdateProductBinding.inflate(layoutInflater)

        setContentView(updateProductBinding.root)

        registerActivityForResult()

        var product:ProductModel?=intent.getParcelableExtra("product")
        id=product?.id.toString()


        updateProductBinding.productNameUpdateId.setText(product?.name)
        updateProductBinding.productPriceUpdateId.setText(product?.price).toString()
        updateProductBinding.productListUpdateId.setText(product?.list)

        updateProductBinding.UpdateButtonId.setOnClickListener {

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    //creating function and adding the the parameters from above
    fun updateProduct(){
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
}