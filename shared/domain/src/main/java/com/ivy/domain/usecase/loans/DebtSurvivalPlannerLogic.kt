package com.ivy.domain.usecase.loans

import com.ivy.data.db.dao.read.LoanTrackerDao
import com.ivy.data.db.entity.LoanTrackerEntity
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

data class PlannerSummary(
    val survivalScore: Double,
    val monthlyIncome: Double,
    val totalRepayments: Double,
    val cashflowProjection: List<Double>,
    val alerts: List<String>,
    val activeLoans: List<LoanTrackerEntity>
)

class DebtSurvivalPlannerLogic @Inject constructor(
    private val loanTrackerDao: LoanTrackerDao
) {

    suspend fun getPlannerSummary(initialBalance: Double = 0.0): PlannerSummary {
        val loans = loanTrackerDao.findAll().filter { it.status == "active" }
        val settings = loanTrackerDao.getSettings()
        val timeline = loanTrackerDao.getIncomeTimeline()

        // 1. Calculate Monthly Income (use the latest effective timeline rate, fallback to default or total salary input)
        val salaryDay = settings?.salaryDay ?: 1
        val today = LocalDate.now()
        val monthlyIncome = timeline.lastOrNull { 
            LocalDate.parse(it.effectiveDate).isBefore(today) || LocalDate.parse(it.effectiveDate).isEqual(today) 
        }?.monthlyIncome ?: timeline.lastOrNull()?.monthlyIncome ?: 0.0

        // 2. Calculate Total Repayments
        val totalRepayments = loans.sumOf { it.monthlyPayment }

        // 3. Calculate Survival Score
        val survivalScore = when {
            totalRepayments == 0.0 -> 100.0
            monthlyIncome <= 0.0 -> 0.0
            totalRepayments >= monthlyIncome -> 0.0
            else -> ((1.0 - (totalRepayments / monthlyIncome)) * 100.0)
        }

        // 4. Calculate 12-Period Cashflow Projection
        val cashflowProjection = mutableListOf<Double>()
        var currentCash = initialBalance
        for (i in 1..12) {
            currentCash = currentCash + monthlyIncome - totalRepayments
            cashflowProjection.add(currentCash)
        }

        // 5. Generate Alerts / Warnings
        val alerts = mutableListOf<String>()
        if (survivalScore < 30.0 && totalRepayments > 0.0) {
            alerts.add("Kategori Bahaya: Pembayaran utang bulanan Anda melebihi 70% dari pendapatan!")
        } else if (survivalScore < 50.0 && totalRepayments > 0.0) {
            alerts.add("Peringatan: Rasio utang Anda tinggi (di atas 50% dari pendapatan).")
        }

        // Check if due day is before salary day
        loans.forEach { loan ->
            val loanDueDay = loan.dueDay
            if (loanDueDay < salaryDay) {
                alerts.add("Risiko Denda: Tanggal jatuh tempo '${loan.appName}' (tgl $loanDueDay) berada sebelum hari gajian Anda (tgl $salaryDay).")
            }
        }

        // Detect multiple due dates clashing (within 3 days of each other)
        val sortedLoans = loans.sortedBy { it.dueDay }
        for (i in 0 until sortedLoans.size - 1) {
            val current = sortedLoans[i]
            val next = sortedLoans[i + 1]
            if (next.dueDay - current.dueDay <= 3) {
                alerts.add("Penumpukan Jatuh Tempo: Jatuh tempo '${current.appName}' (tgl ${current.dueDay}) dan '${next.appName}' (tgl ${next.dueDay}) berdekatan (selisih <= 3 hari).")
            }
        }

        return PlannerSummary(
            survivalScore = survivalScore,
            monthlyIncome = monthlyIncome,
            totalRepayments = totalRepayments,
            cashflowProjection = cashflowProjection,
            alerts = alerts,
            activeLoans = loans
        )
    }
}
