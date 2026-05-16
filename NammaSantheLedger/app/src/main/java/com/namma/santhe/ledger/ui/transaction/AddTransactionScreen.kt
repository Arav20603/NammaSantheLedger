package com.namma.santhe.ledger.ui.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.santhe.ledger.ui.theme.*
import com.namma.santhe.ledger.viewmodel.CustomerViewModel
import com.namma.santhe.ledger.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    customerId: Int,
    viewModel: CustomerViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onTransactionAdded: () -> Unit
) {
    val customer by viewModel.customer.collectAsState()

    LaunchedEffect(customerId) {
        viewModel.loadCustomer(customerId)
    }

    var amountStr by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("CREDIT") }
    var note by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar("Transaction saved ✓")
            showSnackbar = false
            onTransactionAdded()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = SantheBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = customer?.name ?: "Add Transaction",
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (customer != null) {
                            Text(
                                text = "Add Udari or Payment",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SantheSubtext
                            )
                        }
                    }
                },
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Type Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SantheOutline)
                    .padding(4.dp)
            ) {
                TypeTab(
                    text = "Udari (Credit)",
                    selected = transactionType == "CREDIT",
                    selectedColor = SantheRed,
                    modifier = Modifier.weight(1f),
                    onClick = { transactionType = "CREDIT" }
                )
                TypeTab(
                    text = "Payment",
                    selected = transactionType == "PAYMENT",
                    selectedColor = SantheGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { transactionType = "PAYMENT" }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Amount display
            val displayColor = if (transactionType == "CREDIT") SantheRed else SantheGreen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (transactionType == "CREDIT") SantheRedLight else SantheGreenLight)
                    .padding(vertical = 20.dp, horizontal = 24.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = if (transactionType == "CREDIT") "Amount to Credit (₹)" else "Payment Received (₹)",
                        fontSize = 14.sp,
                        color = displayColor.copy(alpha = 0.8f)
                    )
                    Text(
                        text = if (amountStr.isEmpty()) "0" else amountStr,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = displayColor,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Note field
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Note (optional)", color = SantheSubtext) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SantheOrange,
                    unfocusedBorderColor = SantheOutline
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Numeric Keypad
            NumericKeypad(
                onDigit = { digit ->
                    if (amountStr.length < 8) {
                        amountStr += digit
                    }
                },
                onBackspace = {
                    if (amountStr.isNotEmpty()) amountStr = amountStr.dropLast(1)
                },
                onClear = { amountStr = "" }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Confirm button
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull()
                    if (amount != null && amount > 0) {
                        if (transactionType == "CREDIT") {
                            viewModel.addCredit(customerId, amount, note.ifBlank { null })
                        } else {
                            viewModel.addPayment(customerId, amount, note.ifBlank { null })
                        }
                        showSnackbar = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (transactionType == "CREDIT") SantheRed else SantheGreen
                ),
                enabled = amountStr.isNotEmpty() && (amountStr.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text(
                    text = if (transactionType == "CREDIT") "Record Udari ₹$amountStr" else "Record Payment ₹$amountStr",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TypeTab(
    text: String,
    selected: Boolean,
    selectedColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) selectedColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) Color.White else SantheSubtext,
            fontSize = 15.sp
        )
    }
}

@Composable
fun NumericKeypad(
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "⌫")
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(66.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                when (key) {
                                    "C" -> SantheRedLight
                                    "⌫" -> SantheAmberLight
                                    else -> SantheSurface
                                }
                            )
                            .border(
                                1.dp,
                                SantheOutline,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                when (key) {
                                    "⌫" -> onBackspace()
                                    "C" -> onClear()
                                    else -> onDigit(key)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (key == "⌫") {
                            Icon(
                                Icons.Filled.Backspace,
                                contentDescription = "Backspace",
                                tint = SantheAmber,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = key,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (key == "C") SantheRed else SantheOnSurface,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
