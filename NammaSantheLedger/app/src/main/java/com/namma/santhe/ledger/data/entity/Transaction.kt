package com.namma.santhe.ledger.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["customerId"],
        childColumns = ["customerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("customerId")]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    val customerId: Int,
    val amount: Double,
    val type: String, // "CREDIT" or "PAYMENT"
    val note: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
