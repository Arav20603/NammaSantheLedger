package com.namma.santhe.ledger.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.santhe.ledger.data.entity.Customer
import com.namma.santhe.ledger.ui.theme.*
import com.namma.santhe.ledger.viewmodel.HomeViewModel
import com.namma.santhe.ledger.utils.formatAmount
import com.namma.santhe.ledger.utils.initials

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAddTransaction: (Int) -> Unit,
    onNavigateToCustomerLedger: (Int) -> Unit,
    onNavigateToDailySummary: () -> Unit,
    onNavigateToAddCustomer: () -> Unit
) {
    val customers by viewModel.customers.collectAsState()
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showAddMenu by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = SantheBg,
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(visible = showAddMenu) {
                    Column(horizontalAlignment = Alignment.End) {
                        SmallFabMenuItem(
                            label = "New Customer + Udari",
                            icon = Icons.Filled.PersonAdd,
                            onClick = {
                                showAddMenu = false
                                onNavigateToAddCustomer()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                FloatingActionButton(
                    onClick = { showAddMenu = !showAddMenu },
                    containerColor = SantheOrange,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (showAddMenu) Icons.Filled.Close else Icons.Filled.Add,
                        contentDescription = "Add"
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(SantheOrange, SantheOrangeDark)
                            )
                        )
                        .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 28.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "ನಮ್ಮ ಸಂತೆ",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Ledger",
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(
                                onClick = onNavigateToDailySummary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.BarChart,
                                    contentDescription = "Daily Summary",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Outstanding card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Total Outstanding",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "₹${formatAmount(totalOutstanding)}",
                                    color = Color.White,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = (-0.5).sp
                                )
                                Text(
                                    text = "${customers.size} customer${if (customers.size != 1) "s" else ""}",
                                    color = Color.White.copy(alpha = 0.75f),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    placeholder = {
                        Text("Search customer name...", color = SantheSubtext)
                    },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = SantheSubtext)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear", tint = SantheSubtext)
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SantheOrange,
                        unfocusedBorderColor = SantheOutline,
                        focusedContainerColor = SantheSurface,
                        unfocusedContainerColor = SantheSurface
                    ),
                    singleLine = true
                )
            }

            if (customers.isEmpty()) {
                item {
                    EmptyState(onAddCustomer = onNavigateToAddCustomer)
                }
            } else {
                item {
                    Text(
                        text = "Customers",
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = SantheSubtext
                    )
                }
                items(customers, key = { it.customerId }) { customer ->
                    CustomerListItem(
                        customer = customer,
                        viewModel = viewModel,
                        onAddTransaction = { onNavigateToAddTransaction(customer.customerId) },
                        onViewLedger = { onNavigateToCustomerLedger(customer.customerId) }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomerListItem(
    customer: Customer,
    viewModel: HomeViewModel,
    onAddTransaction: () -> Unit,
    onViewLedger: () -> Unit
) {
    val balance by viewModel.getBalanceForCustomer(customer.customerId).collectAsState(initial = 0.0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onViewLedger() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SantheSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(SantheOrangeLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = customer.name.initials(),
                    color = SantheOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = SantheOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!customer.phone.isNullOrBlank()) {
                    Text(
                        text = customer.phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SantheSubtext
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                val balanceColor = when {
                    balance > 0 -> SantheRed
                    balance < 0 -> SantheGreen
                    else -> SantheSubtext
                }
                Text(
                    text = "₹${formatAmount(kotlin.math.abs(balance))}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = balanceColor
                )
                Text(
                    text = when {
                        balance > 0 -> "owes"
                        balance < 0 -> "overpaid"
                        else -> "settled"
                    },
                    fontSize = 12.sp,
                    color = balanceColor.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onAddTransaction,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(SantheOrangeLight)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Udari",
                    tint = SantheOrange,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SmallFabMenuItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(end = 4.dp)
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.Medium, color = SantheOnSurface, fontSize = 15.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Icon(icon, contentDescription = null, tint = SantheOrange, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun EmptyState(onAddCustomer: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🛒", fontSize = 56.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No customers yet",
            style = MaterialTheme.typography.titleMedium,
            color = SantheOnSurface
        )
        Text(
            text = "Tap + to add your first customer and start tracking Udari",
            style = MaterialTheme.typography.bodyMedium,
            color = SantheSubtext,
            modifier = Modifier.padding(top = 6.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onAddCustomer,
            colors = ButtonDefaults.buttonColors(containerColor = SantheOrange)
        ) {
            Icon(Icons.Filled.PersonAdd, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Customer")
        }
    }
}
