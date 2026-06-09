package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.ExchangeRateEntity
import kotlinx.coroutines.flow.Flow

interface ExchangeRatesDao {
    fun findAll(): Flow<List<ExchangeRateEntity>>
    suspend fun findAllManuallyOverridden(): List<ExchangeRateEntity>
    suspend fun findByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    ): ExchangeRateEntity?
}
