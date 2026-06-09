package com.ivy.data.supabase.dao

import com.ivy.data.db.dao.read.PlannedPaymentRuleDao
import com.ivy.data.db.dao.write.WritePlannedPaymentRuleDao
import com.ivy.data.db.entity.PlannedPaymentRuleEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import java.util.UUID
import javax.inject.Inject

class SupabasePlannedPaymentRuleDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), PlannedPaymentRuleDao {

    private val table = "planned_payment_rules"

    override suspend fun findAll(): List<PlannedPaymentRuleEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false) }
            order("amount", Order.DESCENDING)
            order("startDate", Order.ASCENDING)
        }.decodeList<PlannedPaymentRuleEntity>()
    }

    override suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean): List<PlannedPaymentRuleEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isSynced", synced); eq("isDeleted", deleted) }
        }.decodeList<PlannedPaymentRuleEntity>()
    }

    override suspend fun findAllByOneTime(oneTime: Boolean): List<PlannedPaymentRuleEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false); eq("oneTime", oneTime) }
            order("amount", Order.DESCENDING)
            order("startDate", Order.ASCENDING)
        }.decodeList<PlannedPaymentRuleEntity>()
    }

    override suspend fun findById(id: UUID): PlannedPaymentRuleEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("id", id.toString()); eq("isDeleted", false) }
        }.decodeList<PlannedPaymentRuleEntity>().firstOrNull()
    }

    override suspend fun countPlannedPayments(): Long {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false) }
        }.decodeList<PlannedPaymentRuleEntity>().size.toLong()
    }
}

class SupabaseWritePlannedPaymentRuleDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WritePlannedPaymentRuleDao {

    private val table = "planned_payment_rules"

    override suspend fun save(value: PlannedPaymentRuleEntity) {
        supabaseClient.postgrest.from(table).upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun saveMany(value: List<PlannedPaymentRuleEntity>) {
        if (value.isEmpty()) return
        supabaseClient.postgrest.from(table).upsert(toJsonListWithWorkspace(value, workspaceId()))
    }

    override suspend fun deletedByAccountId(accountId: UUID) {
        val wsId = workspaceId()
        supabaseClient.postgrest.from(table).update(
            mapOf("isDeleted" to true, "isSynced" to false)
        ) {
            filter { eq("workspace_id", wsId); eq("accountId", accountId.toString()) }
        }
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
