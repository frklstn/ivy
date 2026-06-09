package com.ivy.data.supabase.dao

import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.db.entity.AccountEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.supabase.SupabaseClient
import io.supabase.postgrest.from
import io.supabase.postgrest.postgrest
import java.util.UUID
import javax.inject.Inject

class SupabaseWriteAccountDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteAccountDao {

    override suspend fun save(value: AccountEntity) {
        val wsId = workspaceId()
        val payload = toJsonWithWorkspace(value, wsId)
        supabaseClient.postgrest.from("accounts").upsert(payload)
    }

    override suspend fun saveMany(values: List<AccountEntity>) {
        if (values.isEmpty()) return
        val wsId = workspaceId()
        val payloads = toJsonListWithWorkspace(values, wsId)
        supabaseClient.postgrest.from("accounts").upsert(payloads)
    }

    override suspend fun deleteById(id: UUID) {
        val wsId = workspaceId()
        supabaseClient.postgrest.from("accounts").delete {
            filter {
                eq("workspace_id", wsId)
                eq("id", id.toString())
            }
        }
    }

    override suspend fun deleteAll() {
        val wsId = workspaceId()
        supabaseClient.postgrest.from("accounts").delete {
            filter { eq("workspace_id", wsId) }
        }
    }
}
