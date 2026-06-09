package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.LoanRecordEntity
import java.util.*

interface LoanRecordDao {
    suspend fun findAll(): List<LoanRecordEntity>
    suspend fun findByIsSyncedAndIsDeleted(
        synced: Boolean,
        deleted: Boolean = false
    ): List<LoanRecordEntity>
    suspend fun findById(id: UUID): LoanRecordEntity?
    suspend fun findAllByLoanId(loanId: UUID): List<LoanRecordEntity>
}
