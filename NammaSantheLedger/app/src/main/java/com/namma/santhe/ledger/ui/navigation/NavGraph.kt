package com.namma.santhe.ledger.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.namma.santhe.ledger.ui.customer.AddCustomerScreen
import com.namma.santhe.ledger.ui.customer.CustomerLedgerScreen
import com.namma.santhe.ledger.ui.home.HomeScreen
import com.namma.santhe.ledger.ui.summary.DailySummaryScreen
import com.namma.santhe.ledger.ui.transaction.AddTransactionScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddTransaction : Screen("add_transaction/{customerId}") {
        fun createRoute(customerId: Int) = "add_transaction/$customerId"
    }
    object CustomerLedger : Screen("customer_ledger/{customerId}") {
        fun createRoute(customerId: Int) = "customer_ledger/$customerId"
    }
    object DailySummary : Screen("daily_summary")
    object AddCustomer : Screen("add_customer")
}

@Composable
fun NammaSantheNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToAddTransaction = { customerId ->
                    navController.navigate(Screen.AddTransaction.createRoute(customerId))
                },
                onNavigateToCustomerLedger = { customerId ->
                    navController.navigate(Screen.CustomerLedger.createRoute(customerId))
                },
                onNavigateToDailySummary = {
                    navController.navigate(Screen.DailySummary.route)
                },
                onNavigateToAddCustomer = {
                    navController.navigate(Screen.AddCustomer.route)
                }
            )
        }

        composable(
            route = Screen.AddTransaction.route,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getInt("customerId") ?: return@composable
            AddTransactionScreen(
                customerId = customerId,
                onBack = { navController.popBackStack() },
                onTransactionAdded = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CustomerLedger.route,
            arguments = listOf(navArgument("customerId") { type = NavType.IntType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getInt("customerId") ?: return@composable
            CustomerLedgerScreen(
                customerId = customerId,
                onBack = { navController.popBackStack() },
                onNavigateToAddTransaction = { cid ->
                    navController.navigate(Screen.AddTransaction.createRoute(cid))
                }
            )
        }

        composable(Screen.DailySummary.route) {
            DailySummaryScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.AddCustomer.route) {
            AddCustomerScreen(
                onBack = { navController.popBackStack() },
                onCustomerAdded = { customerId ->
                    navController.popBackStack()
                    navController.navigate(Screen.AddTransaction.createRoute(customerId))
                }
            )
        }
    }
}
