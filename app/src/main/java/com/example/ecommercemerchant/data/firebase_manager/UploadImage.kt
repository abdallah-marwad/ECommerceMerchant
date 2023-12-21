package com.example.ecommercemerchant.data.firebase_manager

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.util.UUID

class UploadImage {
    val imageRef = Firebase.storage.reference
    val errLiveData  = MutableLiveData<String>()
    val loadingLiveData  = MutableLiveData<Boolean>()
    val dataLiveData  = MutableLiveData<List<String>>()
    val imgUrl  = MutableLiveData<ArrayList<String>>()

    fun uploadImage(imgName: String = "", imgPath: String, fileUri: Uri? ) {
        var mutableImgName =imgName
        if(mutableImgName=="")
            mutableImgName = UUID.randomUUID().toString()

        if (fileUri == null) {
            errLiveData.value = "Please choose image"
            return
        }
        loadingLiveData.value = true

        CoroutineScope(Dispatchers.IO).launch {
            var ref = imageRef.child("$imgPath/$mutableImgName")
            ref.putFile(fileUri)

                .addOnSuccessListener {
                    var array = listOf(imgName ,runBlocking {ref.downloadUrl.await().toString()})
                    dataLiveData.postValue(array)
                    loadingLiveData.postValue(false)
                }

                .addOnFailureListener {
                    loadingLiveData.postValue(false)
                    errLiveData.postValue(it.message)
                }
        }
    }
    fun uploadListOfImages( filesPath: String, fileUriList: ArrayList<Uri>? ) {

        loadingLiveData.postValue(true)
        if (fileUriList == null) {
            errLiveData.value = "Please choose image"
            return
        }
        loadingLiveData.value = true

        val images = arrayListOf<String>()
        CoroutineScope(Dispatchers.IO).launch {
            fileUriList.forEach {imageUri->
                try {
                    val imagesStorage = imageRef.child("$filesPath/${UUID.randomUUID()}")
                    val result = imagesStorage.putFile(imageUri).await()
                    val downloadUrl = result.storage.downloadUrl.await().toString()
                    images.add(downloadUrl)
                    loadingLiveData.postValue(false)
                }catch (e : Exception) {
                    loadingLiveData.postValue(false)
                    errLiveData.postValue(e.message)
                }
            }
            imgUrl.postValue(images)
        }

    }
}