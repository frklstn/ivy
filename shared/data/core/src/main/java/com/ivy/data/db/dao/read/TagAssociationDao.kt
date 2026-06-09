package com.ivy.data.db.dao.read

import com.ivy.data.db.entity.TagAssociationEntity
import java.util.UUID

interface TagAssociationDao {
    suspend fun findAll(): List<TagAssociationEntity>
    suspend fun findById(id: UUID): TagAssociationEntity?
    suspend fun findByAssociatedId(associatedId: UUID): TagAssociationEntity?
    suspend fun findByAllAssociatedIdForTagId(tagIds: List<UUID>): Map<UUID, List<TagAssociationEntity>>
}
