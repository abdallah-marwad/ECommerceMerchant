package com.example.ecommercemerchant.data.models


data class Product (
    var id : Int? = null,
    var productName: String? = null,
    var categoryName: String? = null,
    var productdescription: String? = null,
    var brandName: String? = null,
    var productQuantity: Int? = null,
    var price: Double? = null,
    var offerValue: Double? = null,
    var offerPercentage: Double? = null,
    var date: Long? = null,
    var productImages: ArrayList<String>? = null,
    var productSize: ArrayList<String>? = null,
    var productColors: ArrayList<String>? = null,
    var productMainImg: String? = null,
    var location: String? = null,

)