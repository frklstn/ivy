package com.ivy.data.db.dao.write

import com.ivy.data.db.entity.TagAssociationEntity
import java.util.*

interface WriteTagAssociationDao {
    suspend fun save(value: TagAssociationEntity)
    suspend fun save(value: List<TagAssociationEntity>)
    suspend fun deleteAll()
    suspend fun deleteId(tagId: UUID, associatedId: UUID)
    suspend fun deleteAssociationsByTagId(tagId: UUID)
    suspend fun deleteAssociationsByAssociateId(associatedId: UUID)
}
