package com.namma.santhe.ledger.data.repository

import com.namma.santhe.ledger.data.dao.CustomerDao
import com.namma.santhe.ledger.data.dao.TransactionDao
import com.namma.santhe.ledger.data.entity.Customer
import com.namma.santhe.ledger.data.entity.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LedgerRepository @Inject constructor(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao
) {
    fun getAllCustomers(): Flow<List<Customer>> = customerDao.getAllCustomers()

    fun searchCustomers(query: String): Flow<List<Customer>> = customerDao.searchCustomers(query)

    suspend fun getCustomerById(id: Int): Customer? = customerDao.getCustomerById(id)

    suspend fun insertCustomer(customer: Customer): Long = customerDao.insertCustomer(customer)

    suspend fun deleteCustomer(customer: Customer) = customerDao.deleteCustomer(customer)

    fun getTransactionsForCustomer(customerId: Int): Flow<List<Transaction>> =
        transactionDao.getTransactionsForCustomer(customerId)

    fun getNetBalanceForCustomer(customerId: Int): Flow<Double> =
        transactionDao.getNetBalanceForCustomer(customerId)

    fun getTotalOutstanding(): Flow<Double> = transactionDao.getTotalOutstanding()

    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    fun getTodayTransactions(): Flow<List<Transaction>> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val endOfDay = cal.timeInMillis
        return transactionDao.getTransactionsForDay(startOfDay, endOfDay)
    }
}
