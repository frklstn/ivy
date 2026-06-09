package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.PlannedPaymentRuleEntity
import java.util.*

interface PlannedPaymentRuleDao {
    suspend fun findAll(): List<PlannedPaymentRuleEntity>
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<PlannedPaymentRuleEntity>
    suspend fun findAllByOneTime(oneTime: Boolean): List<PlannedPaymentRuleEntity>
    suspend fun findById(id: UUID): PlannedPaymentRuleEntity?
    suspend fun countPlannedPayments(): Long
}
