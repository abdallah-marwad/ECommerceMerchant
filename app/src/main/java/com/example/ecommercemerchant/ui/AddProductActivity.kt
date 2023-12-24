package com.example.ecommercemerchant.ui

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommercemerchant.data.firebase_manager.FirebaseManager
import com.example.ecommercemerchant.data.firebase_manager.UploadImage
import com.example.ecommercemerchant.data.models.Product
import com.example.ecommercemerchant.databinding.ActivityMainBinding
import com.example.ecommercemerchant.utils.AddCategoryBottomSheet
import com.google.firebase.firestore.FirebaseFirestore
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

const val PRODUCT_IMGS = "products"

class AddProductActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var categoryBottomSheet: AddCategoryBottomSheet
    val uploadProductImages: UploadImage by lazy { UploadImage() }
    val uploadCategoryImage: UploadImage by lazy { UploadImage() }
    val uploadPosterImage: UploadImage by lazy { UploadImage() }
    val uploadImage: UploadImage by lazy { UploadImage() }
    var fileUri: Uri? = null
    private val firestore = FirebaseFirestore.getInstance()
    private var parentJob: Job = Job()
    private var coroutineScope: CoroutineScope = CoroutineScope(parentJob + Dispatchers.IO)
    private var categoriesList: ArrayList<String> = ArrayList()
    private var productImagesList: ArrayList<Uri> = ArrayList()
    private var colorsList: ArrayList<Int> = ArrayList()
    private var sizesList: ArrayList<String> = ArrayList()
    private var selectedCategory: String = ""
    private var posterDownloadUrl: String = ""
    private var posterImage: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        getCategories()
        categoryImageStatusCallback()
        activityOnClick()
        detectSelectedCategory()
        categoriesRealTimeListener()

//        uploadImageErrorCallBack()
//        uploadImageLoadingCallBack()
    }

    private fun showProgress() {
        binding.progress.visibility = View.VISIBLE
        binding.productDetailsArea.visibility = View.GONE
        binding.productsInfoArea.visibility = View.GONE
    }

    private fun hideProgress() {
        binding.progress.visibility = View.GONE
        binding.productDetailsArea.visibility = View.VISIBLE
        binding.productsInfoArea.visibility = View.VISIBLE
    }

    private fun categoriesRealTimeListener() = coroutineScope.launch {
        withContext(Dispatchers.Main) {
            showProgress()
        }
        firestore.collection("category").addSnapshotListener { querySnapShot, error ->
            hideProgress()
            error?.let {
                Toast.makeText(this@AddProductActivity, error.message, Toast.LENGTH_SHORT).show()
                return@let
            }
            if (querySnapShot == null) {
                return@addSnapshotListener
            }

            categoriesList.clear()
            querySnapShot.documents.forEach {
                categoriesList.add(it.id)
            }
            initSpinner()

        }
    }

    private fun initSpinner() {
        if (categoriesList.isEmpty()) {
            Toast.makeText(this, "No categories founded", Toast.LENGTH_SHORT).show()
            return
        }
        categoriesList.add(0, "Choose Category")
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.simple_spinner_item,
            categoriesList
        )
        binding.spinnerCategory.adapter = adapter

    }

    private fun detectSelectedCategory() {
        binding.spinnerCategory.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (p2 == 0) {
                    selectedCategory = ""
                    return

                }
                selectedCategory = categoriesList[p2]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }
    }
