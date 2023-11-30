package com.example.ecommercemerchant.data.models

import com.google.firebase.Timestamp

data class Product (
    var id : Int? = null,
    var name: String? = null,
    var description: String? = null,
    var brandName: String? = null,
    var acceptSubCategory: Boolean? = null,
    var quantity: Int? = null,
    var price: Double? = null,
    var offerValue: Double? = null,
    var offerPercentage: Double? = null,
    var date: Long? = null,
    var productImages: ArrayList<String>? = null,
    var productMainImg: String? = null,
    var location: String? = null,

)