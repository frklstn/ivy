package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.SettingsEntity
import java.util.*

interface SettingsDao {
    suspend fun findFirst(): SettingsEntity
    suspend fun findFirstOrNull(): SettingsEntity?
    suspend fun findAll(): List<SettingsEntity>
    suspend fun findById(id: UUID): SettingsEntity?
}
