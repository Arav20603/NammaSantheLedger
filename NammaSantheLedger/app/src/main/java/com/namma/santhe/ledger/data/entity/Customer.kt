package com.namma.santhe.ledger.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val customerId: Int = 0,
    val name: String,
    val phone: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
