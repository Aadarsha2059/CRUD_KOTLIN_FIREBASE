package com.example.repository

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebase.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class ProductRepositoryImpl:ProductRepository {

    // instances from product activity
    var firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()


    var ref= firebaseDatabase.reference.child("products")

    var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance();
    var storageRef: StorageReference = firebaseStorage.reference.child("products")
    override fun uploadImage(imageUrl: Uri, callback: (Boolean, String?, String?) -> Unit) {
        val imageName= UUID.randomUUID().toString()

        var imageReference= storageRef.child(imageName)



        imageUrl?.let {url->
            imageReference.putFile(url).addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener {downloadUrl->
                    var imagesUrl =downloadUrl.toString()

                    callback(true,imagesUrl,imageName)



                }

            }.addOnFailureListener{
                callback(false,"","")

            }

        }
    }

    override fun addProduct(productModel: ProductModel, callback: (Boolean, String?) -> Unit) {

        var id=ref.push().key.toString()

        productModel.id=id

        ref.child(id).setValue(productModel).addOnCompleteListener {
            if(it.isSuccessful){
                callback(true,"Data uploaded successfully")
            }else{
               callback(false,"unable to upload image")
        }


            }

        }

    override fun getAllProduct(callback: (List<ProductModel>?, Boolean, String?) -> Unit) {

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // new variable
                var productList= mutableListOf<ProductModel>()

                for (eachData in snapshot.children) {
                    var product = eachData.getValue(ProductModel::class.java)
                    if (product != null) {
                        productList.add(product)
                    }




                }
                callback(productList,true,"Data successfully retrieved")



            }

            override fun onCancelled(error: DatabaseError) {
                callback(null,false,"Unable to fetch ${error.message}")

            }
        })
    }

    override fun updateProduct(id: String, callback: (Boolean, String?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteData(id: String, callback: (Boolean, String?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteImage(imageName: String, callback: (Boolean, String?) -> Unit) {
        TODO("Not yet implemented")
    }
}

