package com.example.ecommercemerchant

import android.app.Activity
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment

import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog


fun Activity.showResetPasswordDialog(
    onAddClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(baseContext, R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.add_category_bottom_sheet, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()


    val categoryEt = view.findViewById<EditText>(R.id.categoryEt)
    val addCategoryBtn = view.findViewById<Button>(R.id.addCategoryBtn)




    addCategoryBtn.setOnClickListener {
        val category = categoryEt.text.toString().trim()

        if(category.isEmpty())
            categoryEt.error = "Please enter a category"

        if (category.isNotEmpty()){
            onAddClick(category)
            dialog.dismiss()
        }

    }


}
