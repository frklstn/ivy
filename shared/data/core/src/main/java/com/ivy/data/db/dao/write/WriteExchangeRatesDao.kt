package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.ExchangeRateEntity

interface WriteExchangeRatesDao {
    suspend fun save(value: ExchangeRateEntity)
    suspend fun saveMany(value: List<ExchangeRateEntity>)
    suspend fun deleteByBaseCurrencyAndCurrency(baseCurrency: String, currency: String)
    suspend fun deleteAll()
}
