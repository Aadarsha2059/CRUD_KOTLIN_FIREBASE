package com.example.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.example.firebase.model.ProductModel
import com.example.repository.ProductRepository

class ProductViewModel(val repository: ProductRepository): ViewModel() {

    fun addProduct(productModel: ProductModel, callback:(Boolean, String?)->Unit){
        repository.addProduct(productModel,callback)

        fun uploadImage(imageUrl: Uri, callback: (Boolean, String?, String?) -> Unit){
            repository.uploadImage(imageUrl){
                success,imageUrl,imageName->callback(success,imageUrl,imageName)
            }
        }

    }
}