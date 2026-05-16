package com.namma.santhe.ledger.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.namma.santhe.ledger.data.entity.Customer
import com.namma.santhe.ledger.data.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomerViewModel @Inject constructor(
    private val repository: LedgerRepository
) : ViewModel() {

    fun addCustomer(name: String, phone: String?, onSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertCustomer(
                Customer(name = name.trim(), phone = phone?.trim()?.takeIf { it.isNotBlank() })
            )
            onSuccess(id)
        }
    }
}
