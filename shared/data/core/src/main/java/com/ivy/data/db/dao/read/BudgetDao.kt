package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.BudgetEntity
import java.util.*

interface BudgetDao {
    suspend fun findAll(): List<BudgetEntity>
    suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<BudgetEntity>
    suspend fun findById(id: UUID): BudgetEntity?
    suspend fun findMaxOrderNum(): Double?
}
