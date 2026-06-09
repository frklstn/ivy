package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.TagEntity
import java.util.*

interface TagDao {
    suspend fun findAll(): List<TagEntity>
    suspend fun findByIds(id: UUID): TagEntity?
    suspend fun findByIds(ids: List<UUID>): List<TagEntity>
    suspend fun findByText(text: String): List<TagEntity>
    suspend fun findTagsByAssociatedIds(ids: List<UUID>): Map<UUID, List<TagEntity>>
    suspend fun findTagsByAssociatedId(id: UUID): List<TagEntity>
}
