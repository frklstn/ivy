package com.ivy.data.supabase

import io.supabase.SupabaseClient
import io.supabase.auth.auth
import io.supabase.auth.providers.Email
import io.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(
    private val supabaseClient: SupabaseClient,
) {

    val authState: Flow<AuthState> = supabaseClient.auth.sessionFlow.map { session ->
        if (session != null) {
            AuthState.Authenticated(session.accessToken)
        } else {
            AuthState.Unauthenticated
        }
    }

    val currentUser: UserInfo?
        get() = supabaseClient.auth.currentUserOrNull()

    val isAuthenticated: Boolean
        get() = supabaseClient.auth.currentSessionOrNull() != null

    suspend fun login(email: String, password: String): Result<Unit> = runCatching {
        supabaseClient.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun register(email: String, password: String): Result<Unit> = runCatching {
        supabaseClient.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun logout(): Result<Unit> = runCatching {
        supabaseClient.auth.signOut()
    }
}

sealed interface AuthState {
    data object Unauthenticated : AuthState
    data class Authenticated(val accessToken: String) : AuthState
}
