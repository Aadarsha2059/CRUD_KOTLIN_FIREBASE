package com.example.repository

import android.net.Uri
import android.widget.Toast
import com.example.firebase.model.ProductModel
import com.google.firebase.database.FirebaseDatabase
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

        }   }

