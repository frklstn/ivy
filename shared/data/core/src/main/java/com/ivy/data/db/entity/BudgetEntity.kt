package com.ivy.data.db.entity

import androidx.annotation.Keep
import com.ivy.base.kotlinxserilzation.KSerializerUUID
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Suppress("DataClassDefaultValues")
@Keep
@Serializable
data class BudgetEntity(
    @SerialName("name")
    val name: String,
    @SerialName("amount")
    val amount: Double,

    @SerialName("categoryIdsSerialized")
    val categoryIdsSerialized: String?,
    @SerialName("accountIdsSerialized")
    val accountIdsSerialized: String?,

    @Deprecated("Obsolete field used for cloud sync. Can't be deleted because of backwards compatibility")
    @SerialName("isSynced")
    val isSynced: Boolean = false,
    @Deprecated("Obsolete field used for cloud sync. Can't be deleted because of backwards compatibility")
    @SerialName("isDeleted")
    val isDeleted: Boolean = false,

    @SerialName("orderId")
    val orderId: Double,
    @SerialName("id")
    @Serializable(with = KSerializerUUID::class)
    val id: UUID = UUID.randomUUID()
)
