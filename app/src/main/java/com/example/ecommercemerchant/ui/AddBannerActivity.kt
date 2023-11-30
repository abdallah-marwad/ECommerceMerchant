package com.example.ecommercemerchant.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.ecommercemerchant.R
import com.example.ecommercemerchant.databinding.ActivityAddBannerBinding
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AddBannerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBannerBinding
    private val bannersList = ArrayList<HashMap<Uri, String>>()
    val imageRef = Firebase.storage.reference
    private val FIRST_BANNER = 0
    private val SECOUND_BANNER = 1
    private val THIRD_BANNER = 2


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        downloadImage("1", binding.banner1)
        downloadImage("2", binding.banner2)
        downloadImage("3", binding.banner3)
        activityOnClick()


    }


    private fun activityOnClick() {
        binding.banner1.setOnClickListener {
            openGallery(FIRST_BANNER)
        }
        binding.banner2.setOnClickListener {
            openGallery(SECOUND_BANNER)
        }
        binding.banner3.setOnClickListener {
            openGallery(THIRD_BANNER)
        }
        binding.addBanner.setOnClickListener {
            if (!bannerValidation()) {
                showToast("please add more than one image")
                return@setOnClickListener
            }
            for (banner in bannersList) {
                val firstEntry = banner.entries.first()
                val uri = firstEntry.key
                val nameOfPath = firstEntry.value
                uploadImages(uri, nameOfPath)
            }

        }
    }

    private fun downloadImage(bannerNumber: String, imageView: ImageView)  =
        CoroutineScope(Dispatchers.IO).launch {
        imageRef.child("home_banner/$bannerNumber").downloadUrl
            .addOnSuccessListener { uri ->
                lifecycleScope.launch {
                    Glide.with(this@AddBannerActivity)
                        .load(uri)
                        .placeholder(R.drawable.ic_add_photo)
                        .into(imageView)
                }
            }.addOnFailureListener { exception ->
                lifecycleScope.launch {
//                    showToast("Fail delete image $exception")
                }
            }
    }

    private fun bannerValidation(): Boolean {
        if (bannersList.isEmpty())
            return false
        return true

    }

    private fun showToast(message: String) {
        Toast.makeText(
            this@AddBannerActivity, message,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun deleteImage(bannerNumber: String) =
        CoroutineScope(Dispatchers.IO).launch {
        imageRef.child("home_banner/$bannerNumber").delete()
            .addOnSuccessListener {
            lifecycleScope.launch {
//                showToast("Successfully delete image")
            }
        }.addOnFailureListener {
            lifecycleScope.launch {
//                showToast("Fail delete image")
            }
        }
    }

    private fun uploadImages(filename: Uri, bannerNumber: String) =
        CoroutineScope(Dispatchers.IO).launch {
            imageRef.child("home_banner/$bannerNumber").putFile(filename)
                .addOnSuccessListener {
                    lifecycleScope.launch {
                        showToast("Successfully uploaded image")
                    }
                }.addOnFailureListener {
                    lifecycleScope.launch {
                        showToast("Fail to upload image ${it.message}")
                    }
                }.addOnProgressListener {
                    val progress = (100.0 * it.bytesTransferred / it.totalByteCount).toInt()
                }


        }

    private fun openGallery(reqCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, reqCode)
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

        if (requestCode == FIRST_BANNER) {
            binding.banner1.setImageURI(data.data)
            bannersList.add(hashMapOf(data.data!! to "1"))

        } else if (requestCode == SECOUND_BANNER) {
            binding.banner2.setImageURI(data?.data)
            bannersList.add(hashMapOf(data.data!! to "2"))

        } else if (requestCode == THIRD_BANNER) {
            binding.banner3.setImageURI(data?.data)
            bannersList.add(hashMapOf(data.data!! to "3"))

        }
    }
}