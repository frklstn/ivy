package com.ivy.data.supabase.dao

import com.ivy.data.db.dao.read.ExchangeRatesDao
import com.ivy.data.db.dao.read.UserDao
import com.ivy.data.db.dao.write.WriteExchangeRatesDao
import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.data.db.entity.UserEntity
import com.ivy.data.supabase.WorkspaceResolver
import io.supabase.SupabaseClient
import io.supabase.postgrest.from
import io.supabase.postgrest.postgrest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseExchangeRatesDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), ExchangeRatesDao {

    private val table = "exchange_rates"
    private val _cache = MutableStateFlow<List<ExchangeRateEntity>>(emptyList())
    private var initialized = false

    override fun findAll(): Flow<List<ExchangeRateEntity>> {
        if (!initialized) {
            initialized = true
            // Lazy init: refresh on first access
            MainScope().launch { refreshCache() }
        }
        return _cache.asStateFlow()
    }

    override suspend fun findAllManuallyOverridden(): List<ExchangeRateEntity> {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("manualOverride", true) }
        }.decodeList<ExchangeRateEntity>()
    }

    override suspend fun findByBaseCurrencyAndCurrency(
        baseCurrency: String,
        currency: String
    ): ExchangeRateEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter {
                eq("workspace_id", wsId)
                eq("baseCurrency", baseCurrency)
                eq("currency", currency)
            }
        }.decodeList<ExchangeRateEntity>().firstOrNull()
    }

    internal suspend fun refreshCache() {
        try {
            val wsId = workspaceId()
            _cache.value = supabaseClient.postgrest.from(table).select {
                filter { eq("workspace_id", wsId) }
            }.decodeList<ExchangeRateEntity>()
        } catch (_: Exception) {
            // Keep existing cache on error
        }
    }
}

class SupabaseWriteExchangeRatesDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
    private val readDao: SupabaseExchangeRatesDao,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), WriteExchangeRatesDao {

    private val table = "exchange_rates"

    override suspend fun save(value: ExchangeRateEntity) {
        supabaseClient.postgrest.from(table).upsert(toJsonWithWorkspace(value, workspaceId()))
        readDao.refreshCache()
    }

    override suspend fun saveMany(value: List<ExchangeRateEntity>) {
        if (value.isEmpty()) return
        supabaseClient.postgrest.from(table).upsert(toJsonListWithWorkspace(value, workspaceId()))
        readDao.refreshCache()
    }

    override suspend fun deleteByBaseCurrencyAndCurrency(baseCurrency: String, currency: String) {
        supabaseClient.postgrest.from(table).delete {
            filter {
                eq("workspace_id", workspaceId())
                eq("baseCurrency", baseCurrency)
                eq("currency", currency)
            }
        }
        readDao.refreshCache()
    }

    override suspend fun deleteAll() {
        supabaseClient.postgrest.from(table).delete { filter { eq("workspace_id", workspaceId()) } }
        readDao.refreshCache()
    }
}

@Suppress("DEPRECATION")
class SupabaseUserDao @Inject constructor(
    supabaseClient: SupabaseClient,
    workspaceResolver: WorkspaceResolver,
) : SupabaseBaseDao(supabaseClient, workspaceResolver), UserDao {

    private val table = "users"

    override suspend fun save(user: UserEntity) {
        supabaseClient.postgrest.from(table).upsert(toJsonWithWorkspace(user, workspaceId()))
    }

    override suspend fun findById(userId: UUID): UserEntity? {
        val wsId = workspaceId()
        return supabaseClient.postgrest.from(table).select {
            filter { eq("workspace_id", wsId); eq("id", userId.toString()) }
        }.decodeList<UserEntity>().firstOrNull()
    }

    override suspend fun deleteAll() {
        supabaseClient.postgrest.from(table).delete { filter { eq("workspace_id", workspaceId()) } }
    }
}
