package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.BudgetEntity
import java.util.UUID

interface WriteBudgetDao {
    suspend fun save(value: BudgetEntity)
    suspend fun saveMany(value: List<BudgetEntity>)
    suspend fun deleteById(id: UUID)
    suspend fun deleteAll()
}
