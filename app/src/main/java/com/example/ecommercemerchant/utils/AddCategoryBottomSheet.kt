package com.example.ecommercemerchant.utils

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.ecommercemerchant.R

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

 class AddCategoryBottomSheet {
      var imgUrl : Uri? = null

     lateinit var  image :ImageView

     fun setImageUri(uri: Uri?){
         this.imgUrl = uri
         image.setImageURI(uri)
     }
fun showAddCategoryDialog(
    activity: Activity,
    onAddClick: (String ) -> Unit,

) {
    val dialog = BottomSheetDialog(activity, R.style.DialogStyle)
    val view = activity.layoutInflater.inflate(R.layout.add_category_bottom_sheet, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()


    val categoryEt = view.findViewById<EditText>(R.id.categoryEt)
    val addCategoryBtn = view.findViewById<Button>(R.id.addCategoryBtn)
     image = view.findViewById(R.id.Icone)

    image.setOnClickListener {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        activity.startActivityForResult(intent, 1)
    }





    addCategoryBtn.setOnClickListener {
        val category = categoryEt.text.toString().trim()
        if (!validation(activity , category , categoryEt)){
            return@setOnClickListener
        }

        onAddClick(category)
        dialog.dismiss()


    }


}
     private fun validation(activity: Activity , category : String , editText: EditText) : Boolean{
         var isDataValid = true
         if (imgUrl == null) {
             image.setBackgroundResource(R.drawable.err_img_background)
             image.animation=AnimationUtils.loadAnimation(activity , R.anim.shake_animation)
             isDataValid= false
         }
         if (category.isEmpty()) {
             editText.error = "Please enter a category"
             isDataValid= false
         }
         return isDataValid
     }

}
