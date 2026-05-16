package com.namma.santhe.ledger.ui.customer

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.santhe.ledger.data.entity.Transaction
import com.namma.santhe.ledger.ui.theme.*
import com.namma.santhe.ledger.utils.formatAmount
import com.namma.santhe.ledger.utils.formatDate
import com.namma.santhe.ledger.utils.initials
import com.namma.santhe.ledger.viewmodel.CustomerViewModel
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerLedgerScreen(
    customerId: Int,
    viewModel: CustomerViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToAddTransaction: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val customer by viewModel.customer.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(customerId) {
        viewModel.loadCustomer(customerId)
    }

    val balance = uiState.netBalance

    Scaffold(
        containerColor = SantheBg,
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Customer Ledger") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SantheSurface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddTransaction(customerId) },
                containerColor = SantheOrange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                // Balance card
                val balanceColor = when {
                    balance > 0 -> SantheRed
                    balance < 0 -> SantheGreen
                    else -> SantheSubtext
                }
                val balanceBg = when {
                    balance > 0 -> SantheRed
                    balance < 0 -> SantheGreen
                    else -> SantheSubtext
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(balanceBg)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = customer?.name?.initials() ?: "?",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = customer?.name ?: "",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                    Text(
                                        text = when {
                                            balance > 0 -> "Owes you"
                                            balance < 0 -> "You owe"
                                            else -> "All settled"
                                        },
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                        Text(
                            text = "₹${formatAmount(kotlin.math.abs(balance))}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 30.sp
                        )
                    }
                }
            }

            // WhatsApp Reminder button
            if (!customer?.phone.isNullOrBlank() && balance > 0) {
                item {
                    Button(
                        onClick = {
                            val msg = "Dear ${customer?.name}, you have an outstanding due of ₹${formatAmount(balance)} with your vendor. Kindly settle at the next Santhe. Thank you!"
                            val encoded = URLEncoder.encode(msg, "UTF-8")
                            val phone = customer?.phone?.replace("+", "")?.replace(" ", "")
                            val uri = Uri.parse("https://wa.me/$phone?text=$encoded")
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                    ) {
                        Icon(Icons.Filled.Send, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Send WhatsApp Reminder", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Transaction History",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = SantheSubtext
                )
            }

            if (uiState.transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions yet", color = SantheSubtext)
                    }
                }
            } else {
                items(uiState.transactions, key = { it.transactionId }) { txn ->
                    TransactionRow(transaction = txn, onDelete = { viewModel.deleteTransaction(txn) })
                }
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: Transaction, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete transaction?") },
            text = { Text("This will remove ₹${formatAmount(transaction.amount)} from the ledger.") },
            confirmButton = {
                TextButton(onClick = { onDelete(); showDeleteDialog = false }) {
                    Text("Delete", color = SantheRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (transaction.type == "CREDIT") SantheRedLight else SantheGreenLight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (transaction.type == "CREDIT") Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                contentDescription = null,
                tint = if (transaction.type == "CREDIT") SantheRed else SantheGreen,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (transaction.type == "CREDIT") "Udari" else "Payment",
                    fontWeight = FontWeight.SemiBold,
                    color = SantheOnSurface,
                    fontSize = 15.sp
                )
                if (!transaction.note.isNullOrBlank()) {
                    Text(text = transaction.note, color = SantheSubtext, fontSize = 13.sp)
                }
                Text(
                    text = formatDate(transaction.timestamp),
                    fontSize = 12.sp,
                    color = SantheSubtext
                )
            }
            Text(
                text = "${if (transaction.type == "CREDIT") "+" else "-"}₹${formatAmount(transaction.amount)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (transaction.type == "CREDIT") SantheRed else SantheGreen
            )
            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Filled.DeleteOutline, null, tint = SantheSubtext, modifier = Modifier.size(18.dp))
            }
        }
    }
}
