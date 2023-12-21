package com.example.ecommercemerchant.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ecommercemerchant.data.firebase_manager.FirebaseManager
import com.example.ecommercemerchant.databinding.ActivityAddCategoryBinding
import com.example.ecommercemerchant.utils.AddCategoryBottomSheet
import com.example.ecommercemerchant.utils.AddingTypeBottomSheet
import com.example.ecommercemerchant.utils.PRODUCT
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class AddCategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddCategoryBinding
    lateinit var categoryBottomSheet: AddCategoryBottomSheet
    val imageRef = Firebase.storage.reference

    private val firestore = FirebaseFirestore.getInstance()
    private var parentJob: Job = Job()
    private var coroutineScope: CoroutineScope = CoroutineScope(parentJob + Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        activityOnClick()


    }




    private fun addMainCategoryDialog() {
        categoryBottomSheet = AddCategoryBottomSheet()
        categoryBottomSheet.showAddCategoryDialog(this)
        { collectionName ->
            binding.progress.visibility = View.VISIBLE
            uploadImages(collectionName)
        }
    }

    var fileUri: Uri? = null
    private fun uploadImages( imgName: String) {
        if (fileUri == null) {
            binding.progress.visibility = View.GONE
            Toast.makeText(this@AddCategoryActivity,
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

                    Toast.makeText(this@AddCategoryActivity, it.message, Toast.LENGTH_LONG).show()
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
            Toast.makeText(this@AddCategoryActivity, "Successfully added", Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            binding.progress.visibility = View.GONE
            Toast.makeText(this@AddCategoryActivity, it.message, Toast.LENGTH_LONG).show()

        }

    }



    private fun activityOnClick() {
        binding.addCategory.setOnClickListener {
            addCategoryOrProductDialog()
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

    private fun addCategoryOrProductDialog() {
        AddingTypeBottomSheet().showAddingTypeDialog(this) {
            if (it == -1) {
                Toast.makeText(baseContext, "Please select a type", Toast.LENGTH_SHORT).show()
                return@showAddingTypeDialog
            }
            if (it == PRODUCT) {
                Toast.makeText(baseContext, "Please fill required items", Toast.LENGTH_SHORT).show()
                return@showAddingTypeDialog

            }
            addMainCategoryDialog()

        }
    }
}