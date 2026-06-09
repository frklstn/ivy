package com.ivy.data.supabase.dao

import com.ivy.data.db.dao.read.LoanTrackerDao
import com.ivy.data.db.dao.write.WriteLoanTrackerDao
import com.ivy.data.db.entity.LoanTrackerEntity
import com.ivy.data.db.entity.DebtPlannerSettingsEntity
import com.ivy.data.db.entity.IncomeTimelineEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import java.util.UUID
import javax.inject.Inject

class SupabaseLoanTrackerDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), LoanTrackerDao {

    override suspend fun findAll(): List<LoanTrackerEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("loan_trackers").select {
            filter { eq("workspace_id", wsId) }
            order("start_date", Order.ASCENDING)
        }.decodeList<LoanTrackerEntity>()
    }

    override suspend fun findById(id: UUID): LoanTrackerEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("loan_trackers").select {
            filter { eq("workspace_id", wsId); eq("id", id.toString()) }
        }.decodeList<LoanTrackerEntity>().firstOrNull()
    }

    override suspend fun getSettings(): DebtPlannerSettingsEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("debt_planner_settings").select {
            filter { eq("workspace_id", wsId) }
        }.decodeList<DebtPlannerSettingsEntity>().firstOrNull()
    }

    override suspend fun getIncomeTimeline(): List<IncomeTimelineEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from("income_timeline").select {
            filter { eq("workspace_id", wsId) }
            order("effective_date", Order.ASCENDING)
        }.decodeList<IncomeTimelineEntity>()
    }
}

class SupabaseWriteLoanTrackerDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteLoanTrackerDao {

    override suspend fun saveLoan(value: LoanTrackerEntity) {
        supabaseClient.postgrest.from("loan_trackers").upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun saveSettings(value: DebtPlannerSettingsEntity) {
        supabaseClient.postgrest.from("debt_planner_settings").upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun saveIncome(value: IncomeTimelineEntity) {
        supabaseClient.postgrest.from("income_timeline").upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun deleteLoanById(id: UUID) {
        supabaseClient.postgrest.from("loan_trackers").delete {
            filter { eq("workspace_id", workspaceId()); eq("id", id.toString()) }
        }
    }

    override suspend fun deleteIncomeById(id: UUID) {
        supabaseClient.postgrest.from("income_timeline").delete {
            filter { eq("workspace_id", workspaceId()); eq("id", id.toString()) }
        }
    }

    override suspend fun deleteSettings() {
        supabaseClient.postgrest.from("debt_planner_settings").delete {
            filter { eq("workspace_id", workspaceId()) }
        }
    }

    override suspend fun deleteAllLoans() {
        supabaseClient.postgrest.from("loan_trackers").delete {
            filter { eq("workspace_id", workspaceId()) }
        }
    }
}
