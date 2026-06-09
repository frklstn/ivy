package com.ivy.data.supabase

import io.supabase.SupabaseClient
import io.supabase.auth.auth
import io.supabase.postgrest.from
import io.supabase.postgrest.postgrest
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceResolver @Inject constructor(
    private val supabaseClient: SupabaseClient,
) {
    private var cachedWorkspaceId: UUID? = null
    private val mutex = Mutex()

    suspend fun resolveWorkspaceId(): UUID {
        cachedWorkspaceId?.let { return it }

        return mutex.withLock {
            cachedWorkspaceId?.let { return it }

            val userId = supabaseClient.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("User not authenticated")

            val profile = supabaseClient.postgrest.from("profiles")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<ProfileRow>()

            val workspaceId = UUID.fromString(profile.workspace_id)
            cachedWorkspaceId = workspaceId
            workspaceId
        }
    }

    fun clearCache() {
        cachedWorkspaceId = null
    }

    @Serializable
    private data class ProfileRow(
        val id: String,
        val workspace_id: String,
        val email: String? = null,
    )
}
