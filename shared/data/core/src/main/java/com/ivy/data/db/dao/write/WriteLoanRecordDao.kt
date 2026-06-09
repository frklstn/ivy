package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.LoanRecordEntity
import java.util.UUID

interface WriteLoanRecordDao {
    suspend fun save(value: LoanRecordEntity)
    suspend fun saveMany(value: List<LoanRecordEntity>)
    suspend fun deleteById(id: UUID)
    suspend fun deleteAll()
}
