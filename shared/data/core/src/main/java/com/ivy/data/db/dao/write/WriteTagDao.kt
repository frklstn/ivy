package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.TagEntity
import java.util.UUID

interface WriteTagDao {
    suspend fun save(value: TagEntity)
    suspend fun save(value: List<TagEntity>)
    suspend fun deleteById(id: UUID)
    suspend fun deleteAll()
}
