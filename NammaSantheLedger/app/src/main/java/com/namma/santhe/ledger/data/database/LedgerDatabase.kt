package com.namma.santhe.ledger.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.namma.santhe.ledger.data.dao.CustomerDao
import com.namma.santhe.ledger.data.dao.TransactionDao
import com.namma.santhe.ledger.data.entity.Customer
import com.namma.santhe.ledger.data.entity.Transaction

@Database(
    entities = [Customer::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class LedgerDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao
}
