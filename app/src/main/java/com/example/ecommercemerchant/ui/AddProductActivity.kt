package com.example.ecommercemerchant.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ecommercemerchant.R
import com.example.ecommercemerchant.databinding.ActivityAddBannerBinding
import com.example.ecommercemerchant.databinding.ActivityMainBinding
import com.example.ecommercemerchant.utils.AddingTypeBottomSheet
import com.example.ecommercemerchant.utils.CATEGORY
import com.example.ecommercemerchant.utils.PRODUCT
import com.example.ecommercemerchant.utils.showAddCategoryDialog

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

    private fun activityOnClick(){
        binding.addCategory.setOnClickListener{

        }
    }

}