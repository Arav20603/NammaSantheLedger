package com.namma.santhe.ledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.santhe.ledger.data.entity.Transaction
import com.namma.santhe.ledger.data.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DailySummaryState(
    val totalCredit: Double = 0.0,
    val totalPayments: Double = 0.0,
    val netPending: Double = 0.0,
    val transactionCount: Int = 0,
    val transactions: List<Transaction> = emptyList()
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val repository: LedgerRepository
) : ViewModel() {

    val dailySummary: StateFlow<DailySummaryState> = repository.getTodayTransactions()
        .map { transactions ->
            val totalCredit = transactions.filter { it.type == "CREDIT" }.sumOf { it.amount }
            val totalPayments = transactions.filter { it.type == "PAYMENT" }.sumOf { it.amount }
            DailySummaryState(
                totalCredit = totalCredit,
                totalPayments = totalPayments,
                netPending = totalCredit - totalPayments,
                transactionCount = transactions.size,
                transactions = transactions
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DailySummaryState())
}
