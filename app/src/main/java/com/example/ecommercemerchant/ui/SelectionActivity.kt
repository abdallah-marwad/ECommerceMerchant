package com.example.ecommercemerchant.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ecommercemerchant.databinding.ActivitySelectionBinding

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBanner.setOnClickListener {
            startActivity(Intent(this  , AddBannerActivity::class.java))
        }
        binding.buttonAddProduct.setOnClickListener {
            startActivity(Intent(this  , AddProductActivity::class.java))
        }
        binding.buttonAddCategory.setOnClickListener {
            startActivity(Intent(this  , AddCategoryActivity::class.java))
        }
    }
}