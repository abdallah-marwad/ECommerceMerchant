package com.example.ecommercemerchant.utils

import android.app.Activity
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.ecommercemerchant.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

const val PRODUCT = 0
const val CATEGORY = 1

class AddingTypeBottomSheet {


fun showAddingTypeDialog(
    baseContext : Activity,
    onAddClick: (Int) -> Unit ,

) {
    val dialog = BottomSheetDialog(baseContext, R.style.DialogStyle)
    val view = baseContext.layoutInflater.inflate(R.layout.choose_adding_type, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()


    val groupRadio = view.findViewById<RadioGroup>(R.id.groupRadio)
    val goBtn = view.findViewById<Button>(R.id.goBtn)

    var selectedBtn = -1
    groupRadio.setOnCheckedChangeListener { group, checkedId ->
        if (checkedId == R.id.product) {
            selectedBtn = PRODUCT
        } else if (checkedId == R.id.category) {
            selectedBtn = CATEGORY
        }

    }


    goBtn.setOnClickListener {
        onAddClick(selectedBtn)
        dialog.dismiss()

    }
}

}
