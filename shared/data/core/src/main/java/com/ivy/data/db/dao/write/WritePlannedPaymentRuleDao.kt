package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.PlannedPaymentRuleEntity
import java.util.UUID

interface WritePlannedPaymentRuleDao {
    suspend fun save(value: PlannedPaymentRuleEntity)
    suspend fun saveMany(value: List<PlannedPaymentRuleEntity>)
    suspend fun deletedByAccountId(accountId: UUID)
    suspend fun deleteById(id: UUID)
    suspend fun deleteAll()
}
