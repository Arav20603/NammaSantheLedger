package com.namma.santhe.ledger.ui.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.namma.santhe.ledger.ui.theme.*
import com.namma.santhe.ledger.utils.formatAmount
import com.namma.santhe.ledger.utils.formatDate
import com.namma.santhe.ledger.viewmodel.SummaryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySummaryScreen(
    viewModel: SummaryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val summary by viewModel.dailySummary.collectAsState()
    val today = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).format(Date())

    Scaffold(
        containerColor = SantheBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Daily Summary")
                        Text(today, style = MaterialTheme.typography.bodyMedium, color = SantheSubtext)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SantheSurface)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                // Summary tiles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryTile(
                        modifier = Modifier.weight(1f),
                        label = "Today's Udari",
                        amount = summary.totalCredit,
                        color = SantheRed,
                        bg = SantheRedLight,
                        emoji = "📤"
                    )
                    SummaryTile(
                        modifier = Modifier.weight(1f),
                        label = "Collected",
                        amount = summary.totalPayments,
                        color = SantheGreen,
                        bg = SantheGreenLight,
                        emoji = "📥"
                    )
                }
            }

            item {
                // Net card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (summary.netPending > 0) SantheRed else SantheGreen
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Net Pending Today",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 14.sp
                            )
                            Text(
                                "₹${formatAmount(summary.netPending)}",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${summary.transactionCount}",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text("transactions", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Today's Transactions",
                    style = MaterialTheme.typography.labelMedium,
                    color = SantheSubtext,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            if (summary.transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📊", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No transactions today", color = SantheSubtext)
                        }
                    }
                }
            } else {
                items(summary.transactions) { txn ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (txn.type == "CREDIT") SantheRedLight else SantheGreenLight
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (txn.type == "CREDIT") "Udari" else "Payment",
                                    fontWeight = FontWeight.SemiBold,
                                    color = SantheOnSurface
                                )
                                Text(
                                    text = formatDate(txn.timestamp),
                                    fontSize = 12.sp,
                                    color = SantheSubtext
                                )
                            }
                            Text(
                                text = "${if (txn.type == "CREDIT") "+" else "-"}₹${formatAmount(txn.amount)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (txn.type == "CREDIT") SantheRed else SantheGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryTile(
    modifier: Modifier = Modifier,
    label: String,
    amount: Double,
    color: Color,
    bg: Color,
    emoji: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹${formatAmount(amount)}",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = color
            )
            Text(text = label, fontSize = 13.sp, color = color.copy(alpha = 0.8f))
        }
    }
}
