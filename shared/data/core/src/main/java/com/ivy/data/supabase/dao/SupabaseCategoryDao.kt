package com.ivy.data.supabase.dao

import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.write.WriteCategoryDao
import com.ivy.data.db.entity.CategoryEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.supabase.SupabaseClient
import io.supabase.postgrest.from
import io.supabase.postgrest.postgrest
import io.supabase.postgrest.query.order.Order
import java.util.UUID
import javax.inject.Inject

class SupabaseCategoryDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), CategoryDao {

    override suspend fun findAll(deleted: Boolean): List<CategoryEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("categories").select {
            filter { eq("workspace_id", wsId); eq("isDeleted", deleted) }
            order("orderNum", Order.ASCENDING)
        }.decodeList<CategoryEntity>()
    }

    override suspend fun findById(id: UUID): CategoryEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("categories").select {
            filter { eq("workspace_id", wsId); eq("id", id.toString()) }
        }.decodeList<CategoryEntity>().firstOrNull()
    }

    override suspend fun findMaxOrderNum(): Double? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("categories").select {
            filter { eq("workspace_id", wsId) }
            order("orderNum", Order.DESCENDING)
            limit(1)
        }.decodeList<CategoryEntity>().firstOrNull()?.orderNum
    }
}

class SupabaseWriteCategoryDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteCategoryDao {

    override suspend fun save(value: CategoryEntity) {
        supabaseClient.postgrest.from("categories").upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun saveMany(values: List<CategoryEntity>) {
        if (values.isEmpty()) return
        supabaseClient.postgrest.from("categories").upsert(toJsonListWithWorkspace(values, workspaceId()))
    }

    override suspend fun deleteById(id: UUID) {
        supabaseClient.postgrest.from("categories").delete {
            filter { eq("workspace_id", workspaceId()); eq("id", id.toString()) }
        }
    }

    override suspend fun deleteAll() {
        supabaseClient.postgrest.from("categories").delete { filter { eq("workspace_id", workspaceId()) } }
    }
}
