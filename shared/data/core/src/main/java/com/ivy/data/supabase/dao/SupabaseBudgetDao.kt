package com.ivy.data.supabase.dao

import com.ivy.data.db.dao.read.BudgetDao
import com.ivy.data.db.dao.write.WriteBudgetDao
import com.ivy.data.db.entity.BudgetEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.supabase.SupabaseClient
import io.supabase.postgrest.from
import io.supabase.postgrest.postgrest
import io.supabase.postgrest.query.order.Order
import java.util.UUID
import javax.inject.Inject

class SupabaseBudgetDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), BudgetDao {

    override suspend fun findAll(): List<BudgetEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("budgets").select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false) }
            order("orderId", Order.ASCENDING)
        }.decodeList<BudgetEntity>()
    }

    override suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean): List<BudgetEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("budgets").select {
            filter { eq("workspace_id", wsId); eq("isSynced", synced); eq("isDeleted", deleted) }
        }.decodeList<BudgetEntity>()
    }

    override suspend fun findById(id: UUID): BudgetEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("budgets").select {
            filter { eq("workspace_id", wsId); eq("id", id.toString()) }
        }.decodeList<BudgetEntity>().firstOrNull()
    }

    override suspend fun findMaxOrderNum(): Double? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("budgets").select {
            filter { eq("workspace_id", wsId) }
            order("orderId", Order.DESCENDING)
            limit(1)
        }.decodeList<BudgetEntity>().firstOrNull()?.orderId
    }
}

class SupabaseWriteBudgetDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteBudgetDao {

    override suspend fun save(value: BudgetEntity) {
        supabaseClient.postgrest.from("budgets").upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun saveMany(value: List<BudgetEntity>) {
        if (value.isEmpty()) return
        supabaseClient.postgrest.from("budgets").upsert(toJsonListWithWorkspace(value, workspaceId()))
    }

    override suspend fun deleteById(id: UUID) {
        supabaseClient.postgrest.from("budgets").delete {
            filter { eq("workspace_id", workspaceId()); eq("id", id.toString()) }
        }
    }

    override suspend fun deleteAll() {
        supabaseClient.postgrest.from("budgets").delete { filter { eq("workspace_id", workspaceId()) } }
    }
}
