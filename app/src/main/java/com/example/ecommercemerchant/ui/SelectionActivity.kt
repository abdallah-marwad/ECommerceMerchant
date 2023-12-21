package com.example.ecommercemerchant.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.ecommercemerchant.data.firebase_manager.FirebaseManager
import com.example.ecommercemerchant.databinding.ActivitySelectionBinding
import com.example.ecommercemerchant.utils.AddCategoryBottomSheet
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class SelectionActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySelectionBinding
    lateinit var categoryBottomSheet: AddCategoryBottomSheet
    val imageRef = Firebase.storage.reference
    var fileUri: Uri? = null
    private val firestore = FirebaseFirestore.getInstance()


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
            addMainCategoryDialog()
        }
    }


    private fun addMainCategoryDialog() {
        categoryBottomSheet = AddCategoryBottomSheet()
        categoryBottomSheet.showAddCategoryDialog(this)
        { collectionName ->
            binding.progress.visibility = View.VISIBLE
            uploadImages(collectionName)
        }
    }


    private fun uploadImages( imgName: String) {
        if (fileUri == null) {
            binding.progress.visibility = View.GONE
            Toast.makeText(this,
                "Please choose image"
                , Toast.LENGTH_LONG).show()

            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            var ref = imageRef.child("main_categories/$imgName")
            ref.putFile(fileUri!!)
                .addOnSuccessListener {
                    addMainCategory(imgName , runBlocking {ref.downloadUrl.await().toString()})
                }.addOnFailureListener {
                    binding.progress.visibility = View.GONE

                    Toast.makeText(this@SelectionActivity, it.message, Toast.LENGTH_LONG).show()
                }
        }
    }
    private fun addMainCategory(imgName: String , imgUrl: String){

        val firebaseManager = FirebaseManager()
        firebaseManager.addMainCategory(
            imgName,
            imgUrl,
            firestore
        ).addOnSuccessListener {
            binding.progress.visibility = View.GONE
            Toast.makeText(this, "Successfully added", Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            binding.progress.visibility = View.GONE
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != RESULT_OK) {
            return
        }
        if (data == null) {
            return
        }
        if (data.data == null) {
            return
        }
        if (requestCode == 1) {
            categoryBottomSheet.setImageUri(data.data)
            fileUri =data.data

        }
    }
}