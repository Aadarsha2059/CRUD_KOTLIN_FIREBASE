package com.example.ui.activity

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
import com.example.firebase.R
import com.example.firebase.databinding.ActivityUpdateProductBinding
import com.example.firebase.model.ProductModel
import com.example.repository.ProductRepositoryImpl
import com.example.utils.ImageUtils
import com.example.viewmodel.ProductViewModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID

class UpdateProductActivity : AppCompatActivity() {


    lateinit var updateProductBinding: ActivityUpdateProductBinding
    var id=""
    var imageName=""




    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    var imageUri : Uri? = null

    lateinit var productViewModel: ProductViewModel



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





   //upload image from add product
    fun uploadImage(){
        imageUri?.let {
            productViewModel.uploadImage(imageName,it){
                success,imageUrl->
                if(success){
                    updateProduct(imageUrl.toString())

                }

            }

        }






    }

    lateinit var imageUtils: ImageUtils
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        updateProductBinding=ActivityUpdateProductBinding.inflate(layoutInflater)

        setContentView(updateProductBinding.root)

        var repo=ProductRepositoryImpl()
        productViewModel= ProductViewModel(repo)

        imageUtils = ImageUtils(this)
            imageUtils.registerActivityResult {
                imageUri = it
                Picasso.get().load(it).into(updateProductBinding.imageUpdate)

            }


//        registerActivityForResult()

        var product:ProductModel?=intent.getParcelableExtra("product")
        id=product?.id.toString()


        //to store the image from database
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
            imageUtils.LaunchGallery(this@UpdateProductActivity)
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

       productViewModel.updateProduct(id,data){
           success,message->
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