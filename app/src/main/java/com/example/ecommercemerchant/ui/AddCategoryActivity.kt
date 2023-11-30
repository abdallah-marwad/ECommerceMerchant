package com.example.ecommercemerchant.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.ecommercemerchant.R
import com.example.ecommercemerchant.data.firebase_manager.FirebaseManager
import com.example.ecommercemerchant.databinding.ActivityAddCategoryBinding
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

class AddCategoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityAddCategoryBinding
    private val firestore = FirebaseFirestore.getInstance()
    private var parentJob: Job = Job()
    private var coroutineScope: CoroutineScope = CoroutineScope(parentJob + Dispatchers.IO)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getCategories()
        activityOnClick()


    }

    private fun getCategories() = coroutineScope.launch {
        val collectionReference = firestore.collection("category")
        collectionReference.get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    document.forEach{}
                } else {
                    Toast.makeText(
                        this@AddCategoryActivity,
                        "You should add category before adding product",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this@AddCategoryActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()            }
    }

    var lastCollectionName : String = ""
    private fun addSubCategoryDialog() {
        showAddCategoryDialog {collectionName ->
            val firebaseManager = FirebaseManager()
            firebaseManager. addSubCategory(
                collectionName,
                firestore.collection("category").document("no cat")
            )
        }
    }
    private fun addMainCategoryDialog() {
        showAddCategoryDialog {collectionName ->
            val firebaseManager = FirebaseManager()
            firebaseManager. addSubCategory(
                collectionName,
                firestore.collection("category").document("no cat")
            )
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


        }
    }
    private fun activityOnClick(){
        binding.addCategory.setOnClickListener{
            addCategoryOrProductDialog()
        }
    }
}