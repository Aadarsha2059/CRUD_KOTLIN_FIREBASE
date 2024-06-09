package com.example.firebase.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.firebase.R
import com.example.firebase.UpdateProductActivity
import com.example.firebase.model.ProductModel
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class ProductAdapter(var context: Context, var data:ArrayList<ProductModel>):RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var productName: TextView = view.findViewById(R.id.productName1)
        var productPrice: TextView = view.findViewById(R.id.productPrice2)


        var progressBar: ProgressBar=view.findViewById(R.id.progressBar)
        var imageView: ImageView=view.findViewById(R.id.imageView)
        var productList: TextView = view.findViewById(R.id.productList3)
        var UpdateButtonId:TextView=view.findViewById(R.id.btnEdit)


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        var view= LayoutInflater.from(parent.context).inflate(R.layout.sample_product,parent,false)

        //returning productviewholder and view
         return ProductViewHolder(view)

    }

    override fun getItemCount(): Int {
        return data.size;
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.productName.text=data[position].name
        holder.productPrice.text=data[position].price.toString()
        holder.productList.text=data[position].list
        var image=data[position].url

        Picasso.get().load(image).into(holder.imageView,object :Callback{
            override fun onSuccess() {
                holder.progressBar.visibility=View.INVISIBLE
            }

            override fun onError(e: Exception?) {
                Toast.makeText(context,e?.localizedMessage,Toast.LENGTH_LONG).show()
            }
        })


        holder.UpdateButtonId.setOnClickListener{
            var intent= Intent(context,UpdateProductActivity::class.java)
            intent.putExtra("product",data[position])
            context.startActivity(intent)

            context.startActivity(intent)
        }
    }

    fun getProductId(position: Int):String{
        return data[position].id
    }
    fun getImageName(position:Int):String{
        return data[position].imageName

    }



}
