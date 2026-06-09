package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.SettingsEntity
import java.util.UUID

interface WriteSettingsDao {
    suspend fun save(value: SettingsEntity)
    suspend fun saveMany(value: List<SettingsEntity>)
    suspend fun deleteById(id: UUID)
    suspend fun deleteAll()
}
