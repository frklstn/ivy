package com.ivy.data.supabase.dao

import com.ivy.data.db.dao.read.TagDao
import com.ivy.data.db.dao.read.TagAssociationDao
import com.ivy.data.db.dao.write.WriteTagDao
import com.ivy.data.db.dao.write.WriteTagAssociationDao
import com.ivy.data.db.entity.TagEntity
import com.ivy.data.db.entity.TagAssociationEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.supabase.SupabaseClient
import io.supabase.postgrest.from
import io.supabase.postgrest.postgrest
import io.supabase.postgrest.query.order.Order
import java.util.UUID
import javax.inject.Inject

class SupabaseTagDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), TagDao {

    private val table = "tags"

    override suspend fun findAll(): List<TagEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId) }
            order("orderNum", Order.ASCENDING)
        }.decodeList<TagEntity>()
    }

    override suspend fun findByIds(id: UUID): TagEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("id", id.toString()) }
        }.decodeList<TagEntity>().firstOrNull()
    }

    override suspend fun findByIds(ids: List<UUID>): List<TagEntity> {
        if (ids.isEmpty()) return emptyList()
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); isIn("id", ids.map { it.toString() }) }
        }.decodeList<TagEntity>()
    }

    override suspend fun findByText(text: String): List<TagEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); ilike("name", "%$text%") }
        }.decodeList<TagEntity>()
    }

    override suspend fun findTagsByAssociatedIds(ids: List<UUID>): Map<UUID, List<TagEntity>> {
        if (ids.isEmpty()) return emptyMap()
        val wsId = workspaceId()
        // Query tag_associations to get the mapping, then fetch tags
        val associations = supabaseClient.postgrest.from("tags_association").select {
            filter { eq("workspace_id", wsId); isIn("associatedId", ids.map { it.toString() }) }
        }.decodeList<TagAssociationEntity>()

        if (associations.isEmpty()) return emptyMap()

        val tagIds = associations.map { it.tagId.toString() }.distinct()
        val tags = supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); isIn("id", tagIds) }
        }.decodeList<TagEntity>()

        val tagMap = tags.associateBy { it.id }
        return associations.groupBy { it.associatedId }.mapValues { (_, assocs) ->
            assocs.mapNotNull { tagMap[it.tagId] }
        }
    }

    override suspend fun findTagsByAssociatedId(id: UUID): List<TagEntity> {
        val wsId = workspaceId()
        val associations = supabaseClient.postgrest.from("tags_association").select {
            filter { eq("workspace_id", wsId); eq("associatedId", id.toString()) }
        }.decodeList<TagAssociationEntity>()

        if (associations.isEmpty()) return emptyList()

        val tagIds = associations.map { it.tagId.toString() }
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); isIn("id", tagIds) }
        }.decodeList<TagEntity>()
    }
}

class SupabaseWriteTagDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteTagDao {

    private val table = "tags"

    override suspend fun save(value: TagEntity) {
        supabaseClient.postgrest.from(table).upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun save(value: List<TagEntity>) {
        if (value.isEmpty()) return
        supabaseClient.postgrest.from(table).upsert(toJsonListWithWorkspace(value, workspaceId()))
    }

    override suspend fun deleteById(id: UUID) {
        supabaseClient.postgrest.from(table).delete {
            filter { eq("workspace_id", workspaceId()); eq("id", id.toString()) }
        }
    }

    override suspend fun deleteAll() {
        supabaseClient.postgrest.from(table).delete { filter { eq("workspace_id", workspaceId()) } }
    }
}

class SupabaseTagAssociationDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), TagAssociationDao {

    private val table = "tags_association"

    override suspend fun findAll(): List<TagAssociationEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId) }
        }.decodeList<TagAssociationEntity>()
    }

    override suspend fun findById(id: UUID): TagAssociationEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("tagId", id.toString()) }
            limit(1)
        }.decodeList<TagAssociationEntity>().firstOrNull()
    }

    override suspend fun findByAssociatedId(associatedId: UUID): TagAssociationEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("associatedId", associatedId.toString()) }
            limit(1)
        }.decodeList<TagAssociationEntity>().firstOrNull()
    }

    override suspend fun findByAllAssociatedIdForTagId(tagIds: List<UUID>): Map<UUID, List<TagAssociationEntity>> {
        if (tagIds.isEmpty()) return emptyMap()
        val wsId = workspaceId()
        val results = supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); isIn("tagId", tagIds.map { it.toString() }) }
        }.decodeList<TagAssociationEntity>()
        return results.groupBy { it.tagId }
    }
}

class SupabaseWriteTagAssociationDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteTagAssociationDao {

    private val table = "tags_association"

    override suspend fun save(value: TagAssociationEntity) {
        supabaseClient.postgrest.from(table).upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun save(value: List<TagAssociationEntity>) {
        if (value.isEmpty()) return
        supabaseClient.postgrest.from(table).upsert(toJsonListWithWorkspace(value, workspaceId()))
    }

    override suspend fun deleteAll() {
        supabaseClient.postgrest.from(table).delete { filter { eq("workspace_id", workspaceId()) } }
    }

    override suspend fun deleteId(tagId: UUID, associatedId: UUID) {
        supabaseClient.postgrest.from(table).delete {
            filter {
                eq("workspace_id", workspaceId())
                eq("tagId", tagId.toString())
                eq("associatedId", associatedId.toString())
            }
        }
    }

    override suspend fun deleteAssociationsByTagId(tagId: UUID) {
        supabaseClient.postgrest.from(table).delete {
            filter { eq("workspace_id", workspaceId()); eq("tagId", tagId.toString()) }
        }
    }

    override suspend fun deleteAssociationsByAssociateId(associatedId: UUID) {
        supabaseClient.postgrest.from(table).delete {
            filter { eq("workspace_id", workspaceId()); eq("associatedId", associatedId.toString()) }
        }
    }
}
