package com.ivy.data.supabase.dao

import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.write.WriteSettingsDao
import com.ivy.data.db.entity.SettingsEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.supabase.SupabaseClient
import io.supabase.postgrest.from
import io.supabase.postgrest.postgrest
import java.util.UUID
import javax.inject.Inject

class SupabaseSettingsDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), SettingsDao {

    private val table = "settings"

    override suspend fun findFirst(): SettingsEntity {
        return findFirstOrNull() ?: throw NoSuchElementException("No settings found")
    }

    override suspend fun findFirstOrNull(): SettingsEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId) }
            limit(1)
        }.decodeList<SettingsEntity>().firstOrNull()
    }

    override suspend fun findAll(): List<SettingsEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId) }
        }.decodeList<SettingsEntity>()
    }

    override suspend fun findById(id: UUID): SettingsEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("id", id.toString()) }
        }.decodeList<SettingsEntity>().firstOrNull()
    }
}

class SupabaseWriteSettingsDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteSettingsDao {

    private val table = "settings"

    override suspend fun save(value: SettingsEntity) {
        supabaseClient.postgrest.from(table).upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun saveMany(value: List<SettingsEntity>) {
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
