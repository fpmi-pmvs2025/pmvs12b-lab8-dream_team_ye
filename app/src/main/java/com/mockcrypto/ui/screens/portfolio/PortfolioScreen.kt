package com.mockcrypto.ui.screens.portfolio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mockcrypto.R
import com.mockcrypto.domain.model.DemoAccountState
import com.mockcrypto.domain.model.PortfolioItem
import com.mockcrypto.domain.model.Transaction
import com.mockcrypto.domain.model.TransactionType
import java.math.BigDecimal
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioScreen(
    onCryptoClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PortfolioViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val pullRefreshState = rememberPullToRefreshState()
    
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.loadPortfolioData()
            pullRefreshState.endRefresh()
        }
    }
    
    // Show operation result if there's any
    LaunchedEffect(uiState.operationSuccess, uiState.operationMessage) {
        val message = uiState.operationMessage
        if (uiState.operationSuccess != null && message != null) {
            snackbarHostState.showSnackbar(
                message = message
            )
            viewModel.resetOperationStatus()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.portfolio_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .nestedScroll(pullRefreshState.nestedScrollConnection)
        ) {
            if (uiState.isLoading && uiState.accountState == null) {
                LoadingIndicator()
            } else if (uiState.error != null && uiState.accountState == null) {
                ErrorState(
                    error = uiState.error ?: "Unknown error",
                    onRetry = { viewModel.loadPortfolioData() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AccountSummaryCard(
                            accountState = uiState.accountState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                    
                    if (uiState.portfolioItems.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.your_assets),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                        
                        items(uiState.portfolioItems) { item ->
                            PortfolioItemCard(
                                portfolioItem = item,
                                onItemClick = { onCryptoClick(item.cryptoId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    } else {
                        item {
                            EmptyPortfolioMessage(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }
                    }
                    
                    if (uiState.transactions.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.transaction_history),
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                        
                        items(uiState.transactions) { transaction ->
                            TransactionItem(
                                transaction = transaction,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    // Add some space at the bottom
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            
            // Show pull-to-refresh indicator only when refreshing
            if (pullRefreshState.isRefreshing) {
                PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}

@Composable
private fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = stringResource(R.string.loading))
        }
    }
}

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = stringResource(R.string.try_again))
            }
        }
    }
}

@Composable
private fun AccountSummaryCard(
    accountState: DemoAccountState?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.account_summary),
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (accountState != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.available_balance),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$${formatCurrency(accountState.balance)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = stringResource(R.string.portfolio_value),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "$${formatCurrency(accountState.totalPortfolioValue)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Profit/Loss indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (accountState.totalProfitLoss >= BigDecimal.ZERO)
                                Color(0xFF4CAF50).copy(alpha = 0.1f)
                            else
                                Color(0xFFF44336).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (accountState.totalProfitLoss >= BigDecimal.ZERO)
                            Icons.Default.ArrowUpward
                        else
                            Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = if (accountState.totalProfitLoss >= BigDecimal.ZERO)
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFF44336)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = if (accountState.totalProfitLoss >= BigDecimal.ZERO)
                            "+$${formatCurrency(accountState.totalProfitLoss)}"
                        else
                            "-$${formatCurrency(accountState.totalProfitLoss.abs())}",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (accountState.totalProfitLoss >= BigDecimal.ZERO)
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFF44336)
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.account_data_not_available),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PortfolioItemCard(
    portfolioItem: PortfolioItem,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onItemClick,
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = portfolioItem.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = portfolioItem.symbol,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${formatCurrency(portfolioItem.currentValue)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${portfolioItem.amount} ${portfolioItem.symbol}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Calculate and display profit/loss percentage
                val profitLossAmount = portfolioItem.currentValue - (portfolioItem.amount * portfolioItem.averageBuyPrice)
                val profitLossPercentage = if (portfolioItem.averageBuyPrice > BigDecimal.ZERO) {
                    profitLossAmount.divide(portfolioItem.amount * portfolioItem.averageBuyPrice, 4, BigDecimal.ROUND_HALF_UP) * BigDecimal(100)
                } else {
                    BigDecimal.ZERO
                }
                
                val isProfitable = profitLossAmount >= BigDecimal.ZERO
                val profitLossColor = if (isProfitable) Color(0xFF4CAF50) else Color(0xFFF44336)
                
                Text(
                    text = if (isProfitable) 
                        "+${profitLossPercentage.setScale(2, BigDecimal.ROUND_HALF_UP)}%" 
                    else 
                        "${profitLossPercentage.setScale(2, BigDecimal.ROUND_HALF_UP)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = profitLossColor
                )
            }
        }
    }
}

@Composable
private fun EmptyPortfolioMessage(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = stringResource(R.string.empty_portfolio_message),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = stringResource(R.string.empty_portfolio_instruction),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Transaction type indicator
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .background(
                        color = if (transaction.type == TransactionType.BUY)
                            Color(0xFF4CAF50)
                        else
                            Color(0xFFF44336),
                        shape = RoundedCornerShape(4.dp)
                    )
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (transaction.type == TransactionType.BUY)
                        stringResource(R.string.bought_amount, transaction.amount, transaction.symbol)
                    else
                        stringResource(R.string.sold_amount, transaction.amount, transaction.symbol),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = transaction.timestamp.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "$${formatCurrency(transaction.totalCost)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "@$${formatCurrency(transaction.pricePerUnit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Divider(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )
    }
}

private fun formatCurrency(value: BigDecimal): String {
    return value.setScale(2, BigDecimal.ROUND_HALF_UP).toString()
} 