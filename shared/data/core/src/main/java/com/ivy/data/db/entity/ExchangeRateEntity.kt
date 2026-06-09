package com.ivy.data.db.entity

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ExchangeRateEntity(
    @SerialName("baseCurrency")
    val baseCurrency: String,
    @SerialName("currency")
    val currency: String,
    @SerialName("rate")
    val rate: Double,
    @SerialName("manualOverride")
    val manualOverride: Boolean = false,
)
