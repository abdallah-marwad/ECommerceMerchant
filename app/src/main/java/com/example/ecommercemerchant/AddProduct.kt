package com.example.ecommercemerchant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddProduct : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private var parentJob: Job = Job()
    private var coroutineScope: CoroutineScope = CoroutineScope(parentJob + Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getCategories()


    }

    private fun getCategories() = coroutineScope.launch {
        val collectionReference = firestore.collection("category")

        val documentReference = collectionReference.document("main_category")

        documentReference.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val yourData = document.toObject(String::class.java)


                } else {
                    Toast.makeText(this@AddProduct ,"You should add category" , Toast.LENGTH_SHORT ).show()
                }
            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }
    private fun addCategory(){
        showResetPasswordDialog{
            val collectionReference = firestore.collection("category")

            val documentReference = collectionReference.document("main_category")

            documentReference.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val yourData = document.toObject(String::class.java)


                    } else {
                        Toast.makeText(this@AddProduct ,"You should add category" , Toast.LENGTH_SHORT ).show()
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle errors
                }
        }
        }

}