package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.AccountEntity
import java.util.UUID

interface WriteAccountDao {
    suspend fun save(value: AccountEntity)
    suspend fun saveMany(values: List<AccountEntity>)
    suspend fun deleteById(id: UUID)
    suspend fun deleteAll()
}
