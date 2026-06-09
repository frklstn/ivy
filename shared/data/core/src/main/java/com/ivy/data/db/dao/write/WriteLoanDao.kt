package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.LoanEntity
import java.util.UUID

interface WriteLoanDao {
    suspend fun save(value: LoanEntity)
    suspend fun saveMany(value: List<LoanEntity>)
    suspend fun deleteById(id: UUID)
    suspend fun deleteAll()
}
