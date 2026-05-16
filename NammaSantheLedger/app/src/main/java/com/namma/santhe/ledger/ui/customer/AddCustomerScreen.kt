package com.namma.santhe.ledger.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.santhe.ledger.ui.theme.*
import com.namma.santhe.ledger.viewmodel.AddCustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(
    viewModel: AddCustomerViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onCustomerAdded: (Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = SantheBg,
        topBar = {
            TopAppBar(
                title = { Text("Add New Customer") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SantheSurface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SantheSurface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Customer Details",
                        style = MaterialTheme.typography.titleMedium,
                        color = SantheOnSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Customer Name *") },
                        leadingIcon = { Icon(Icons.Filled.Person, null, tint = SantheOrange) },
                        isError = nameError,
                        supportingText = if (nameError) { { Text("Name is required") } } else null,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SantheOrange,
                            unfocusedBorderColor = SantheOutline
                        ),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { if (it.length <= 12) phone = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Phone Number (for WhatsApp)") },
                        leadingIcon = { Icon(Icons.Filled.Phone, null, tint = SantheOrange) },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SantheOrange,
                            unfocusedBorderColor = SantheOutline
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        placeholder = { Text("+91 98765 43210") },
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    viewModel.addCustomer(name, phone.ifBlank { null }) { newId ->
                        onCustomerAdded(newId.toInt())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SantheOrange)
            ) {
                Text("Save & Add First Udari", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@OutlinedButton
                    }
                    viewModel.addCustomer(name, phone.ifBlank { null }) { _ ->
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SantheOrange)
            ) {
                Text("Save Only")
            }
        }
    }
}
