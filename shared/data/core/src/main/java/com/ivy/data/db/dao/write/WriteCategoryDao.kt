package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.CategoryEntity
import java.util.UUID

interface WriteCategoryDao {
    suspend fun save(value: CategoryEntity)
    suspend fun saveMany(values: List<CategoryEntity>)
    suspend fun deleteById(id: UUID)
    suspend fun deleteAll()
}