//    private fun addMainCategoryDialog() {
//        categoryBottomSheet = AddCategoryBottomSheet()
//        categoryBottomSheet.showAddCategoryDialog(this)
//        { collectionName ->
//            uploadedImageNameAndUrlCallBack(false)
//            binding.progress.visibility = View.VISIBLE
//            uploadCategoryImage.uploadImage(
//                collectionName,
//                "main_categories",
//                fileUri
//            )
//
//        }
//    }


    private fun addMainCategoryDialog() {
        categoryBottomSheet = AddCategoryBottomSheet()
        categoryBottomSheet.showAddCategoryDialog(this)
        { collectionName ->
            binding.progress.visibility = View.VISIBLE
            uploadCategoryImage.uploadImage(
                collectionName,
                "main_categories",
                fileUri
            )
        }
    }

    private fun categoryImageStatusCallback() {
        uploadCategoryImage.errLiveData.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        uploadCategoryImage.loadingLiveData.observe(this) {
            if (it)
                showProgress()
            else
                hideProgress()
        }

        uploadCategoryImage.uploadedImageData.observe(this) {
            addMainCategory(
                it[0],
                it[1]
            )
        }
    }

    private fun addMainCategory(docName: String, imgUrl: String) {

        showProgress()
        val firebaseManager = FirebaseManager()
        firebaseManager.addMainCategory(
            docName,
            imgUrl,
            firestore
        ).addOnSuccessListener {
            hideProgress()
            Toast.makeText(this, "Successfully added", Toast.LENGTH_LONG).show()
        }.addOnFailureListener {
            hideProgress()
            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()

        }
    }


    private fun uploadProductImages() {
        uploadProductImages.uploadListOfImages(PRODUCT_IMGS , productImagesList)
        uploadImagesCallBack()
    }

    private fun updateColors() {
        var colors = ""
        colorsList.forEach {
            colors += "${Integer.toHexString(it)}, "
        }
        binding.tvSelectedColors.text = colors
    }

    private val selectImagesActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                productImagesList.clear()

                if (intent?.clipData != null) {
                    val count = intent.clipData?.itemCount ?: 0

                    (0 until count).forEach {
                        val imagesUri = intent.clipData?.getItemAt(it)?.uri
                        imagesUri?.let { uri -> productImagesList.add(uri) }
                    }

                    //One images was selected
                } else {
                    val imageUri = intent?.data
                    imageUri?.let { productImagesList.add(it) }
                }
                updateImages()
            }
        }

    private fun updateImages() {
        binding.tvSelectedImages.setText(productImagesList.size.toString())
    }

    private fun openSizesDialog() {
        var customDialog: AlertDialog
        val dialogView: View = LayoutInflater.from(this)
            .inflate(com.example.ecommercemerchant.R.layout.all_sizes_dialog, null)

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setView(dialogView)
        customDialog = dialogBuilder.create()
        customDialog.setCancelable(false)
        customDialog.setCanceledOnTouchOutside(false)
        customDialog.show()
        val checkBoxList = ArrayList<CheckBox>()
        val checkBox_XS: CheckBox =
            dialogView.findViewById(com.example.ecommercemerchant.R.id.checkBox_XS)
        checkBoxList.add(checkBox_XS)
        val checkBox_S: CheckBox =
            dialogView.findViewById(com.example.ecommercemerchant.R.id.checkBox_S)
        checkBoxList.add(checkBox_S)
        val checkBox_M: CheckBox =
            dialogView.findViewById(com.example.ecommercemerchant.R.id.checkBox_M)
        checkBoxList.add(checkBox_M)
        val checkBox_L: CheckBox =
            dialogView.findViewById(com.example.ecommercemerchant.R.id.checkBox_L)
        checkBoxList.add(checkBox_L)
        val checkBox_XL: CheckBox =
            dialogView.findViewById(com.example.ecommercemerchant.R.id.checkBox_XL)
        checkBoxList.add(checkBox_XL)
        val checkBox_2XL: CheckBox =
            dialogView.findViewById(com.example.ecommercemerchant.R.id.checkBox_2XL)
        checkBoxList.add(checkBox_2XL)
        val checkBox_3XL: CheckBox =
            dialogView.findViewById(com.example.ecommercemerchant.R.id.checkBox_3XL)
        checkBoxList.add(checkBox_3XL)
        val close: ImageView =
            dialogView.findViewById(com.example.ecommercemerchant.R.id.close)

        if(sizesList.size >0)
            sizesList.forEach { selectedSize->
            checkBoxList.forEach {checkBox->
                if(selectedSize==checkBox.text)
                    checkBox.isChecked = true
            }
        }
        close.setOnClickListener{
            customDialog.dismiss()
            updateSizesTxt()
        }
        checkBox_XS.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                sizesList.add("XS")
            else
                sizesList.remove("XS")
        }
        checkBox_S.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                sizesList.add("S")
            else
                sizesList.remove("S")
        }
        checkBox_M.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                sizesList.add("M")
            else
                sizesList.remove("M")
        }
        checkBox_L.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                sizesList.add("L")
            else
                sizesList.remove("L")
        }
        checkBox_XL.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                sizesList.add("XL")
            else
                sizesList.remove("XL")
        }
        checkBox_2XL.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                sizesList.add("2XL")
            else
                sizesList.remove("2XL")
        }
        checkBox_3XL.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                sizesList.add("3XL")
            else
                sizesList.remove("3XL")
        }
    }

    private fun updateSizesTxt() {
        var sizeTxt = ""

        sizesList.forEach { size ->
            sizeTxt += "$size , "
        }
        binding.tvSelectedSizes.text = sizeTxt
    }
    private fun addProduct(imgList : ArrayList<String>) {
        try{
            showProgress()
           FirebaseManager().addProduct(
               selectedCategory,
               getProductObj(imgList),
               firestore
           ) .addOnSuccessListener {
               hideProgress()
               Toast.makeText(this , "Successfully Adding Product" , Toast.LENGTH_SHORT).show()


           }.addOnFailureListener {
               hideProgress()
               Toast.makeText(this , it.message , Toast.LENGTH_LONG).show()

           }


        }catch (e : Exception){
            Toast.makeText(this , "Some err happened" , Toast.LENGTH_SHORT).show()
        }


    }
    private fun getProductObj(imgList: ArrayList<String>): Product {
        val offerValue = (binding.edPrice.text.toString().trim().toDouble() *
                binding.offerPercentage.text.toString().trim().toDouble()) / 100
        return Product(
            UUID.randomUUID().toString(),
            binding.edName.text.toString().trim(),
            selectedCategory,
            binding.edDescription.text.toString().trim(),
            binding.quantityEd.text.toString().trim().toInt(),
            binding.edPrice.text.toString().trim().toDouble(),
            offerValue,
            binding.offerPercentage.text.toString().trim().toDouble(),
            Date().time,
            imgList,
            sizesList,
            colorsList,
            posterDownloadUrl
        )

    }
    private fun uploadImagesCallBack() {
        uploadProductImages.imageUrls.observe(this){
            addProduct(it)
        }
        uploadProductImages.loadingLiveData.observe(this){
            if(it)
                showProgress()
            else
                hideProgress()
        }
        uploadProductImages.errLiveData.observe(this){
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadPosterImage() {
        uploadPosterImage.uploadImage(
            imgPath = PRODUCT_IMGS,
            fileUri = posterImage
        )
        posterImageCallBack()
    }

    private fun posterImageCallBack() {
        uploadPosterImage.uploadedImageData.observe(this){
            uploadProductImages()
            posterDownloadUrl = it[1]
        }
        uploadPosterImage.loadingLiveData.observe(this){
            if(it)
                showProgress()
            else
                hideProgress()
        }
        uploadPosterImage.errLiveData.observe(this){
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        }
    }
//    private fun uploadImageLoadingCallBack() {
//        uploadImage.loadingLiveData.observe(this){
//            if(it)
//                showProgress()
//            else
//                hideProgress()
//        }
//    }
//  private fun uploadImageErrorCallBack() {
//      uploadImage.errLiveData.observe(this){
//          Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
//      }
//    }
//    private fun uploadProductImages() {
//        uploadProductImages.uploadListOfImages(PRODUCT_IMGS , productImagesList)
//        uploadedImageUrlsCallBack()
//    }
//    private fun uploadedImageNameAndUrlCallBack(isPoster : Boolean) {
//      uploadImage.uploadedImageData.observe(this){
//          if(isPoster){
//              uploadProductImages()
//              posterDownloadUrl = it[1]
//              return@observe
//          }
//          addMainCategory(
//              it[0],
//              it[1]
//          )
//      }
//    }
//    private fun uploadedImageUrlsCallBack() {
//      uploadImage.imageUrls.observe(this){
//          addProduct(it)
//      }
//    }

    private fun isInputsValid(): Boolean {
        var isValid = true
        val nameTxt = binding.edName.text.toString()
        val priceTxt = binding.edPrice.text.toString()
        if (nameTxt.isEmpty()) {
            binding.edName.error = "Please add name of product"
            isValid = false
        }
        if (priceTxt.isEmpty()) {
            binding.edPrice.error = "Please enter price"
            isValid = false
        }
        if (selectedCategory == "") {
            Toast.makeText(this, "Please choose category", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (productImagesList.isEmpty()) {
            Toast.makeText(this, "Please add images", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        if (posterImage==null) {
            Toast.makeText(this, "Please add poster image", Toast.LENGTH_SHORT).show()
            isValid = false
        }
        return isValid
    }
    private fun activityOnClick() {
        binding.posterImage.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.type = "image/*"
           startActivityForResult(intent, 2)
        }
        binding.addCategory.setOnClickListener {
            addMainCategoryDialog()

        }
        binding.btnAddSize.setOnClickListener {
            openSizesDialog()

        }
        binding.btnAddProduct.setOnClickListener {
            if (isInputsValid().not())
                return@setOnClickListener
            uploadPosterImage()
//            uploadedImageNameAndUrlCallBack(true)
        }
        binding.buttonImagesPicker.setOnClickListener {
            val intent = Intent(ACTION_GET_CONTENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.type = "image/*"
            selectImagesActivityResult.launch(intent)
        }

        binding.buttonColorPicker.setOnClickListener {
            ColorPickerDialog
                .Builder(this)
                .setTitle("Product color")
                .setPositiveButton("Select", object : ColorEnvelopeListener {

                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
                        envelope?.let {
                            colorsList.add(it.color)
                            updateColors()
                        }
                    }
                }).setNegativeButton("Cancel") { colorPicker, _ ->
                    colorPicker.dismiss()
                }.show()
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
            fileUri = data.data

        }
        if (requestCode == 2) {

            posterImage = data.data
            binding.posterImageDone.visibility = View.VISIBLE

        }
    }


}