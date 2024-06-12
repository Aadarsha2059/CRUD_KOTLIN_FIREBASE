package com.example.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.firebase.model.ProductModel
import com.example.repository.ProductRepository


class ProductViewModel(val repository: ProductRepository): ViewModel() {
    fun updateProduct(id:String,data:MutableMap<String,Any>?,callback: (Boolean, String?) -> Unit){
        repository.updateProduct(id,data,callback)
    }

    fun addProduct(productModel: ProductModel, callback:(Boolean, String?)->Unit){
        repository.addProduct(productModel,callback)



    }
    fun uploadImage(imageName:String, imageUrl: Uri, callback: (Boolean, String?) -> Unit){
        repository.uploadImage(imageName,imageUrl){
                success,imageUrl->
            callback(success,imageUrl)
        }
    }


    var _productList=MutableLiveData<List<ProductModel>?>()

    var productList=MutableLiveData<List<ProductModel>?>()
        get()=_productList


    var _loadingState =MutableLiveData<Boolean>()
    var loadingState= MutableLiveData<Boolean>()
        get()= _loadingState


    fun fetchProduct(){
        _loadingState.value=true
        repository.getAllProduct { productList, success, message ->
            if(productList!=null){
                _loadingState.value=false
                _productList.value=productList

            }
        }
    }
}