package com.ivy.loans.pinjol

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ivy.data.db.entity.LoanTrackerEntity
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.domain.usecase.loans.DebtSurvivalPlannerLogic
import com.ivy.domain.usecase.loans.PlannerSummary
import com.ivy.navigation.PinjolTrackerScreen
import com.ivy.navigation.navigation
import kotlinx.coroutines.launch

@Composable
fun BoxWithConstraintsScope.PinjolTrackerScreen(screen: PinjolTrackerScreen) {
    val nav = navigation()
    val viewModel: PinjolTrackerViewModel = viewModel()

    var summary by remember { mutableStateOf<PlannerSummary?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            summary = viewModel.loadSummary()
            isLoading = false
        }
    }

    PinjolTrackerUI(
        summary = summary,
        isLoading = isLoading,
        onBack = { nav.back() },
        onRefresh = {
            isLoading = true
            scope.launch {
                summary = viewModel.loadSummary()
                isLoading = false
            }
        }
    )
}

@Composable
private fun PinjolTrackerUI(
    summary: PlannerSummary?,
    isLoading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(UI.colors.pure)
            .systemBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(UI.colors.medium)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "←",
                    fontSize = 18.sp,
                    color = UI.colors.pureInverse
                )
            }

            Spacer(Modifier.padding(start = 16.dp))

            Text(
                text = "Pinjol Tracker",
                style = UI.typo.h2.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (summary == null) {
            EmptyPinjolState(onRefresh = onRefresh)
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { SurvivalScoreCard(summary) }
                item { CashflowCard(summary) }
                if (summary.alerts.isNotEmpty()) {
                    item { AlertsCard(summary.alerts) }
                }
                item {
                    Text(
                        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
                        text = "Pinjaman Aktif (${summary.activeLoans.size})",
                        style = UI.typo.b1.style(
                            fontWeight = FontWeight.ExtraBold,
                            color = UI.colors.pureInverse
                        )
                    )
                }
                items(summary.activeLoans) { loan ->
                    LoanItem(loan = loan)
                }
                item {
                    if (summary.activeLoans.isEmpty()) {
                        EmptyLoansState()
                    }
                }
                item { Spacer(Modifier.height(120.dp)) }
            }
        }
    }
}

@Composable
private fun SurvivalScoreCard(summary: PlannerSummary) {
    val score = summary.survivalScore.coerceIn(0.0, 100.0)
    val scoreColor = when {
        score >= 60 -> Color(0xFF10B981) // Green
        score >= 30 -> Color(0xFFF59E0B) // Amber
        else -> Color(0xFFEF4444) // Red
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = UI.colors.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Survival Score",
                        style = UI.typo.b2.style(
                            color = UI.colors.pureInverse.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = "${score.toInt()}/100",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor
                    )
                }

                Box(
                    modifier = Modifier.size(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { (score / 100f).toFloat() },
                        modifier = Modifier.fillMaxSize(),
                        color = scoreColor,
                        trackColor = UI.colors.pure,
                        strokeWidth = 6.dp,
                    )
                    Text(
                        text = "${score.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = scoreColor
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Pendapatan Bulanan",
                        style = UI.typo.c.style(color = UI.colors.pureInverse.copy(alpha = 0.6f))
                    )
                    Text(
                        text = formatCurrency(summary.monthlyIncome),
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Cicilan Bulanan",
                        style = UI.typo.c.style(color = UI.colors.pureInverse.copy(alpha = 0.6f))
                    )
                    Text(
                        text = formatCurrency(summary.totalRepayments),
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            val repaymentRatio = if (summary.monthlyIncome > 0) summary.totalRepayments / summary.monthlyIncome else 0.0
            LinearProgressIndicator(
                progress = { repaymentRatio.toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = scoreColor,
                trackColor = UI.colors.pure
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${(repaymentRatio * 100).toInt()}% dari pendapatan untuk cicilan",
                style = UI.typo.c.style(color = UI.colors.pureInverse.copy(alpha = 0.6f))
            )
        }
    }
}

@Composable
private fun CashflowCard(summary: PlannerSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = UI.colors.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Proyeksi Arus Kas 12 Bulan",
                style = UI.typo.b1.style(
                    fontWeight = FontWeight.ExtraBold,
                    color = UI.colors.pureInverse
                )
            )

            Spacer(Modifier.height(12.dp))

            summary.cashflowProjection.forEachIndexed { index, amount ->
                val isNegative = amount < 0
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bulan ${index + 1}",
                        style = UI.typo.b2.style(color = UI.colors.pureInverse.copy(alpha = 0.7f))
                    )
                    Text(
                        text = formatCurrency(amount),
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.Bold,
                            color = if (isNegative) Color(0xFFEF4444) else Color(0xFF10B981)
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AlertsCard(alerts: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color(0xFFD97706),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.padding(start = 8.dp))
                Text(
                    text = "Peringatan",
                    style = UI.typo.b1.style(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF92400E)
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            alerts.forEach { alert ->
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = "• $alert",
                    style = UI.typo.b2.style(color = Color(0xFF78350F))
                )
            }
        }
    }
}

