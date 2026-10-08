package com.example.mycursmob.domain

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseModule
{
    val auth: FirebaseAuth= FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore= FirebaseFirestore.getInstance()
}