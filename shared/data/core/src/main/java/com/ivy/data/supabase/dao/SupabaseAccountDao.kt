package com.ivy.data.supabase.dao

import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.entity.AccountEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import java.util.UUID
import javax.inject.Inject

class SupabaseAccountDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), AccountDao {

    override suspend fun findAll(deleted: Boolean): List<AccountEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("accounts")
            .select {
                filter {
                    eq("workspace_id", wsId)
                    eq("isDeleted", deleted)
                }
                order("orderNum", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
            }
            .decodeList<AccountEntity>()
    }

    override suspend fun findById(id: UUID): AccountEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("accounts")
            .select {
                filter {
                    eq("workspace_id", wsId)
                    eq("id", id.toString())
                }
            }
            .decodeList<AccountEntity>()
            .firstOrNull()
    }

    override suspend fun findMaxOrderNum(): Double? {
        val wsId = workspaceId()
        val results = supabaseClient.postgrest.from("accounts")
            .select {
                filter { eq("workspace_id", wsId) }
                order("orderNum", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                limit(1)
            }
            .decodeList<AccountEntity>()
        return results.firstOrNull()?.orderNum
    }
}
