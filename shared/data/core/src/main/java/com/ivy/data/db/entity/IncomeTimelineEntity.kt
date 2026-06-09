package com.ivy.data.db.entity

import androidx.annotation.Keep
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Suppress("DataClassDefaultValues")
@Keep
@Serializable
data class IncomeTimelineEntity(
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID = UUID.randomUUID(),

    @SerialName("workspace_id")
    @Serializable(with = KSerializerUUID::class)
    val workspaceId: UUID,

    @SerialName("effective_date")
    val effectiveDate: String, // ISO date string (YYYY-MM-DD)

    @SerialName("monthly_income")
    val monthlyIncome: Double
)
