package com.ivy.data.supabase.dao

import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class SupabaseTransactionDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), TransactionDao {

    private val table = "transactions"

    private suspend fun queryAll(filter: suspend io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder.() -> Unit = {}): List<TransactionEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter {
                eq("workspace_id", wsId)
                eq("isDeleted", false)
                filter()
            }
            order("dateTime", Order.DESCENDING)
            order("dueDate", Order.ASCENDING)
        }.decodeList<TransactionEntity>()
    }

    override suspend fun findAll(): List<TransactionEntity> = queryAll()

    @Suppress("FunctionNaming")
    override suspend fun findAll_LIMIT_1(): List<TransactionEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false) }
            limit(1)
        }.decodeList<TransactionEntity>()
    }

    override suspend fun findAllByType(type: TransactionType): List<TransactionEntity> = queryAll {
        eq("type", type.name)
    }

    override suspend fun findAllByTypeAndAccount(type: TransactionType, accountId: UUID): List<TransactionEntity> = queryAll {
        eq("type", type.name); eq("accountId", accountId.toString())
    }

    override suspend fun findAllByTypeAndAccountBetween(
        type: TransactionType, accountId: UUID, startDate: Instant, endDate: Instant
    ): List<TransactionEntity> = queryAll {
        eq("type", type.name)
        eq("accountId", accountId.toString())
        gte("dateTime", startDate.toString())
        lte("dateTime", endDate.toString())
    }

    override suspend fun findAllTransfersToAccount(toAccountId: UUID, type: TransactionType): List<TransactionEntity> = queryAll {
        eq("type", type.name); eq("toAccountId", toAccountId.toString())
    }

    override suspend fun findAllTransfersToAccountBetween(
        toAccountId: UUID, startDate: Instant, endDate: Instant, type: TransactionType
    ): List<TransactionEntity> = queryAll {
        eq("type", type.name)
        eq("toAccountId", toAccountId.toString())
        gte("dateTime", startDate.toString())
        lte("dateTime", endDate.toString())
    }

    override suspend fun findAllBetween(startDate: Instant, endDate: Instant): List<TransactionEntity> = queryAll {
        gte("dateTime", startDate.toString()); lte("dateTime", endDate.toString())
    }

    override suspend fun findAllByAccountAndBetween(accountId: UUID, startDate: Instant, endDate: Instant): List<TransactionEntity> = queryAll {
        eq("accountId", accountId.toString())
        gte("dateTime", startDate.toString()); lte("dateTime", endDate.toString())
    }

    override suspend fun findAllByCategoryAndBetween(categoryId: UUID, startDate: Instant, endDate: Instant): List<TransactionEntity> = queryAll {
        eq("categoryId", categoryId.toString())
        gte("dateTime", startDate.toString()); lte("dateTime", endDate.toString())
    }

    override suspend fun findAllUnspecifiedAndBetween(startDate: Instant, endDate: Instant): List<TransactionEntity> = queryAll {
        filter("categoryId", FilterOperator.IS, "null")
        gte("dateTime", startDate.toString()); lte("dateTime", endDate.toString())
    }

    override suspend fun findAllByCategoryAndTypeAndBetween(
        categoryId: UUID, type: TransactionType, startDate: Instant, endDate: Instant
    ): List<TransactionEntity> = queryAll {
        eq("categoryId", categoryId.toString()); eq("type", type.name)
        gte("dateTime", startDate.toString()); lte("dateTime", endDate.toString())
    }

    override suspend fun findAllUnspecifiedAndTypeAndBetween(
        type: TransactionType, startDate: Instant, endDate: Instant
    ): List<TransactionEntity> = queryAll {
        filter("categoryId", FilterOperator.IS, "null"); eq("type", type.name)
        gte("dateTime", startDate.toString()); lte("dateTime", endDate.toString())
    }

    override suspend fun findAllToAccountAndBetween(toAccountId: UUID, startDate: Instant, endDate: Instant): List<TransactionEntity> = queryAll {
        eq("toAccountId", toAccountId.toString())
        gte("dateTime", startDate.toString()); lte("dateTime", endDate.toString())
    }

    override suspend fun findAllDueToBetween(startDate: Instant, endDate: Instant): List<TransactionEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter {
                eq("workspace_id", wsId); eq("isDeleted", false)
                gte("dueDate", startDate.toString()); lte("dueDate", endDate.toString())
            }
            order("dueDate", Order.ASCENDING)
        }.decodeList<TransactionEntity>()
    }

    override suspend fun findAllDueToBetweenByCategory(startDate: Instant, endDate: Instant, categoryId: UUID): List<TransactionEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter {
                eq("workspace_id", wsId); eq("isDeleted", false)
                eq("categoryId", categoryId.toString())
                gte("dueDate", startDate.toString()); lte("dueDate", endDate.toString())
            }
            order("dateTime", Order.DESCENDING); order("dueDate", Order.ASCENDING)
        }.decodeList<TransactionEntity>()
    }

    override suspend fun findAllDueToBetweenByCategoryUnspecified(startDate: Instant, endDate: Instant): List<TransactionEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter {
                eq("workspace_id", wsId); eq("isDeleted", false)
                filter("categoryId", FilterOperator.IS, "null")
                gte("dueDate", startDate.toString()); lte("dueDate", endDate.toString())
            }
            order("dateTime", Order.DESCENDING); order("dueDate", Order.ASCENDING)
        }.decodeList<TransactionEntity>()
    }

    override suspend fun findAllDueToBetweenByAccount(startDate: Instant, endDate: Instant, accountId: UUID): List<TransactionEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter {
                eq("workspace_id", wsId); eq("isDeleted", false)
                eq("accountId", accountId.toString())
                gte("dueDate", startDate.toString()); lte("dueDate", endDate.toString())
            }
            order("dateTime", Order.DESCENDING); order("dueDate", Order.ASCENDING)
        }.decodeList<TransactionEntity>()
    }

    override suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<TransactionEntity> = queryAll {
        eq("recurringRuleId", recurringRuleId.toString())
    }

    override suspend fun findAllBetweenAndType(startDate: Instant, endDate: Instant, type: TransactionType): List<TransactionEntity> = queryAll {
        gte("dateTime", startDate.toString()); lte("dateTime", endDate.toString()); eq("type", type.name)
    }

    override suspend fun findAllBetweenAndRecurringRuleId(startDate: Instant, endDate: Instant, recurringRuleId: UUID): List<TransactionEntity> = queryAll {
        gte("dateTime", startDate.toString()); lte("dateTime", endDate.toString())
        eq("recurringRuleId", recurringRuleId.toString())
    }

    override suspend fun findById(id: UUID): TransactionEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("id", id.toString()) }
        }.decodeList<TransactionEntity>().firstOrNull()
    }

    override suspend fun findByIds(ids: List<UUID>): List<TransactionEntity> {
        if (ids.isEmpty()) return emptyList()
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); isIn("id", ids.map { it.toString() }) }
        }.decodeList<TransactionEntity>()
    }

    override suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean): List<TransactionEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isSynced", synced); eq("isDeleted", deleted) }
        }.decodeList<TransactionEntity>()
    }

    override suspend fun countHappenedTransactions(): Long {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false); filterNot("dateTime", FilterOperator.IS, "null") }
        }.decodeList<TransactionEntity>().size.toLong()
    }

    override suspend fun findAllByTitleMatchingPattern(pattern: String): List<TransactionEntity> = queryAll {
        ilike("title", pattern)
    }

    override suspend fun countByTitleMatchingPattern(pattern: String): Long {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false); ilike("title", pattern) }
        }.decodeList<TransactionEntity>().size.toLong()
    }

    override suspend fun findAllByCategory(categoryId: UUID): List<TransactionEntity> = queryAll {
        eq("categoryId", categoryId.toString())
    }

    override suspend fun countByTitleMatchingPatternAndCategoryId(pattern: String, categoryId: UUID): Long {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false); ilike("title", pattern); eq("categoryId", categoryId.toString()) }
        }.decodeList<TransactionEntity>().size.toLong()
    }

    override suspend fun findAllByAccount(accountId: UUID): List<TransactionEntity> = queryAll {
        eq("accountId", accountId.toString())
    }

    override suspend fun countByTitleMatchingPatternAndAccountId(pattern: String, accountId: UUID): Long {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false); ilike("title", pattern); eq("accountId", accountId.toString()) }
        }.decodeList<TransactionEntity>().size.toLong()
    }

    override suspend fun findLoanTransaction(loanId: UUID): TransactionEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false); eq("loanId", loanId.toString()); filter("loanRecordId", FilterOperator.IS, "null") }
        }.decodeList<TransactionEntity>().firstOrNull()
    }

    override suspend fun findLoanRecordTransaction(loanRecordId: UUID): TransactionEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("isDeleted", false); eq("loanRecordId", loanRecordId.toString()) }
        }.decodeList<TransactionEntity>().firstOrNull()
    }

    override suspend fun findAllByLoanId(loanId: UUID): List<TransactionEntity> = queryAll {
        eq("loanId", loanId.toString())
    }
}

class SupabaseWriteTransactionDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteTransactionDao {

    private val table = "transactions"

    override suspend fun save(value: TransactionEntity) {
        supabaseClient.postgrest.from(table).upsert(toJsonWithWorkspace(value, workspaceId()))
    }

    override suspend fun saveMany(value: List<TransactionEntity>) {
        if (value.isEmpty()) return
        supabaseClient.postgrest.from(table).upsert(toJsonListWithWorkspace(value, workspaceId()))
    }

    override suspend fun deletedByRecurringRuleIdAndNoDateTime(recurringRuleId: UUID) {
        supabaseClient.postgrest.from(table).delete {
            filter {
                eq("workspace_id", workspaceId())
                eq("recurringRuleId", recurringRuleId.toString())
                filter("dateTime", FilterOperator.IS, "null")
            }
        }
    }

    override suspend fun deleteById(id: UUID) {
        supabaseClient.postgrest.from(table).delete {
            filter { eq("workspace_id", workspaceId()); eq("id", id.toString()) }
        }
    }

    override suspend fun deleteAllByAccountId(accountId: UUID) {
        supabaseClient.postgrest.from(table).delete {
            filter { eq("workspace_id", workspaceId()); eq("accountId", accountId.toString()) }
        }
    }

    override suspend fun deleteAll() {
        supabaseClient.postgrest.from(table).delete { filter { eq("workspace_id", workspaceId()) } }
    }
}
