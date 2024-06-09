package com.example.firebase

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firebase.databinding.ActivityAddProductBinding
import com.example.firebase.databinding.ActivityUpdateProductBinding
import com.example.firebase.model.ProductModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID

class UpdateProductActivity : AppCompatActivity() {
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref= firebaseDatabase.reference.child("products")
    lateinit var updateProductBinding: ActivityUpdateProductBinding
    var id=""
    var imageName=""

    var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance();
    var storageRef: StorageReference = firebaseStorage.reference


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

                    updateProductBinding.UpdateButtonId.setOnClickListener {
                        uploadImage()
                    }
                    updateProductBinding.imageUpdate.setOnClickListener {
                        var permissions = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                            android.Manifest.permission.READ_MEDIA_IMAGES
                        }else{
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                        if (ContextCompat.checkSelfPermission(this,permissions) != PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(this, arrayOf(permissions),1)
                        }else{
                            val intent = Intent()
                            intent.type = "image/*"
                            intent.action = Intent.ACTION_GET_CONTENT
                            activityResultLauncher.launch(intent)
                        }

                    }

                    }
                }

            })
    }

   //upload image from add product
    fun uploadImage(){
        val imageName= UUID.randomUUID().toString()
        //ram
        var imageReference= storageRef.child("products").child(imageName)


        imageUri?.let {url->
            imageReference.putFile(url).addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener {downloadUrl->
                    var imagesUrl =downloadUrl.toString()

                    updateProduct(imagesUrl)

                }

            }.addOnFailureListener{
                Toast.makeText(applicationContext,it.localizedMessage,
                    Toast.LENGTH_LONG).show()

            }

        }



    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updateProductBinding=ActivityUpdateProductBinding.inflate(layoutInflater)

        setContentView(updateProductBinding.root)

        registerActivityForResult()

        var product:ProductModel?=intent.getParcelableExtra("product")
        id=product?.id.toString()

        imageName=product?.imageName.toString()


        updateProductBinding.productNameUpdateId.setText(product?.name)
        updateProductBinding.productPriceUpdateId.setText(product?.price).toString()
        updateProductBinding.productListUpdateId.setText(product?.list)

        //calling picasso to load images
        Picasso.get().load(product?.url).into(updateProductBinding.imageUpdate)

        updateProductBinding.UpdateButtonId.setOnClickListener {

        }

        //image browse id to update the new image
        updateProductBinding.imageUpdate.setOnClickListener{
            var permissions = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                android.Manifest.permission.READ_MEDIA_IMAGES
            }else{
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            }
            if (ContextCompat.checkSelfPermission(this,permissions) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(permissions),1)
            }else{
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                activityResultLauncher.launch(intent)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    //creating function and adding the the parameters from above
    fun updateProduct(url: String){
        var updatedName: String = updateProductBinding.productNameUpdateId.text.toString()
        var updatedPrice: Int =
            updateProductBinding.productPriceUpdateId.text.toString().toInt()
        var updatedlist: String = updateProductBinding.productListUpdateId.text.toString()


        var data = mutableMapOf<String, Any>()
        data["name"] = updatedName
        data["price"] = updatedPrice
        data["list"] = updatedlist
        data["url"]=url

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