package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.AccountEntity
import java.util.*

interface AccountDao {
    suspend fun findAll(deleted: Boolean = false): List<AccountEntity>
    suspend fun findById(id: UUID): AccountEntity?
    suspend fun findMaxOrderNum(): Double?
}
