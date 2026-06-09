package com.ivy.data.db.entity

import androidx.annotation.Keep
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Suppress("DataClassDefaultValues")
@Keep
@Serializable
data class LoanTrackerEntity(
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID = UUID.randomUUID(),

    @SerialName("workspace_id")
    @Serializable(with = KSerializerUUID::class)
    val workspaceId: UUID,

    @SerialName("app_name")
    val appName: String,

    @SerialName("category")
    val category: String = "pinjol",

    @SerialName("amount_received")
    val amountReceived: Double,

    @SerialName("total_repayment")
    val totalRepayment: Double,

    @SerialName("monthly_payment")
    val monthlyPayment: Double,

    @SerialName("tenure_months")
    val tenureMonths: Int,

    @SerialName("due_day")
    val dueDay: Int,

    @SerialName("start_date")
    val startDate: String, // ISO date string (YYYY-MM-DD)

    @SerialName("salary_date")
    val salaryDate: Int? = null,

    @SerialName("status")
    val status: String = "active", // active, paid_off

    @SerialName("notes")
    val notes: String? = null,

    @SerialName("payment_frequency")
    val paymentFrequency: String = "monthly",

    @SerialName("end_date")
    val endDate: String? = null,

    @SerialName("total_remaining_balance")
    val totalRemainingBalance: Double? = null,

    @SerialName("penalty_fee")
    val penaltyFee: Double? = null,

    @SerialName("can_early_payoff")
    val canEarlyPayoff: Boolean = false
)
