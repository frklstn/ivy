package com.ivy.data.di

import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.BudgetDao
import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.read.ExchangeRatesDao
import com.ivy.data.db.dao.read.LoanDao
import com.ivy.data.db.dao.read.LoanRecordDao
import com.ivy.data.db.dao.read.PlannedPaymentRuleDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.read.TagAssociationDao
import com.ivy.data.db.dao.read.TagDao
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.read.UserDao
import com.ivy.data.db.dao.read.LoanTrackerDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.db.dao.write.WriteBudgetDao
import com.ivy.data.db.dao.write.WriteCategoryDao
import com.ivy.data.db.dao.write.WriteExchangeRatesDao
import com.ivy.data.db.dao.write.WriteLoanDao
import com.ivy.data.db.dao.write.WriteLoanRecordDao
import com.ivy.data.db.dao.write.WritePlannedPaymentRuleDao
import com.ivy.data.db.dao.write.WriteSettingsDao
import com.ivy.data.db.dao.write.WriteTagAssociationDao
import com.ivy.data.db.dao.write.WriteTagDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.db.dao.write.WriteLoanTrackerDao
import com.ivy.data.supabase.WorkspaceResolver
import com.ivy.data.supabase.dao.SupabaseAccountDao
import com.ivy.data.supabase.dao.SupabaseBudgetDao
import com.ivy.data.supabase.dao.SupabaseCategoryDao
import com.ivy.data.supabase.dao.SupabaseExchangeRatesDao
import com.ivy.data.supabase.dao.SupabaseLoanDao
import com.ivy.data.supabase.dao.SupabaseLoanRecordDao
import com.ivy.data.supabase.dao.SupabasePlannedPaymentRuleDao
import com.ivy.data.supabase.dao.SupabaseSettingsDao
import com.ivy.data.supabase.dao.SupabaseTagAssociationDao
import com.ivy.data.supabase.dao.SupabaseTagDao
import com.ivy.data.supabase.dao.SupabaseTransactionDao
import com.ivy.data.supabase.dao.SupabaseUserDao
import com.ivy.data.supabase.dao.SupabaseLoanTrackerDao
import com.ivy.data.supabase.dao.SupabaseWriteAccountDao
import com.ivy.data.supabase.dao.SupabaseWriteBudgetDao
import com.ivy.data.supabase.dao.SupabaseWriteCategoryDao
import com.ivy.data.supabase.dao.SupabaseWriteExchangeRatesDao
import com.ivy.data.supabase.dao.SupabaseWriteLoanDao
import com.ivy.data.supabase.dao.SupabaseWriteLoanRecordDao
import com.ivy.data.supabase.dao.SupabaseWritePlannedPaymentRuleDao
import com.ivy.data.supabase.dao.SupabaseWriteSettingsDao
import com.ivy.data.supabase.dao.SupabaseWriteTagAssociationDao
import com.ivy.data.supabase.dao.SupabaseWriteTagDao
import com.ivy.data.supabase.dao.SupabaseWriteTransactionDao
import com.ivy.data.supabase.dao.SupabaseWriteLoanTrackerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SupabaseDaoModule {

    // ---- Read DAOs ----

    @Provides
    @Singleton
    @Named("supabase")
    fun provideAccountDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): AccountDao = SupabaseAccountDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideTransactionDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): TransactionDao = SupabaseTransactionDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideCategoryDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): CategoryDao = SupabaseCategoryDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideBudgetDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): BudgetDao = SupabaseBudgetDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideSettingsDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): SettingsDao = SupabaseSettingsDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideLoanDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): LoanDao = SupabaseLoanDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideLoanRecordDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): LoanRecordDao = SupabaseLoanRecordDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun providePlannedPaymentRuleDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): PlannedPaymentRuleDao = SupabasePlannedPaymentRuleDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideTagDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): TagDao = SupabaseTagDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideTagAssociationDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): TagAssociationDao = SupabaseTagAssociationDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideExchangeRatesDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): ExchangeRatesDao = SupabaseExchangeRatesDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideUserDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): UserDao = SupabaseUserDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideLoanTrackerDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): LoanTrackerDao = SupabaseLoanTrackerDao(client, ws)

    // ---- Write DAOs ----

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteAccountDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): WriteAccountDao = SupabaseWriteAccountDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteTransactionDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): WriteTransactionDao = SupabaseWriteTransactionDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteCategoryDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): WriteCategoryDao = SupabaseWriteCategoryDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteBudgetDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): WriteBudgetDao = SupabaseWriteBudgetDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteSettingsDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): WriteSettingsDao = SupabaseWriteSettingsDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteLoanDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): WriteLoanDao = SupabaseWriteLoanDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteLoanRecordDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): WriteLoanRecordDao = SupabaseWriteLoanRecordDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWritePlannedPaymentRuleDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): WritePlannedPaymentRuleDao = SupabaseWritePlannedPaymentRuleDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteExchangeRatesDao(
        client: SupabaseClient, ws: WorkspaceResolver, @Named("supabase") readDao: SupabaseExchangeRatesDao
    ): WriteExchangeRatesDao = SupabaseWriteExchangeRatesDao(client, ws, readDao)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteTagDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): WriteTagDao = SupabaseWriteTagDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteTagAssociationDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): TagAssociationDao = SupabaseTagAssociationDao(client, ws)

    @Provides
    @Singleton
    @Named("supabase")
    fun provideWriteLoanTrackerDao(
        client: SupabaseClient, ws: WorkspaceResolver
    ): WriteLoanTrackerDao = SupabaseWriteLoanTrackerDao(client, ws)
}
