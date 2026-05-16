package com.namma.santhe.ledger.data.dao

import androidx.room.*
import com.namma.santhe.ledger.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: Int): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay 
        ORDER BY timestamp DESC
    """)
    fun getTransactionsForDay(startOfDay: Long, endOfDay: Long): Flow<List<Transaction>>

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END), 0) -
               COALESCE(SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END), 0)
        FROM transactions 
        WHERE customerId = :customerId
    """)
    fun getNetBalanceForCustomer(customerId: Int): Flow<Double>

    @Query("""
        SELECT COALESCE(SUM(CASE WHEN type = 'CREDIT' THEN amount ELSE 0 END), 0) -
               COALESCE(SUM(CASE WHEN type = 'PAYMENT' THEN amount ELSE 0 END), 0)
        FROM transactions
    """)
    fun getTotalOutstanding(): Flow<Double>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)
}
