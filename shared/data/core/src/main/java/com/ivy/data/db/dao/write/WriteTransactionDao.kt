package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.TransactionEntity
import java.util.UUID

interface WriteTransactionDao {
    suspend fun save(value: TransactionEntity)
    suspend fun saveMany(value: List<TransactionEntity>)
    suspend fun deletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID)
    suspend fun deleteById(id: UUID)
    suspend fun deleteAllByAccountId(accountId: UUID)
    suspend fun deleteAll()
}