@Composable
private fun LoanItem(loan: LoanTrackerEntity) {
    val statusColor = if (loan.status == "active") Color(0xFF10B981) else Color(0xFF6B7280)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = UI.colors.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = loan.appName,
                        style = UI.typo.b1.style(
                            fontWeight = FontWeight.ExtraBold,
                            color = UI.colors.pureInverse
                        )
                    )
                    Text(
                        text = "Jatuh Tempo: Tgl ${loan.dueDay} setiap bulan",
                        style = UI.typo.c.style(color = UI.colors.pureInverse.copy(alpha = 0.6f))
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (loan.status == "active") "Aktif" else "Lunas",
                        style = UI.typo.c.style(
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Cicilan Bulanan",
                        style = UI.typo.c.style(color = UI.colors.pureInverse.copy(alpha = 0.6f))
                    )
                    Text(
                        text = formatCurrency(loan.monthlyPayment),
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Tenor",
                        style = UI.typo.c.style(color = UI.colors.pureInverse.copy(alpha = 0.6f))
                    )
                    Text(
                        text = "${loan.tenureMonths} bulan",
                        style = UI.typo.b2.style(
                            fontWeight = FontWeight.Bold,
                            color = UI.colors.pureInverse
                        )
                    )
                }
            }

            loan.totalRemainingBalance?.let { remaining ->
                Spacer(Modifier.height(8.dp))
                val totalRepaid = loan.totalRepayment - remaining
                val progress = if (loan.totalRepayment > 0) (totalRepaid / loan.totalRepayment) else 0.0
                LinearProgressIndicator(
                    progress = { progress.toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF10B981),
                    trackColor = UI.colors.pure
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Sisa: ${formatCurrency(remaining)}",
                    style = UI.typo.c.style(color = UI.colors.pureInverse.copy(alpha = 0.6f))
                )
            }
        }
    }
}

@Composable
private fun EmptyLoansState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Belum ada pinjaman aktif",
            style = UI.typo.b1.style(
                color = UI.colors.pureInverse.copy(alpha = 0.5f),
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Tambahkan data pinjaman Anda untuk memantau cicilan dan kesehatan finansial Anda.",
            style = UI.typo.b2.style(
                color = UI.colors.pureInverse.copy(alpha = 0.4f),
                textAlign = TextAlign.Center
            )
        )
    }
}

@Composable
private fun EmptyPinjolState(onRefresh: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Gagal memuat data",
            style = UI.typo.b1.style(
                fontWeight = FontWeight.Bold,
                color = UI.colors.pureInverse
            )
        )
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onRefresh() }
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Coba Lagi",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatCurrency(amount: Double): String {
    return if (amount >= 0) {
        "Rp ${"%,.0f".format(amount)}"
    } else {
        "-Rp ${"%,.0f".format(-amount)}"
    }
}
