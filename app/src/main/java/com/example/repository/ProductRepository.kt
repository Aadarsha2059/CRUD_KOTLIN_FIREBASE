package com.example.repository

import android.net.Uri
import com.example.firebase.model.ProductModel

interface ProductRepository {

    //void in kotlin as unit

    fun uploadImage(imageUrl: Uri, callback: (Boolean, String?, String?) -> Unit)

    fun addProduct(productModel: ProductModel, callback:(Boolean,String?)->Unit)
}