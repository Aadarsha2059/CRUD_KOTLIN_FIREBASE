package com.example.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.Adapter.ProductAdapter
import com.example.firebase.R
import com.example.firebase.databinding.ActivityDashBoardBinding
import com.example.firebase.model.ProductModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class DashBoardActivity : AppCompatActivity() {
    lateinit var dashBoardBinding: ActivityDashBoardBinding
    var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    var ref: DatabaseReference = database.reference.child("products")

    var firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance();
    var storageRef: StorageReference = firebaseStorage.reference

    //making arraylist of productmodel
    var productList=ArrayList<ProductModel>()
    lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dashBoardBinding = ActivityDashBoardBinding.inflate(layoutInflater)

        setContentView(dashBoardBinding.root)
        productAdapter=ProductAdapter(this@DashBoardActivity,productList)

        ItemTouchHelper(object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var id=productAdapter.getProductId(viewHolder.adapterPosition)

                var imageName=productAdapter.getImageName(viewHolder.adapterPosition)

                //to delete the data from storage and realtime database
                ref.child(id).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) {
                        storageRef.child("products").child(imageName).delete()
                        Toast.makeText(applicationContext,"Data deleted",
                            Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, "Data delete", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(
                            applicationContext, it.exception?.message, Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }).attachToRecyclerView(dashBoardBinding.recyclerViewId)


        //intent for floatingaction button intent
        dashBoardBinding.floatingId.setOnClickListener {
            var intent = Intent(
                this@DashBoardActivity,
                AddProductActivity::class.java
            )
            startActivity(intent)

        }
        //event listener conditions
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (eachData in snapshot.children) {

                    var product = eachData.getValue(ProductModel::class.java)
                    if (product != null) {
                        Log.d("data from firebase", product.name)
                        Log.d("data from firebase", product.list)
                        Log.d("data from firebase", product.price.toString())
                        Log.d("data from firebase", product.id)

                        productList.add(product)
                    }

                    //calling layoutmanager and adapter here


                    dashBoardBinding.recyclerViewId.layoutManager=LinearLayoutManager(this@DashBoardActivity)
                    dashBoardBinding.recyclerViewId.adapter = productAdapter
                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}