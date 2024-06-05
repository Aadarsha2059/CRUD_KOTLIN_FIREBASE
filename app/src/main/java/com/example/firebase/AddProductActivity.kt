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
import com.example.firebase.model.ProductModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID

class AddProductActivity : AppCompatActivity() {
    var firebaseDatabase: FirebaseDatabase= FirebaseDatabase.getInstance()


    var ref= firebaseDatabase.reference.child("products")

    var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance();
    var storageRef: StorageReference = firebaseStorage.reference

    lateinit var addProductBinding: ActivityAddProductBinding


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
                        Picasso.get().load(it).into(addProductBinding.imageBrowse)
                    }
                }

            })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Binding
        addProductBinding=ActivityAddProductBinding.inflate(layoutInflater)

        setContentView(addProductBinding.root)

        //from gist file week eight lesson 2

        registerActivityForResult()


        addProductBinding.imageBrowse.setOnClickListener{
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

        addProductBinding.addButtonId.setOnClickListener{
            if(imageUri!=null){
                uploadImage()
            }else{
                Toast.makeText(applicationContext,"Please upload image first",
                    Toast.LENGTH_LONG).show()
            }
        }

}
    fun uploadImage(){
        val imageName=UUID.randomUUID().toString()
        //ram
        var imageReference= storageRef.child("products").child(imageName)



        imageUri?.let {url->
            imageReference.putFile(url).addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener {downloadUrl->
                    var imagesUrl =downloadUrl.toString()
                    addProduct(imagesUrl)

                }

            }.addOnFailureListener{

            }

        }



    }

    fun addProduct(url:String){
        var name:String= addProductBinding.productNameId.text.toString()
        var price:String= addProductBinding.productPriceId.text.toString()
        var list:String=addProductBinding.productListId.text.toString()

        //to generate id
        var id=ref.push().key.toString()

        //to place value in the previously made model
        var data= ProductModel(id,name,price,list,url)

        ref.child(id).setValue(data).addOnCompleteListener {
        }

        addProductBinding.addButtonId.setOnClickListener{
            var name:String= addProductBinding.productNameId.text.toString()
            var price:String= addProductBinding.productPriceId.text.toString()
            var list:String=addProductBinding.productListId.text.toString()

            //to generate id
            var id=ref.push().key.toString()

            //to place value in the previously made model
            var data= ProductModel(id,name,price,list)

            ref.child(id).setValue(data).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(applicationContext,"Data added",Toast.LENGTH_LONG).show()
                    finish()
                }else{
                    Toast.makeText(applicationContext,it.exception?.message
                        ,Toast.LENGTH_LONG).show()

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