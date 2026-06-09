package com.ivy.data.supabase.dao

import com.ivy.data.supabase.WorkspaceResolver
import io.supabase.SupabaseClient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.Json

/**
 * Base class for Supabase DAO implementations.
 * Provides common helpers for workspace-scoped CRUD operations.
 */
abstract class SupabaseBaseDao(
    protected val supabaseClient: SupabaseClient,
    protected val workspaceResolver: WorkspaceResolver,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    protected suspend fun workspaceId(): String =
        workspaceResolver.resolveWorkspaceId().toString()

    /**
     * Serializes an entity to JsonObject and adds workspace_id field.
     */
    protected inline fun <reified T> toJsonWithWorkspace(entity: T, workspaceId: String): JsonObject {
        val element = Json.encodeToJsonElement(entity)
        val map = element.jsonObject.toMutableMap()
        map["workspace_id"] = JsonPrimitive(workspaceId)
        return JsonObject(map)
    }

    /**
     * Serializes a list of entities to JsonObjects with workspace_id.
     */
    protected inline fun <reified T> toJsonListWithWorkspace(
        entities: List<T>,
        workspaceId: String
    ): List<JsonObject> {
        return entities.map { toJsonWithWorkspace(it, workspaceId) }
    }
}
