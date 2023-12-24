package com.example.ecommercemerchant.data.firebase_manager

import com.example.ecommercemerchant.data.models.Category
import com.example.ecommercemerchant.data.models.Product
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class FirebaseManager {

    fun addCollection(
        isInsideDocument : Boolean = false ,
        documentReference : DocumentReference? = null ,
        firestore: FirebaseFirestore ,
        collectionName : String
    ): Task<QuerySnapshot>{

        if(isInsideDocument && documentReference != null){
            return documentReference.collection(collectionName).get()
        }

        return firestore.collection(collectionName).get()
    }
    fun addField(
        isWithoutDocName : Boolean = false ,
        documentReference : DocumentReference? ,
        collectionReference: CollectionReference ? ,
        product : Product
    ): Task<*>? {

        if(isWithoutDocName && collectionReference != null){
            return collectionReference.add(product)
        }
        if(!isWithoutDocName && documentReference != null){
            return documentReference.set(product)
        }

        return null
    }

    fun addDocument(
        collectionReference: CollectionReference,
        documentName : String,
    ): DocumentReference{


        return collectionReference.document(documentName)
    }
    fun addSubCategory(
        documentName : String,
        documentReference : DocumentReference
    ){
         documentReference.collection("category").document(documentName).set(Product())
    }


    fun addMainCategory(
        documentName : String,
        catUrl : String,
        firestore: FirebaseFirestore,
    )=
         firestore.collection("category").document(documentName).
         set(Category(documentName , catUrl))
    fun addProduct(
        documentName : String,
        product : Product,
        firestore: FirebaseFirestore,
    )=
         firestore.collection("category").document(documentName).collection("Product")
             .document(product.id!!).set(product)




}