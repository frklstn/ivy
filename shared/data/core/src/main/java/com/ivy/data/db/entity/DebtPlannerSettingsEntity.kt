package com.ivy.data.db.entity

import androidx.annotation.Keep
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Suppress("DataClassDefaultValues")
@Keep
@Serializable
data class DebtPlannerSettingsEntity(
    @SerialName("workspace_id")
    @Serializable(with = KSerializerUUID::class)
    val workspaceId: UUID,

    @SerialName("salary_day")
    val salaryDay: Int = 1
)
