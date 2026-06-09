package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.CategoryEntity
import java.util.*

interface CategoryDao {
    suspend fun findAll(deleted: Boolean = false): List<CategoryEntity>
    suspend fun findById(id: UUID): CategoryEntity?
    suspend fun findMaxOrderNum(): Double?
}
