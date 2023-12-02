package com.example.ecommercemerchant.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommercemerchant.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddProductActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var parentJob: Job = Job()
    private var coroutineScope: CoroutineScope = CoroutineScope(parentJob + Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
    private fun getCategories() = coroutineScope.launch {
        val collectionReference = firestore.collection("category")
        collectionReference.get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    document.forEach {}
                } else {
                    Toast.makeText(
                        this@AddProductActivity,
                        "You should add category before adding product",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@AddProductActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    private fun activityOnClick(){
        binding.addCategory.setOnClickListener{

        }
    }

}