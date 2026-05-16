package com.namma.santhe.ledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.santhe.ledger.data.entity.Customer
import com.namma.santhe.ledger.data.entity.Transaction
import com.namma.santhe.ledger.data.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerLedgerUiState(
    val customer: Customer? = null,
    val transactions: List<Transaction> = emptyList(),
    val netBalance: Double = 0.0,
    val isLoading: Boolean = false
)

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val repository: LedgerRepository
) : ViewModel() {

    private val _customerId = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<CustomerLedgerUiState> = _customerId
        .filterNotNull()
        .flatMapLatest { id ->
            combine(
                repository.getTransactionsForCustomer(id),
                repository.getNetBalanceForCustomer(id)
            ) { transactions, balance ->
                CustomerLedgerUiState(
                    transactions = transactions,
                    netBalance = balance,
                    isLoading = false
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CustomerLedgerUiState(isLoading = true))

    private val _customer = MutableStateFlow<Customer?>(null)
    val customer = _customer.asStateFlow()

    fun loadCustomer(customerId: Int) {
        _customerId.value = customerId
        viewModelScope.launch {
            _customer.value = repository.getCustomerById(customerId)
        }
    }

    fun addCredit(customerId: Int, amount: Double, note: String? = null) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    customerId = customerId,
                    amount = amount,
                    type = "CREDIT",
                    note = note
                )
            )
        }
    }

    fun addPayment(customerId: Int, amount: Double, note: String? = null) {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(
                    customerId = customerId,
                    amount = amount,
                    type = "PAYMENT",
                    note = note
                )
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}
