package com.ivy.data.db.dao.read

import com.ivy.base.model.TransactionType
import com.ivy.data.db.entity.TransactionEntity
import java.time.Instant
import java.util.UUID

interface TransactionDao {
    suspend fun findAll(): List<TransactionEntity>

    @Suppress("FunctionNaming")
    @Deprecated("legacy remove")
    suspend fun findAll_LIMIT_1(): List<TransactionEntity>

    suspend fun findAllByType(type: TransactionType): List<TransactionEntity>
    suspend fun findAllByTypeAndAccount(type: TransactionType, accountId: UUID): List<TransactionEntity>
    suspend fun findAllByTypeAndAccountBetween(
        type: TransactionType, accountId: UUID, startDate: Instant, endDate: Instant
    ): List<TransactionEntity>

    suspend fun findAllTransfersToAccount(
        toAccountId: UUID, type: TransactionType = TransactionType.TRANSFER
    ): List<TransactionEntity>

    suspend fun findAllTransfersToAccountBetween(
        toAccountId: UUID, startDate: Instant, endDate: Instant, type: TransactionType = TransactionType.TRANSFER
    ): List<TransactionEntity>

    suspend fun findAllBetween(startDate: Instant, endDate: Instant): List<TransactionEntity>
    suspend fun findAllByAccountAndBetween(accountId: UUID, startDate: Instant, endDate: Instant): List<TransactionEntity>
    suspend fun findAllByCategoryAndBetween(categoryId: UUID, startDate: Instant, endDate: Instant): List<TransactionEntity>
    suspend fun findAllUnspecifiedAndBetween(startDate: Instant, endDate: Instant): List<TransactionEntity>
    suspend fun findAllByCategoryAndTypeAndBetween(
        categoryId: UUID, type: TransactionType, startDate: Instant, endDate: Instant
    ): List<TransactionEntity>

    suspend fun findAllUnspecifiedAndTypeAndBetween(
        type: TransactionType, startDate: Instant, endDate: Instant
    ): List<TransactionEntity>

    suspend fun findAllToAccountAndBetween(toAccountId: UUID, startDate: Instant, endDate: Instant): List<TransactionEntity>
    suspend fun findAllDueToBetween(startDate: Instant, endDate: Instant): List<TransactionEntity>
    suspend fun findAllDueToBetweenByCategory(startDate: Instant, endDate: Instant, categoryId: UUID): List<TransactionEntity>
    suspend fun findAllDueToBetweenByCategoryUnspecified(startDate: Instant, endDate: Instant): List<TransactionEntity>
    suspend fun findAllDueToBetweenByAccount(startDate: Instant, endDate: Instant, accountId: UUID): List<TransactionEntity>
    suspend fun findAllByRecurringRuleId(recurringRuleId: UUID): List<TransactionEntity>
    suspend fun findAllBetweenAndType(startDate: Instant, endDate: Instant, type: TransactionType): List<TransactionEntity>
    suspend fun findAllBetweenAndRecurringRuleId(startDate: Instant, endDate: Instant, recurringRuleId: UUID): List<TransactionEntity>
    suspend fun findById(id: UUID): TransactionEntity?
    suspend fun findByIds(ids: List<UUID>): List<TransactionEntity>
    suspend fun findByIsSyncedAndIsDeleted(synced: Boolean, deleted: Boolean = false): List<TransactionEntity>
    suspend fun countHappenedTransactions(): Long
    suspend fun findAllByTitleMatchingPattern(pattern: String): List<TransactionEntity>
    suspend fun countByTitleMatchingPattern(pattern: String): Long
    suspend fun findAllByCategory(categoryId: UUID): List<TransactionEntity>
    suspend fun countByTitleMatchingPatternAndCategoryId(pattern: String, categoryId: UUID): Long
    suspend fun findAllByAccount(accountId: UUID): List<TransactionEntity>
    suspend fun countByTitleMatchingPatternAndAccountId(pattern: String, accountId: UUID): Long
    suspend fun findLoanTransaction(loanId: UUID): TransactionEntity?
    suspend fun findLoanRecordTransaction(loanRecordId: UUID): TransactionEntity?
    suspend fun findAllByLoanId(loanId: UUID): List<TransactionEntity>
}
