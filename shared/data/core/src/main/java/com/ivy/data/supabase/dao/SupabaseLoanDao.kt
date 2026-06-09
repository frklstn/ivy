package com.ivy.data.supabase.dao

import com.ivy.data.db.dao.read.LoanDao
import com.ivy.data.db.dao.read.LoanRecordDao
import com.ivy.data.db.dao.write.WriteLoanDao
import com.ivy.data.db.dao.write.WriteLoanRecordDao
import com.ivy.data.db.entity.LoanEntity
import com.ivy.data.db.entity.LoanRecordEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import java.util.UUID
import javax.inject.Inject

class SupabaseLoanDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), LoanDao {

    override suspend fun findAll(): List<LoanEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("loans").select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false) }
            order("orderNum", Order.ASCENDING)
        }.decodeList<LoanEntity>()
    }

    override suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean): List<LoanEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("loans").select {
            filter { eq("workspace_id", wsId); eq("isSynced", synced); eq("isDeleted", deleted) }
        }.decodeList<LoanEntity>()
    }

    override suspend fun findById(id: UUID): LoanEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("loans").select {
            filter { eq("workspace_id", wsId); eq("id", id.toString()) }
        }.decodeList<LoanEntity>().firstOrNull()
    }

    override suspend fun findMaxOrderNum(): Double? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("loans").select {
            filter { eq("workspace_id", wsId) }
            order("orderNum", Order.DESCENDING)
            limit(1)
        }.decodeList<LoanEntity>().firstOrNull()?.orderNum
    }
}

class SupabaseWriteLoanDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteLoanDao {

    override suspend fun save(value: LoanEntity) {
        supabaseClient.postgrest.from("loans").upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun saveMany(value: List<LoanEntity>) {
        if (value.isEmpty()) return
        supabaseClient.postgrest.from("loans").upsert(toJsonListWithWorkspace(value, workspaceId()))
    }

    override suspend fun deleteById(id: UUID) {
        supabaseClient.postgrest.from("loans").delete {
            filter { eq("workspace_id", workspaceId()); eq("id", id.toString()) }
        }
    }

    override suspend fun deleteAll() {
        supabaseClient.postgrest.from("loans").delete { filter { eq("workspace_id", workspaceId()) } }
    }
}

class SupabaseLoanRecordDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), LoanRecordDao {

    override suspend fun findAll(): List<LoanRecordEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("loan_records").select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false) }
            order("dateTime", Order.DESCENDING)
        }.decodeList<LoanRecordEntity>()
    }

    override suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean): List<LoanRecordEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("loan_records").select {
            filter { eq("workspace_id", wsId); eq("isSynced", synced); eq("isDeleted", deleted) }
        }.decodeList<LoanRecordEntity>()
    }

    override suspend fun findById(id: UUID): LoanRecordEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("loan_records").select {
            filter { eq("workspace_id", wsId); eq("id", id.toString()) }
        }.decodeList<LoanRecordEntity>().firstOrNull()
    }

    override suspend fun findAllByLoanId(loanId: UUID): List<LoanRecordEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("loan_records").select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false); eq("loanId", loanId.toString()) }
            order("dateTime", Order.DESCENDING)
        }.decodeList<LoanRecordEntity>()
    }
}

class SupabaseWriteLoanRecordDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteLoanRecordDao {

    override suspend fun save(value: LoanRecordEntity) {
        supabaseClient.postgrest.from("loan_records").upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun saveMany(value: List<LoanRecordEntity>) {
        if (value.isEmpty()) return
        supabaseClient.postgrest.from("loan_records").upsert(toJsonListWithWorkspace(value, workspaceId()))
    }

    override suspend fun deleteById(id: UUID) {
        supabaseClient.postgrest.from("loan_records").delete {
            filter { eq("workspace_id", workspaceId()); eq("id", id.toString()) }
        }
    }

    override suspend fun deleteAll() {
        supabaseClient.postgrest.from("loan_records").delete { filter { eq("workspace_id", workspaceId()) } }
    }
}
