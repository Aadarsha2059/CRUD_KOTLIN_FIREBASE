package com.example.repository

import android.net.Uri
import com.example.firebase.model.ProductModel

interface ProductRepository {

    //void in kotlin as unit

    fun uploadImage(imageUrl: Uri, callback: (Boolean, String?, String?) -> Unit)

    fun addProduct(productModel: ProductModel, callback:(Boolean,String?)->Unit)

    fun getAllProduct(callback:(List<ProductModel>?,Boolean,String?)->Unit)

    fun updateProduct(id:String,callback: (Boolean, String?) -> Unit)

    fun deleteData(id:String,callback: (Boolean, String?) -> Unit)

    fun deleteImage(imageName:String,callback: (Boolean, String?) -> Unit)


}