package com.namma.santhe.ledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.santhe.ledger.data.entity.Customer
import com.namma.santhe.ledger.data.entity.Transaction
import com.namma.santhe.ledger.data.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val customers: List<CustomerWithBalance> = emptyList(),
    val totalOutstanding: Double = 0.0,
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

data class CustomerWithBalance(
    val customer: Customer,
    val balance: Double = 0.0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: LedgerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val customers: StateFlow<List<Customer>> = _searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllCustomers()
            else repository.searchCustomers(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalOutstanding: StateFlow<Double> = repository.getTotalOutstanding()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun getBalanceForCustomer(customerId: Int): Flow<Double> =
        repository.getNetBalanceForCustomer(customerId)

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            repository.deleteCustomer(customer)
        }
    }
}
