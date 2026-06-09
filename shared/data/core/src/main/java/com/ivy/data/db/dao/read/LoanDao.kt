package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.LoanEntity
import java.util.*

interface LoanDao {
    suspend fun findAll(): List<LoanEntity>
    suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<LoanEntity>
    suspend fun findById(id: UUID): LoanEntity?
    suspend fun findMaxOrderNum(): Double?
}
