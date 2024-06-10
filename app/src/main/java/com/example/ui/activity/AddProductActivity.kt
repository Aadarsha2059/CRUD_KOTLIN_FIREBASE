package com.example.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.firebase.databinding.ActivityAddProductBinding
import com.example.firebase.model.ProductModel
import com.example.utils.ImageUtils
import com.example.viewmodel.ProductViewModel
import com.squareup.picasso.Picasso

class AddProductActivity : AppCompatActivity() {

    lateinit var addProductBinding: ActivityAddProductBinding


    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    var imageUri: Uri? = null

    lateinit var imageUtlis: ImageUtils
    lateinit var productViewModel: ProductViewModel

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResultLauncher.launch(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        //Binding
        addProductBinding = ActivityAddProductBinding.inflate(layoutInflater)

        setContentView(addProductBinding.root)


        //.let is used so that nullable cant be executed

        imageUtlis = ImageUtils(this)
        imageUtlis.registerActivityResult { url ->
            url.let {
                imageUri = it
                Picasso.get().load(url).into(addProductBinding.imageBrowse)


            }

        }

        //from gist file week eight lesson 2


        addProductBinding.imageBrowse.setOnClickListener {
            imageUtlis.LaunchGallery(this)
        }

        addProductBinding.addButtonId.setOnClickListener {
            if (imageUri != null) {
                uploadImage()
            } else {
                Toast.makeText(
                    applicationContext, "Please upload image first",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }
    fun uploadImage() {


        imageUri?.let { url ->
            productViewModel.uploadImage(url)
            { success, imageName, imageUrl ->

                if (success) {
                    addProduct(imageUri.toString(),imageName.toString())
                }
            }
        }


    }

    fun addProduct(url: String, imagesName: String) {
        var name: String = addProductBinding.productNameId.text.toString()
        var price: String = addProductBinding.productPriceId.text.toString()
        var list: String = addProductBinding.productListId.text.toString()

        var data = ProductModel("", name, price, list, url, imagesName)
        productViewModel.addProduct(data) { success, message ->
            if(success){
                Toast.makeText(applicationContext,message,
                    Toast.LENGTH_LONG).show()

            }else{
                Toast.makeText(applicationContext,message,
                    Toast.LENGTH_LONG).show()
            }
        }


    }

}


