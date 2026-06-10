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

    @Provides @Singleton
    fun provideAccountDao(client: SupabaseClient, ws: WorkspaceResolver): AccountDao =
        SupabaseAccountDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideAccountDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): AccountDao =
        SupabaseAccountDao(client, ws)

    @Provides @Singleton
    fun provideTransactionDao(client: SupabaseClient, ws: WorkspaceResolver): TransactionDao =
        SupabaseTransactionDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideTransactionDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): TransactionDao =
        SupabaseTransactionDao(client, ws)

    @Provides @Singleton
    fun provideCategoryDao(client: SupabaseClient, ws: WorkspaceResolver): CategoryDao =
        SupabaseCategoryDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideCategoryDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): CategoryDao =
        SupabaseCategoryDao(client, ws)

    @Provides @Singleton
    fun provideBudgetDao(client: SupabaseClient, ws: WorkspaceResolver): BudgetDao =
        SupabaseBudgetDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideBudgetDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): BudgetDao =
        SupabaseBudgetDao(client, ws)

    @Provides @Singleton
    fun provideSettingsDao(client: SupabaseClient, ws: WorkspaceResolver): SettingsDao =
        SupabaseSettingsDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideSettingsDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): SettingsDao =
        SupabaseSettingsDao(client, ws)

    @Provides @Singleton
    fun provideLoanDao(client: SupabaseClient, ws: WorkspaceResolver): LoanDao =
        SupabaseLoanDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideLoanDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): LoanDao =
        SupabaseLoanDao(client, ws)

    @Provides @Singleton
    fun provideLoanRecordDao(client: SupabaseClient, ws: WorkspaceResolver): LoanRecordDao =
        SupabaseLoanRecordDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideLoanRecordDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): LoanRecordDao =
        SupabaseLoanRecordDao(client, ws)

    @Provides @Singleton
    fun providePlannedPaymentRuleDao(client: SupabaseClient, ws: WorkspaceResolver): PlannedPaymentRuleDao =
        SupabasePlannedPaymentRuleDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun providePlannedPaymentRuleDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): PlannedPaymentRuleDao =
        SupabasePlannedPaymentRuleDao(client, ws)

    @Provides @Singleton
    fun provideTagDao(client: SupabaseClient, ws: WorkspaceResolver): TagDao =
        SupabaseTagDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideTagDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): TagDao =
        SupabaseTagDao(client, ws)

    @Provides @Singleton
    fun provideTagAssociationDao(client: SupabaseClient, ws: WorkspaceResolver): TagAssociationDao =
        SupabaseTagAssociationDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideTagAssociationDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): TagAssociationDao =
        SupabaseTagAssociationDao(client, ws)

    @Provides @Singleton
    fun provideExchangeRatesDao(client: SupabaseClient, ws: WorkspaceResolver): ExchangeRatesDao =
        SupabaseExchangeRatesDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideExchangeRatesDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): ExchangeRatesDao =
        SupabaseExchangeRatesDao(client, ws)

    @Provides @Singleton
    fun provideLoanTrackerDao(client: SupabaseClient, ws: WorkspaceResolver): LoanTrackerDao =
        SupabaseLoanTrackerDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideLoanTrackerDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): LoanTrackerDao =
        SupabaseLoanTrackerDao(client, ws)

    @Provides @Singleton
    fun provideUserDao(client: SupabaseClient, ws: WorkspaceResolver): UserDao =
        SupabaseUserDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideUserDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): UserDao =
        SupabaseUserDao(client, ws)

    // ---- Write DAOs ----

    @Provides @Singleton
    fun provideWriteAccountDao(client: SupabaseClient, ws: WorkspaceResolver): WriteAccountDao =
        SupabaseWriteAccountDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWriteAccountDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WriteAccountDao =
        SupabaseWriteAccountDao(client, ws)

    @Provides @Singleton
    fun provideWriteTransactionDao(client: SupabaseClient, ws: WorkspaceResolver): WriteTransactionDao =
        SupabaseWriteTransactionDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWriteTransactionDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WriteTransactionDao =
        SupabaseWriteTransactionDao(client, ws)

    @Provides @Singleton
    fun provideWriteCategoryDao(client: SupabaseClient, ws: WorkspaceResolver): WriteCategoryDao =
        SupabaseWriteCategoryDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWriteCategoryDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WriteCategoryDao =
        SupabaseWriteCategoryDao(client, ws)

    @Provides @Singleton
    fun provideWriteBudgetDao(client: SupabaseClient, ws: WorkspaceResolver): WriteBudgetDao =
        SupabaseWriteBudgetDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWriteBudgetDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WriteBudgetDao =
        SupabaseWriteBudgetDao(client, ws)

    @Provides @Singleton
    fun provideWriteSettingsDao(client: SupabaseClient, ws: WorkspaceResolver): WriteSettingsDao =
        SupabaseWriteSettingsDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWriteSettingsDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WriteSettingsDao =
        SupabaseWriteSettingsDao(client, ws)

    @Provides @Singleton
    fun provideWriteLoanDao(client: SupabaseClient, ws: WorkspaceResolver): WriteLoanDao =
        SupabaseWriteLoanDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWriteLoanDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WriteLoanDao =
        SupabaseWriteLoanDao(client, ws)

    @Provides @Singleton
    fun provideWriteLoanRecordDao(client: SupabaseClient, ws: WorkspaceResolver): WriteLoanRecordDao =
        SupabaseWriteLoanRecordDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWriteLoanRecordDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WriteLoanRecordDao =
        SupabaseWriteLoanRecordDao(client, ws)

    @Provides @Singleton
    fun provideWritePlannedPaymentRuleDao(client: SupabaseClient, ws: WorkspaceResolver): WritePlannedPaymentRuleDao =
        SupabaseWritePlannedPaymentRuleDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWritePlannedPaymentRuleDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WritePlannedPaymentRuleDao =
        SupabaseWritePlannedPaymentRuleDao(client, ws)

    @Provides @Singleton
    fun provideWriteExchangeRatesDao(
        client: SupabaseClient, ws: WorkspaceResolver, readDao: ExchangeRatesDao
    ): WriteExchangeRatesDao = SupabaseWriteExchangeRatesDao(client, ws, readDao as SupabaseExchangeRatesDao)

    @Provides @Singleton @Named("supabase")
    fun provideWriteExchangeRatesDaoNamed(
        client: SupabaseClient, ws: WorkspaceResolver, @Named("supabase") readDao: ExchangeRatesDao
    ): WriteExchangeRatesDao = SupabaseWriteExchangeRatesDao(client, ws, readDao as SupabaseExchangeRatesDao)

    @Provides @Singleton
    fun provideWriteTagDao(client: SupabaseClient, ws: WorkspaceResolver): WriteTagDao =
        SupabaseWriteTagDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWriteTagDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WriteTagDao =
        SupabaseWriteTagDao(client, ws)

    @Provides @Singleton
    fun provideWriteTagAssociationDao(client: SupabaseClient, ws: WorkspaceResolver): WriteTagAssociationDao =
        SupabaseWriteTagAssociationDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWriteTagAssociationDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WriteTagAssociationDao =
        SupabaseWriteTagAssociationDao(client, ws)

    @Provides @Singleton
    fun provideWriteLoanTrackerDao(client: SupabaseClient, ws: WorkspaceResolver): WriteLoanTrackerDao =
        SupabaseWriteLoanTrackerDao(client, ws)

    @Provides @Singleton @Named("supabase")
    fun provideWriteLoanTrackerDaoNamed(client: SupabaseClient, ws: WorkspaceResolver): WriteLoanTrackerDao =
        SupabaseWriteLoanTrackerDao(client, ws)
}
